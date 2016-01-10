package crazypants.enderzoo.entity;

import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.ai.EntityAIMountedArrowAttack;
import crazypants.enderzoo.entity.ai.EntityAIMountedAttackOnCollide;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityFallenKnight extends EntitySkeleton implements IEnderZooMob {

  public static final int EGG_FG_COL = 0x365A25;
  public static final int EGG_BG_COL = 0xA0A0A0;

  public static String NAME = "enderzoo.FallenKnight";

  private static final double ATTACK_MOVE_SPEED = Config.fallenKnightChargeSpeed;

  private EntityAIMountedArrowAttack aiArrowAttack;
  private EntityAIMountedAttackOnCollide aiAttackOnCollide;

  private final EntityAIBreakDoor breakDoorAI = new EntityAIBreakDoor(this);
  private boolean canBreakDoors = false;

  private EntityLivingBase lastAttackTarget = null;

  private boolean firstUpdate = true;
  private boolean isMounted = false;

  private boolean spawned = false;

  public EntityFallenKnight(World world) {
    super(world);
    targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, false));
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(Config.fallenKnightFollowRange);
    MobInfo.FALLEN_KNIGHT.applyAttributes(this);
  }

//  private float getAttackRange() {
//    if(isRiding()) {
//      return 3;
//    }
//    return 2;
//  }

  @Override
  public void setCombatTask() {
    tasks.removeTask(getAiAttackOnCollide());
    tasks.removeTask(getAiArrowAttack());
    if(isRanged()) {
      tasks.addTask(4, getAiArrowAttack());
    } else {
      tasks.addTask(4, getAiAttackOnCollide());
    }
  }

  public EntityAIMountedArrowAttack getAiArrowAttack() {
    if(aiArrowAttack == null) {
      aiArrowAttack = new EntityAIMountedArrowAttack(this, ATTACK_MOVE_SPEED, EntityFallenMount.MOUNTED_ATTACK_MOVE_SPEED,
          Config.fallenKnightRangedMinAttackPause, Config.fallenKnightRangedMaxAttackPause, Config.fallenKnightRangedMaxRange,
          Config.fallKnightMountedArchesMaintainDistance);
    }
    return aiArrowAttack;
  }

  public EntityAIMountedAttackOnCollide getAiAttackOnCollide() {
    if(aiAttackOnCollide == null) {
      aiAttackOnCollide = new EntityAIMountedAttackOnCollide(this, EntityPlayer.class, ATTACK_MOVE_SPEED, EntityFallenMount.MOUNTED_ATTACK_MOVE_SPEED, false);
    }
    return aiAttackOnCollide;
  }

  @Override
  protected String getLivingSound() {
    return "mob.zombie.say";
  }

  @Override
  protected String getHurtSound() {
    return "mob.zombie.hurt";
  }

  @Override
  protected String getDeathSound() {
    return "mob.zombie.death";
  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();

    if(firstUpdate && !worldObj.isRemote) {
      spawnMount();
    }

    if(isRidingMount()) {
      EntityLiving entLiving = ((EntityLiving) ridingEntity);
      if(lastAttackTarget != getAttackTarget() || firstUpdate) {
        EntityUtil.cancelCurrentTasks(entLiving);
        lastAttackTarget = getAttackTarget();
      }
    }
    firstUpdate = false;

    if(!isMounted == isRidingMount()) {
      getAiAttackOnCollide().resetTask();
      getAiArrowAttack().resetTask();
      getNavigator().clearPathEntity();
      isMounted = isRidingMount();
    }
    if(isBurning() && isRidingMount()) {
      ridingEntity.setFire(8);
    }
    if(Config.fallenKnightArchersSwitchToMelee && (!isMounted || !Config.fallKnightMountedArchesMaintainDistance)
        && getAttackTarget() != null && isRanged() && getDistanceSqToEntity(getAttackTarget()) < 5) {
      setCurrentItemOrArmor(0, getSwordForLevel(getRandomEquipmentLevel()));
    }
  }

  private boolean isRidingMount() {
    return isRiding() && ridingEntity.getClass() == EntityFallenMount.class;
  }

  @Override
  protected void despawnEntity() {
    Entity mount = ridingEntity;
    super.despawnEntity();
    if(isDead && mount != null) {
      mount.setDead();
    }
  }

  private void spawnMount() {
    if(ridingEntity != null || !spawned) {
      return;
    }

    EntityFallenMount mount = null;
    if(Config.fallenMountEnabled && rand.nextFloat() <= Config.fallenKnightChanceMounted) {
      mount = new EntityFallenMount(worldObj);
      mount.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);

      DifficultyInstance di = worldObj.getDifficultyForLocation(new BlockPos(mount));
      mount.onInitialSpawn(di, null);
      //NB: don;t check for entity collisions as we know the knight will collide
      if(!SpawnUtil.isSpaceAvailableForSpawn(worldObj, mount, false)) {
        mount = null;
      }
    }
    if(mount != null) {
      setCanPickUpLoot(false);
      setCanBreakDoors(false);
      worldObj.spawnEntityInWorld(mount);
      mountEntity(mount);
    }
  }

  private boolean isRanged() {
    ItemStack itemstack = getHeldItem();
    return itemstack != null && itemstack.getItem() == Items.bow;
  }

  private void addRandomArmor() {

    float occupiedDiffcultyMultiplier = EntityUtil.getDifficultyMultiplierForLocation(worldObj, posX, posY, posZ);

    int equipmentLevel = getRandomEquipmentLevel(occupiedDiffcultyMultiplier);
    int armorLevel = equipmentLevel;
    if(armorLevel == 1) {
      //Skip gold armor, I don't like it
      armorLevel++;
    }

    float chancePerPiece = isHardDifficulty() ? Config.fallenKnightChancePerArmorPieceHard
        : Config.fallenKnightChancePerArmorPiece;
    chancePerPiece *= (1 + occupiedDiffcultyMultiplier); //If we have the max occupied factor, double the chance of improved armor

    for (int slot = 1; slot < 5; slot++) {
      ItemStack itemStack = getEquipmentInSlot(slot);
      if(itemStack == null && rand.nextFloat() <= chancePerPiece) {
        Item item = EntityLiving.getArmorItemForSlot(slot, armorLevel);
        if(item != null) {
          ItemStack stack = new ItemStack(item);
          if(armorLevel == 0) {
            ((ItemArmor) item).setColor(stack, 0);
          }
          setCurrentItemOrArmor(slot, stack);
        }
      }
    }
    if(rand.nextFloat() > Config.fallenKnightRangedRatio) {
      setCurrentItemOrArmor(0, getSwordForLevel(equipmentLevel));
    } else {
      setCurrentItemOrArmor(0, new ItemStack(Items.bow));
    }
  }

  private int getRandomEquipmentLevel() {
    return getRandomEquipmentLevel(EntityUtil.getDifficultyMultiplierForLocation(worldObj, posX, posY, posZ));
  }

  private int getRandomEquipmentLevel(float occupiedDiffcultyMultiplier) {
    float chanceImprovedArmor = isHardDifficulty() ? Config.fallenKnightChanceArmorUpgradeHard
        : Config.fallenKnightChanceArmorUpgrade;
    chanceImprovedArmor *= (1 + occupiedDiffcultyMultiplier); //If we have the max occupied factor, double the chance of improved armor   

    int armorLevel = rand.nextInt(2);
    for (int i = 0; i < 2; i++) {
      if(rand.nextFloat() <= chanceImprovedArmor) {
        armorLevel++;
      }
    }
    return armorLevel;
  }

  protected boolean isHardDifficulty() {
    return EntityUtil.isHardDifficulty(worldObj);
  }

  private ItemStack getSwordForLevel(int swordLevel) {
    ////have a better chance of not getting a wooden or stone sword
    if(swordLevel < 2) {
      swordLevel += rand.nextInt(isHardDifficulty() ? 3 : 2);
      swordLevel = Math.min(swordLevel, 2);
    }
    switch (swordLevel) {
    case 0:
      return new ItemStack(Items.wooden_sword);
    case 1:
      return new ItemStack(Items.stone_sword);
    case 2:
      return new ItemStack(Items.iron_sword);
    case 4:
      return new ItemStack(Items.diamond_sword);
    }
    return new ItemStack(Items.iron_sword);
  }

  @Override
  public IEntityLivingData onInitialSpawn(DifficultyInstance di, IEntityLivingData livingData) {
    spawned = true;

    //From base entity living class
    getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random spawn bonus", rand.nextGaussian() * 0.05D, 1));
    setSkeletonType(0);
    addRandomArmor();
    setEnchantmentBasedOnDifficulty(di); //enchantEquipment();

    float f = di.getClampedAdditionalDifficulty();
    this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * f);
    setCanPickUpLoot(rand.nextFloat() < 0.55F * f);
    setCanBreakDoors(rand.nextFloat() < f * 0.1F);

    return livingData;
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound root) {
    super.writeEntityToNBT(root);
    root.setBoolean("canBreakDoors", canBreakDoors);
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound root) {
    super.readEntityFromNBT(root);
    setCanBreakDoors(root.getBoolean("canBreakDoors"));
  }

  private void setCanBreakDoors(boolean val) {
    if(canBreakDoors != val) {
      canBreakDoors = val;
      if(canBreakDoors) {
        tasks.addTask(1, breakDoorAI);
      } else {
        tasks.removeTask(breakDoorAI);
      }
    }
  }

  @Override
  protected void dropFewItems(boolean hitByPlayer, int lootingLevel) {
    int numDrops = rand.nextInt(3 + lootingLevel);
    for (int i = 0; i < numDrops; ++i) {
      if(rand.nextBoolean()) {
        dropItem(Items.bone, 1);
      } else {
        dropItem(Items.rotten_flesh, 1);
      }
    }
  }

}
