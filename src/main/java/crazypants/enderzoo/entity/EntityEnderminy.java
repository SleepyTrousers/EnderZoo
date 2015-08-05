package crazypants.enderzoo.entity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.vec.VecUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityEnderminy extends EntityMob implements IEnderZooMob {

  public static String NAME = "enderzoo.Enderminy";
  public static final int EGG_BG_COL = 0x27624D;
  public static final int EGG_FG_COL = 0x212121;

  private static final int MAX_RND_TP_DISTANCE = 32;

  private static final int SCREAMING_INDEX = 20;

  private static final UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291B0");
  private static final AttributeModifier attackingSpeedBoostModifier = (new AttributeModifier(attackingSpeedBoostModifierUUID, "Attacking speed boost",
      6.2, 0)).setSaved(false);

  private boolean isAggressive;

  private boolean attackIfLookingAtPlayer = Config.enderminyAttacksPlayerOnSight;
  private boolean attackCreepers = Config.enderminyAttacksCreepers;
  private boolean groupAgroEnabled = Config.enderminyGroupAgro;

  public EntityEnderminy(World world) {
    super(world);
    setSize(0.6F * 0.5F, 2.9F * 0.25F);
    stepHeight = 1.0F;

    tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0D, false));
    tasks.addTask(7, new EntityAIWander(this, 1.0D));
    tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    tasks.addTask(8, new EntityAILookIdle(this));
    targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));

    if(attackIfLookingAtPlayer) {
      targetTasks.addTask(2, new AIFindPlayer());
    }

    if(attackCreepers) {
      targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityCreeper.class, true, true));
    }
  }


  @Override
  protected boolean isValidLightLevel() {
    return Config.enderminySpawnInLitAreas ? true : super.isValidLightLevel();
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
    MobInfo.ENDERMINY.applyAttributes(this);
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    dataWatcher.addObject(SCREAMING_INDEX, new Byte((byte) 0));
  }

  @Override
  public boolean getCanSpawnHere() {
    boolean passedGrassCheck = true;
    if(Config.enderminySpawnOnlyOnGrass) {
      int i = MathHelper.floor_double(posX);
      int j = MathHelper.floor_double(getEntityBoundingBox().minY);
      int k = MathHelper.floor_double(posZ);
      passedGrassCheck = worldObj.getBlockState(VecUtil.bpos(i, j - 1, k)).getBlock() == Blocks.grass;
    }
    return passedGrassCheck && posY > Config.enderminyMinSpawnY && super.getCanSpawnHere();
  }

  /**
   * Checks to see if this enderman should be attacking this player
   */
  private boolean shouldAttackPlayer(EntityPlayer player) {

    ItemStack itemstack = player.inventory.armorInventory[3];
    if(itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
      return false;
    } else {

      Vec3 relativePlayerEyePos = new Vec3(
          posX - player.posX,
          getEntityBoundingBox().minY + height / 2.0F - (player.posY + player.getEyeHeight()),
          posZ - player.posZ);

      double distance = relativePlayerEyePos.lengthVector();
      relativePlayerEyePos = relativePlayerEyePos.normalize();

      //NB: inverse of normal enderman, attack when this guy looks at the player instead of the other
      //way around
      Vec3 lookVec = getLook(1.0F).normalize();
      double dotTangent = -lookVec.dotProduct(relativePlayerEyePos);

      return dotTangent > 1.0D - 0.025D / distance;
    }
  }

  public void onLivingUpdate() {
    if(this.worldObj.isRemote) {
      for (int i = 0; i < 2; ++i) {
        this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
            this.posY + this.rand.nextDouble() * (double) this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
            (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
      }
    }
    isJumping = false;
    super.onLivingUpdate();
  }

  protected void updateAITasks() {
    if(isWet()) {
      attackEntityFrom(DamageSource.drown, 1.0F);
    }
    if(isScreaming() && !isAggressive && rand.nextInt(100) == 0) {
      setScreaming(false);
    }
    super.updateAITasks();
  }

  protected boolean teleportRandomly(int distance) {
    double d0 = posX + (rand.nextDouble() - 0.5D) * distance;
    double d1 = posY + rand.nextInt(distance + 1) - distance / 2;
    double d2 = posZ + (rand.nextDouble() - 0.5D) * distance;
    return teleportTo(d0, d1, d2);
  }

  protected boolean teleportRandomly() {
    return teleportRandomly(MAX_RND_TP_DISTANCE);
  }

  protected boolean teleportToEntity(Entity p_70816_1_) {
    Vec3 vec3 = new Vec3(posX - p_70816_1_.posX, getEntityBoundingBox().minY + height / 2.0F - p_70816_1_.posY
        + p_70816_1_.getEyeHeight(), posZ - p_70816_1_.posZ);
    vec3 = vec3.normalize();
    double d0 = 16.0D;
    double d1 = posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3.xCoord * d0;
    double d2 = posY + (rand.nextInt(16) - 8) - vec3.yCoord * d0;
    double d3 = posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3.zCoord * d0;
    return teleportTo(d1, d2, d3);
  }

  protected boolean teleportTo(double x, double y, double z) {

    EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
    if(MinecraftForge.EVENT_BUS.post(event)) {
      return false;
    }
    double d3 = posX;
    double d4 = posY;
    double d5 = posZ;
    posX = event.targetX;
    posY = event.targetY;
    posZ = event.targetZ;

    int xInt = MathHelper.floor_double(posX);
    int yInt = MathHelper.floor_double(posY);
    int zInt = MathHelper.floor_double(posZ);

    boolean flag = false;
    if(worldObj.isBlockLoaded(new BlockPos(xInt, yInt, zInt))) {

      boolean foundGround = false;
      while (!foundGround && yInt > 0) {
        Block block = worldObj.getBlockState(new BlockPos(xInt, yInt - 1, zInt)).getBlock();
        if(block.getMaterial().blocksMovement()) {
          foundGround = true;
        } else {
          --posY;
          --yInt;
        }
      }

      if(foundGround) {
        setPosition(posX, posY, posZ);
        if(worldObj.getCollidingBoundingBoxes(this, getEntityBoundingBox()).isEmpty() && !worldObj.isAnyLiquid(getEntityBoundingBox())) {
          flag = true;
        }
      }
    }

    if(!flag) {
      setPosition(d3, d4, d5);
      return false;
    }

    short short1 = 128;
    for (int l = 0; l < short1; ++l) {
      double d6 = l / (short1 - 1.0D);
      float f = (rand.nextFloat() - 0.5F) * 0.2F;
      float f1 = (rand.nextFloat() - 0.5F) * 0.2F;
      float f2 = (rand.nextFloat() - 0.5F) * 0.2F;
      double d7 = d3 + (posX - d3) * d6 + (rand.nextDouble() - 0.5D) * width * 2.0D;
      double d8 = d4 + (posY - d4) * d6 + rand.nextDouble() * height;
      double d9 = d5 + (posZ - d5) * d6 + (rand.nextDouble() - 0.5D) * width * 2.0D;
      worldObj.spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
    }

    worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
    playSound("mob.endermen.portal", 1.0F, 1.0F);
    return true;

  }

  @Override
  protected String getLivingSound() {
    return isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
  }

  @Override
  protected String getHurtSound() {
    return "mob.endermen.hit";
  }

  @Override
  protected String getDeathSound() {
    return "mob.endermen.death";
  }

  @Override
  protected Item getDropItem() {
    return Items.ender_pearl;
  }

  @Override
  protected void dropFewItems(boolean hitByPlayer, int looting) {
    Item item = getDropItem();
    if(item != null) {
      int numItems = rand.nextInt(2 + looting);
      for (int i = 0; i < numItems; ++i) {
        if(rand.nextFloat() <= 0.5) {
          dropItem(EnderZoo.itemEnderFragment, 1);
        }
        dropItem(item, 1);
      }
    }
  }

  /**
   * Called when the entity is attacked.
   */
  @Override
  public boolean attackEntityFrom(DamageSource damageSource, float p_70097_2_) {

    if(isEntityInvulnerable(damageSource)) {
      return false;
    }

    setScreaming(true);

    if(damageSource instanceof EntityDamageSourceIndirect) {
      isAggressive = false;
      for (int i = 0; i < 64; ++i) {
        if(teleportRandomly()) {
          return true;
        }
      }
      return super.attackEntityFrom(damageSource, p_70097_2_);
    }

    boolean res = super.attackEntityFrom(damageSource, p_70097_2_);
    if(damageSource instanceof EntityDamageSource && damageSource.getEntity() instanceof EntityPlayer &&
        getHealth() > 0
    //&& !ItemDarkSteelSword.isEquippedAndPowered((EntityPlayer) damageSource.getEntity(), 1)) {
    ) {
      isAggressive = true;
      if(rand.nextInt(3) == 0) {
        for (int i = 0; i < 64; ++i) {
          if(teleportRandomly(16)) {
            setAttackTarget((EntityPlayer) damageSource.getEntity());
            doGroupArgo();
            return true;
          }
        }
      }
    }

    if(res) {
      doGroupArgo();
    }
    return res;

  }

  private void doGroupArgo() {
    if(!groupAgroEnabled) {
      return;
    }
    if(!(getAttackTarget() instanceof EntityPlayer)) {
      return;
    }
    int range = 16;
    AxisAlignedBB bb = new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
    @SuppressWarnings("unchecked")
    List<EntityEnderminy> minies = worldObj.getEntitiesWithinAABB(EntityEnderminy.class, bb);
    if(minies != null && !minies.isEmpty()) {

      for (EntityEnderminy miny : minies) {

        if(miny.getAttackTarget() == null) { //&& miny.canEntityBeSeen(this)) {
          miny.setAttackTarget(getAttackTarget());
        }
      }
    }
  }

  public boolean isScreaming() {
    return dataWatcher.getWatchableObjectByte(SCREAMING_INDEX) > 0;
  }

  public void setScreaming(boolean p_70819_1_) {
    dataWatcher.updateObject(SCREAMING_INDEX, Byte.valueOf((byte) (p_70819_1_ ? 1 : 0)));
  }

//  private final class ClosestEntityComparator implements Comparator<EntityCreeper> {
//
//    Vec3 pos = new Vec3(0, 0, 0);
//
//    @Override
//    public int compare(EntityCreeper o1, EntityCreeper o2) {
//      pos = new Vec3(posX, posY, posZ);
//      double d1 = distanceSquared(o1.posX, o1.posY, o1.posZ, pos);
//      double d2 = distanceSquared(o2.posX, o2.posY, o2.posZ, pos);
//      return Double.compare(d1, d2);
//    }
//  }
//
//  public double distanceSquared(double x, double y, double z, Vec3 v2) {
//    double dx, dy, dz;
//    dx = x - v2.xCoord;
//    dy = y - v2.yCoord;
//    dz = z - v2.zCoord;
//    return (dx * dx + dy * dy + dz * dz);
//  }

  class AIFindPlayer extends EntityAINearestAttackableTarget {

    private EntityPlayer targetPlayer;
    private int stareTimer;
    private int teleportDelay;
    private EntityEnderminy enderminy = EntityEnderminy.this;

    public AIFindPlayer() {
      super(EntityEnderminy.this, EntityPlayer.class, true);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @SuppressWarnings("unchecked")
    public boolean shouldExecute() {
      double d0 = getTargetDistance();
      List<?> list = taskOwner.worldObj.getEntitiesWithinAABB(EntityPlayer.class, taskOwner.getEntityBoundingBox().expand(d0, 4.0D, d0), targetEntitySelector);
      Collections.sort(list, this.theNearestAttackableTargetSorter);
      if(list.isEmpty()) {
        return false;
      } else {
        targetPlayer = (EntityPlayer) list.get(0);
        return true;
      }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
      stareTimer = 5;
      teleportDelay = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask() {
      targetPlayer = null;
      enderminy.setScreaming(false);
      IAttributeInstance iattributeinstance = enderminy.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      iattributeinstance.removeModifier(EntityEnderminy.attackingSpeedBoostModifier);
      super.resetTask();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
      if(targetPlayer != null) {
        if(!enderminy.shouldAttackPlayer(targetPlayer)) {
          return false;
        } else {
          enderminy.isAggressive = true;
          enderminy.faceEntity(targetPlayer, 10.0F, 10.0F);
          return true;
        }
      } else {
        return super.continueExecuting();
      }
    }

    /**
     * Updates the task
     */
    public void updateTask() {
      if(targetPlayer != null) {
        if(--stareTimer <= 0) {
          targetEntity = targetPlayer;
          targetPlayer = null;
          super.startExecuting();
          enderminy.playSound("mob.endermen.stare", 1.0F, 1.0F);
          enderminy.setScreaming(true);
          IAttributeInstance iattributeinstance = enderminy.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
          iattributeinstance.applyModifier(EntityEnderminy.attackingSpeedBoostModifier);
        }
      } else {
        if(targetEntity != null) {
          if(targetEntity instanceof EntityPlayer && enderminy.shouldAttackPlayer((EntityPlayer) this.targetEntity)) {
            if(targetEntity.getDistanceSqToEntity(enderminy) < 16.0D) {
              enderminy.teleportRandomly();
            }
            teleportDelay = 0;
          } else if(targetEntity.getDistanceSqToEntity(enderminy) > 256.0D && this.teleportDelay++ >= 30 && enderminy.teleportToEntity(targetEntity)) {
            teleportDelay = 0;
          }
        }
        super.updateTask();
      }
    }
  }

}
