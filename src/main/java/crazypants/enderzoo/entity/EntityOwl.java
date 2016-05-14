package crazypants.enderzoo.entity;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.ai.EntityAIFlyingAttackOnCollide;
import crazypants.enderzoo.entity.ai.EntityAIFlyingFindPerch;
import crazypants.enderzoo.entity.ai.EntityAIFlyingLand;
import crazypants.enderzoo.entity.ai.EntityAIFlyingPanic;
import crazypants.enderzoo.entity.ai.EntityAIFlyingShortWander;
import crazypants.enderzoo.entity.ai.EntityAINearestAttackableTargetBounded;
import crazypants.enderzoo.entity.navigate.FlyingMoveHelper;
import crazypants.enderzoo.entity.navigate.FlyingPathNavigate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityOwl extends EntityAnimal implements IFlyingMob {

  public static final String NAME = "Owl";
  public static final int EGG_BG_COL = 0xC17949;
  public static final int EGG_FG_COL = 0xFFDDC6;

  private static final SoundEvent SND_HOOT = new SoundEvent(new ResourceLocation("enderzoo:owl.hootSingle"));
  private static final SoundEvent SND_HOOT2 = new SoundEvent(new ResourceLocation("enderzoo:owl.hootDouble"));
  private static final SoundEvent SND_HURT = new SoundEvent(new ResourceLocation("enderzoo:owl.hurt"));

  private float wingRotation;
  private float prevWingRotation;
  private float wingRotDelta = 1.0F;

  private float destPos;
  private float prevDestPos;

  private float bodyAngle = 5;
  private float targetBodyAngle = 0;
  private float wingAngle;

  private double groundSpeedRatio = 0.25;
  private float climbRate = 0.25f;
  private float turnRate = 30;

  public int timeUntilNextEgg;;

  public EntityOwl(World worldIn) {
    super(worldIn);
    setSize(0.4F, 0.85F);
    stepHeight = 1.0F;

    int pri = 0;
    tasks.addTask(++pri, new EntityAIFlyingPanic(this, 2));
    tasks.addTask(++pri, new EntityAIFlyingAttackOnCollide(this, 2.5, false));    
    tasks.addTask(++pri, new EntityAIMate(this, 1.0));
    tasks.addTask(++pri, new EntityAITempt(this, 1.0D, Items.spider_eye, false));
    tasks.addTask(++pri, new EntityAIFollowParent(this, 1.5));
    tasks.addTask(++pri, new EntityAIFlyingLand(this, 2));
    tasks.addTask(++pri, new EntityAIFlyingFindPerch(this, 2, 80));
    tasks.addTask(++pri, new EntityAIFlyingShortWander(this, 2, 150));

    tasks.addTask(++pri, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    tasks.addTask(++pri, new EntityAILookIdle(this));

    EntityAINearestAttackableTargetBounded<EntitySpider> targetSpiders = new EntityAINearestAttackableTargetBounded<EntitySpider>(this, EntitySpider.class,
        true, true);
    targetSpiders.setMaxDistanceToTarget(12);
    targetSpiders.setMaxVerticalDistanceToTarget(24);
    targetTasks.addTask(0, targetSpiders);

    moveHelper = new FlyingMoveHelper(this);

    timeUntilNextEgg = getNextLayingTime();
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    MobInfo.OWL.applyAttributes(this);
    
  }

  @Override
  protected PathNavigate getNewNavigator(World worldIn) {
    return new FlyingPathNavigate(this, worldIn);
  }

  @Override
  public FlyingPathNavigate getFlyingNavigator() {
    return (FlyingPathNavigate) getNavigator();
  }

  @Override
  public float getBlockPathWeight(BlockPos pos) {
    IBlockState bs = worldObj.getBlockState(pos.down());
    return bs.getBlock().getMaterial(bs) == Material.leaves ? 10.0F : 0;
  }

  @Override
  public boolean attackEntityAsMob(Entity entityIn) {
    super.attackEntityAsMob(entityIn);
    float attackDamage = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    if (entityIn instanceof EntitySpider) {
      attackDamage *= Config.owlSpiderDamageMultiplier;
    }
    return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
  }

  @Override
  public void onLivingUpdate() {

    // setDead();
    super.onLivingUpdate();
    prevWingRotation = wingRotation;
    prevDestPos = destPos;
    destPos = (float) (destPos + (onGround ? -1 : 4) * 0.3D);
    destPos = MathHelper.clamp_float(destPos, 0.0F, 1.0F);
    if (!onGround && wingRotDelta < 1.0F) {
      wingRotDelta = 1.0F;
    }
    wingRotDelta = (float) (wingRotDelta * 0.9D);
    float flapSpeed = 2f;
    double yDelta = Math.abs(posY - prevPosY);
    if (yDelta != 0) {
      // normalise between 0 and 0.02
      yDelta = Math.min(1, yDelta / 0.02);
      yDelta = Math.max(yDelta, 0.75);
      flapSpeed *= yDelta;
    }
    wingRotation += wingRotDelta * flapSpeed;

    if (!worldObj.isRemote && !isChild() && --timeUntilNextEgg <= 0) {
      if (isOnLeaves()) {
        playSound(SoundEvents.entity_chicken_egg, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        dropItem(EnderZoo.itemOwlEgg, 1);        
      }
      timeUntilNextEgg = getNextLayingTime();
    }

  }
  
  private boolean isOnLeaves() {
    IBlockState bs = worldObj.getBlockState(getPosition().down());    
    return bs.getBlock().getMaterial(bs) == Material.leaves;
  }

  @Override
  public void moveEntityWithHeading(float strafe, float forward) {

    moveFlying(strafe, forward, 0.1f);

    // Dont fly up inot things
    AxisAlignedBB movedBB = getEntityBoundingBox().offset(0, motionY, 0);
    BlockPos ep = getPosition();
    BlockPos pos = new BlockPos(ep.getX(), movedBB.maxY, ep.getZ());
    IBlockState bs = worldObj.getBlockState(pos);
    Block block = bs.getBlock();
    if (block.getMaterial(bs) != Material.air) {
      AxisAlignedBB bb = block.getCollisionBoundingBox(bs, worldObj, pos);
      if (bb != null) {
        double ouch = movedBB.maxY - bb.minY;
        if (ouch == 0) {
          motionY = -0.1;
        } else {
          motionY = 0;
        }
      }
    }

    moveEntity(motionX, motionY, motionZ);

    // drag
    motionX *= 0.8;
    motionY *= 0.8;
    motionZ *= 0.8;

    onGround = EntityUtil.isOnGround(this);

    isAirBorne = !onGround;

    if (onGround) {
      motionX *= groundSpeedRatio;
      motionZ *= groundSpeedRatio;
    }

    prevLimbSwingAmount = limbSwingAmount;
    double deltaX = posX - prevPosX;
    double deltaZ = posZ - prevPosZ;
    float f7 = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) * 4.0F;
    if (f7 > 1.0F) {
      f7 = 1.0F;
    }
    limbSwingAmount += (f7 - limbSwingAmount) * 0.4F;
    limbSwing += limbSwingAmount;
  }

  @Override
  public boolean isEntityInsideOpaqueBlock() {
    if (noClip) {
      return false;
    } else {
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

      for (int i = 0; i < 8; ++i) {
        int x = MathHelper.floor_double(posX + ((i >> 1) % 2 - 0.5F) * width * 0.8F);
        int y = MathHelper.floor_double(posY + ((i >> 0) % 2 - 0.5F) * 0.1F + getEyeHeight());
        // I added this check as it was sometimes clipping into the block above
        if (y > getEntityBoundingBox().maxY) {
          y = MathHelper.floor_double(getEntityBoundingBox().maxY);
        }
        int z = MathHelper.floor_double(posZ + ((i >> 2) % 2 - 0.5F) * width * 0.8F);

        if (pos.getX() != x || pos.getY() != y || pos.getZ() != z) {
          pos.set(x, y, z);
          if (worldObj.getBlockState(pos).getBlock().isVisuallyOpaque()) {
            return true;
          }
        }
      }

      return false;
    }
  }

  private void calculateWingAngle(float partialTicks) {
    float flapComletion = prevWingRotation + (wingRotation - prevWingRotation) * partialTicks;
    float onGroundTimerThing = prevDestPos + (destPos - prevDestPos) * partialTicks;
    wingAngle = (MathHelper.sin(flapComletion) + 1.0F) * onGroundTimerThing;

    if (onGround) {
      wingAngle = (float) Math.toRadians(3);
    }
  }

  private void calculateBodyAngle(float partialTicks) {

    if (onGround) {
      bodyAngle = 7;
      targetBodyAngle = 7;
      return;
    }

    // ignore y as we want no tilt going straight up or down
    Vec3d motionVec = new Vec3d(motionX, 0, motionZ);
    double speed = motionVec.lengthVector();
    // normalise between 0 - 0.1
    speed = Math.min(1, speed * 10);
    targetBodyAngle = 20 + ((float) speed * 30);

    if (targetBodyAngle == bodyAngle) {
      return;
    }
    if (targetBodyAngle > bodyAngle) {
      bodyAngle += (2 * partialTicks);
      if (bodyAngle > targetBodyAngle) {
        bodyAngle = targetBodyAngle;
      }
    } else {
      bodyAngle -= (1 * partialTicks);
      if (bodyAngle < targetBodyAngle) {
        bodyAngle = targetBodyAngle;
      }
    }
  }

  public void calculateAngles(float partialTicks) {
    calculateBodyAngle(partialTicks);
    calculateWingAngle(partialTicks);
  }

  public float getBodyAngle() {
    return (float) Math.toRadians(bodyAngle);
  }

  public float getWingAngle() {
    return wingAngle;
  }

  @Override
  public float getEyeHeight() {
    return height;
  }

  @Override
  protected void updateFallState(double y, boolean onGroundIn, IBlockState blockIn, BlockPos pos) {
  }

  @Override
  public int getTalkInterval() {
    return Config.owlHootInterval;
  }

  @Override
  public void playLivingSound() {
    SoundEvent snd = getAmbientSound();
    if (snd == null) {
      return;
    }

    if (worldObj != null && !worldObj.isRemote && (worldObj.isDaytime() || getAttackTarget() != null)) {
      return;
    }

    float volume = getSoundVolume() * Config.owlHootVolumeMult;
    float pitch = 0.8f * getSoundPitch();
    playSound(snd, volume, pitch);

  }

  @Override
  protected SoundEvent getAmbientSound() {
    if (worldObj.rand.nextBoolean()) {
      return SND_HOOT2;
    } else {
      return SND_HOOT;
    }
  }

  @Override
  protected SoundEvent getHurtSound() {    
    return SND_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SND_HURT;
  }

  @Override
  public EntityOwl createChild(EntityAgeable ageable) {
    return new EntityOwl(worldObj);
  }

  @Override
  public boolean isBreedingItem(ItemStack stack) {
    return stack != null && stack.getItem() == Items.spider_eye;
  }

  @Override
  protected void playStepSound(BlockPos pos, Block blockIn) {
    playSound(SoundEvents.entity_chicken_step, 0.15F, 1.0F);
  }

  @Override
  protected Item getDropItem() {
    return Items.feather;
  }

  @Override
  public float getMaxTurnRate() {
    return turnRate;
  }

  @Override
  public float getMaxClimbRate() {
    return climbRate;
  }

  @Override
  public EntityCreature asEntityCreature() {
    return this;
  }

  private int getNextLayingTime() {
    int dif = Config.owlTimeBetweenEggsMax - Config.owlTimeBetweenEggsMin;
    return Config.owlTimeBetweenEggsMin + rand.nextInt(dif);    
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound tagCompund) {
    super.readEntityFromNBT(tagCompund);
    if (tagCompund.hasKey("EggLayTime")) {
      this.timeUntilNextEgg = tagCompund.getInteger("EggLayTime");
    }
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound tagCompound) {
    super.writeEntityToNBT(tagCompound);
    tagCompound.setInteger("EggLayTime", this.timeUntilNextEgg);
  }

}
