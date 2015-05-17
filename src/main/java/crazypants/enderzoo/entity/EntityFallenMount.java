package crazypants.enderzoo.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import crazypants.enderzoo.config.Config;

public class EntityFallenMount extends EntityHorse implements IEnderZooMob {

  public static final int EGG_BG_COL = 0x365A25;
  public static final int EGG_FG_COL = 0xA0A0A0;
  public static String NAME = "enderzoo.FallenMount";

  public static final double MOUNTED_ATTACK_MOVE_SPEED = Config.fallenMountChargeSpeed;;

  private boolean wasRidden = false;
  private final EntityAINearestAttackableTarget findTargetAI;
  private EntityAIAttackOnCollide attackAI;

  private ItemStack armor;

  public EntityFallenMount(World world) {
    super(world);
    setGrowingAge(0);
    setHorseSaddled(true);

    tasks.taskEntries.clear();
    tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(6, new EntityAIWander(this, 1.2D));
    tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    tasks.addTask(8, new EntityAILookIdle(this));

    findTargetAI = new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true);
    attackAI = new EntityAIAttackOnCollide(this, EntityPlayer.class, MOUNTED_ATTACK_MOVE_SPEED, false);
    updateAttackAI();
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
    MobInfo.FALLEN_MOUNT.applyAttributes(this);
  }

  @Override
  protected boolean isMovementBlocked() {
    return isRearing();
  }

  @Override
  public boolean interact(EntityPlayer p_70085_1_) {
    ItemStack itemstack = p_70085_1_.inventory.getCurrentItem();
    if (itemstack != null && itemstack.getItem() == Items.spawn_egg) {
      return super.interact(p_70085_1_);
    }
    return false;
  }

  @Override
  protected boolean canDespawn() {
    return true;
  }

  @Override
  public boolean canMateWith(EntityAnimal p_70878_1_) {
    return false;
  }

  @Override
  public boolean allowLeashing() {
    return false;
  }

  @Override
  public boolean isBreedingItem(ItemStack p_70877_1_) {
    return false;
  }

  @Override
  public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
    if (type == EnumCreatureType.monster) {
      return true;
    }
    return false;
  }

  @Override
  public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
    setHorseType(3);
    setHorseSaddled(true);
    setGrowingAge(0);
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Config.fallenMountHealth);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2);
    getAttributeMap().getAttributeInstanceByName("horse.jumpStrength").setBaseValue(0.5);
    setHealth(getMaxHealth());

    float chanceOfArmor = worldObj.difficultySetting == EnumDifficulty.HARD ? Config.fallenMountChanceArmoredHard : Config.fallenMountChanceArmored;
    if (rand.nextFloat() <= chanceOfArmor) {

      //Value between 0 and 1 (normal) - 1.5 based on how long a chunk has been occupied and the moon phase
      float occupiedDiffcultyMultiplier = worldObj.func_147462_b(posX, posY, posZ);
      occupiedDiffcultyMultiplier /= 1.5f; // normalize
      float chanceImprovedArmor = worldObj.difficultySetting == EnumDifficulty.HARD ? Config.fallenMountChanceArmorUpgradeHard
          : Config.fallenMountChanceArmorUpgrade;
      chanceImprovedArmor *= (1 + occupiedDiffcultyMultiplier); //If we have the max occupied factor, double the chance of improved armor

      int armorLevel = 0;
      for (int i = 0; i < 2; i++) {
        if (rand.nextFloat() <= chanceImprovedArmor) {
          armorLevel++;
        }
      }
      Item armorItem = Items.iron_horse_armor;
      switch (armorLevel) {
      case 1:
        armorItem = Items.golden_horse_armor;
        break;
      case 2:
        armorItem = Items.diamond_horse_armor;
        break;
      }
      armor = new ItemStack(armorItem);
      func_146086_d(armor);
    }
    return data;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
      setDead();
    }
  }

  @Override
  public void onLivingUpdate() {

    super.onLivingUpdate();

    if (worldObj.isDaytime() && !worldObj.isRemote) {
      if (burnInSun() && worldObj.getTotalWorldTime() % 20 == 0) {
        float f = getBrightness(1.0F);
        if (f > 0.5F && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F
            && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))) {
          setFire(8);
        }
      }
    }
    setEating(false);

    if (wasRidden != isRidden()) {
      updateAttackAI();
      wasRidden = isRidden();
    }
  }

  private boolean burnInSun() {
    if (!isRidden()) {
      return getTotalArmorValue() == 0;
    }
    if (Config.fallenMountShadedByRider) {
      return false;
    }
    return getTotalArmorValue() > 0;
  }

  protected boolean isRidden() {
    return riddenByEntity != null;
  }

  private void updateAttackAI() {
    targetTasks.removeTask(findTargetAI);
    tasks.removeTask(attackAI);
    if (!isRidden()) {
      targetTasks.addTask(2, findTargetAI);
      tasks.addTask(4, attackAI);
    }
  }

  @Override
  public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
    //Need to pretend we arn't being ridden else it will update as if a player was riding us    
    Entity prev = riddenByEntity;
    riddenByEntity = null;
    super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
    riddenByEntity = prev;
  }

  @Override
  public boolean attackEntityAsMob(Entity target) {
    if (isRidden() || isDead) {
      return false;
    }
    super.attackEntityAsMob(target);
    if (!isRearing()) {
      makeHorseRearWithSound();
    }
    float damage = (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
    return target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound root) {
    super.writeEntityToNBT(root);
    if (armor != null) {
      NBTTagCompound armTag = new NBTTagCompound();
      armor.writeToNBT(armTag);
      root.setTag("armor", armTag);
    }
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound root) {
    super.readEntityFromNBT(root);
    setHorseSaddled(true);
    if (root.hasKey("armor")) {
      NBTTagCompound armTag = root.getCompoundTag("armor");
      armor = ItemStack.loadItemStackFromNBT(armTag);
      func_146086_d(armor);
    }
  }

}
