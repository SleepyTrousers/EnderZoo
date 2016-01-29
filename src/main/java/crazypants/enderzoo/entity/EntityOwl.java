package crazypants.enderzoo.entity;

import crazypants.enderzoo.entity.ai.EntityAIFlyingFindPerch;
import crazypants.enderzoo.entity.ai.EntityAIFlyingLand;
import crazypants.enderzoo.entity.ai.EntityAIFlyingPanic;
import crazypants.enderzoo.entity.ai.FlyingPathNavigate;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityOwl extends EntityAnimal implements IEnderZooMob {

  public static final String NAME = "Owl";
  public static final int EGG_BG_COL = 0x27624D;
  public static final int EGG_FG_COL = 0x212121;

  private float wingRotation;
  private float prevWingRotation;
  private float wingRotDelta = 1.0F;

  private float destPos;
  private float prevDestPos;

  private float bodyAngle = 5;
  private float targetBodyAngle = 0;
  private float wingAngle;

  private double groundSpeedRatio = 0.25;
  private double climbRate = 0.25;
  private float turnRate = 10.0F;

  public EntityOwl(World worldIn) {
    super(worldIn);
    setSize(0.5F, 0.95F);
    stepHeight = 1.0F;

    int pri = 0;
    // tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(++pri, new EntityAIFlyingPanic(this, 2));
    tasks.addTask(++pri, new EntityAIFlyingLand(this, 2));    
    tasks.addTask(++pri, new EntityAIMate(this, 1.0));    
//    tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.wheat_seeds, false));
     tasks.addTask(++pri, new EntityAIFlyingFindPerch(this, 2, 60));

    tasks.addTask(++pri, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    tasks.addTask(++pri, new EntityAILookIdle(this));

    moveHelper = new OwlMoveHelper(this);

  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
  }

  @Override
  protected PathNavigate getNewNavigator(World worldIn) {
    return new FlyingPathNavigate(this, worldIn);
  }

  @Override
  public boolean interact(EntityPlayer playerIn) {
    if (!super.interact(playerIn)) {
      if (!worldObj.isRemote) {
        System.out.println("EntityOwl.interact: ");
        if (!getNavigator().tryMoveToXYZ(posX - 10, posY + 8, posZ - 10, 2)) {
          System.out.println("EntityOwl.interact: No path");
        }
      }

    }
    return true;
  }

  @Override
  public void onLivingUpdate() {
    
//     setDead();
    super.onLivingUpdate();
    prevWingRotation = wingRotation;
    prevDestPos = destPos;
    destPos = (float) (destPos + (onGround ? -1 : 4) * 0.3D);
    // destPos = (float) (destPos + 4 * 0.3);
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
    
    if(!isDead && !onGround && motionY < -0.1) {
//      System.out.println("EntityOwl.onLivingUpdate: Slow fall");
      motionY = -0.1;
    }

  }

  @Override
  public void moveEntityWithHeading(float strafe, float forward) {

    moveFlying(strafe, forward, 0.1f);   
    moveEntity(motionX, motionY, motionZ);
    // drag
    motionX *= 0.8;
    motionY *= 0.8;
    motionZ *= 0.8;

    // Check for landing
    onGround = EntityUtil.isOnGround(this);    
    isAirBorne = !onGround;

    if (onGround) {
      motionX *= groundSpeedRatio;
      motionZ *= groundSpeedRatio;
    }

    prevLimbSwingAmount = limbSwingAmount;
    double deltaX = this.posX - this.prevPosX;
    double deltaZ = this.posZ - this.prevPosZ;
    float f7 = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) * 4.0F;
    if (f7 > 1.0F) {
      f7 = 1.0F;
    }
    limbSwingAmount += (f7 - limbSwingAmount) * 0.4F;
    limbSwing += limbSwingAmount;
  }

  private void calculateWingAngle(float partialTicks) {
    float flapComletion = prevWingRotation + (wingRotation - prevWingRotation) * partialTicks;
    float onGroundTimerThing = prevDestPos + (destPos - prevDestPos) * partialTicks;
    wingAngle = (MathHelper.sin(flapComletion) + 1.0F) * onGroundTimerThing;

    if (onGround) {
      wingAngle = (float)Math.toRadians(3);
    }
  }

  private void calculateBodyAngle(float partialTicks) {

    if (onGround) {
      bodyAngle = 7;
      targetBodyAngle = 7;
      return;
    }

    // ignore y as we want no tilt going straight up or down
    Vec3 motionVec = new Vec3(motionX, 0, motionZ);
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
  public void fall(float distance, float damageMultiplier) {
  }

  @Override
  protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
  }

  @Override
  protected String getLivingSound() {
    return "mob.chicken.say";
  }

  @Override
  protected String getHurtSound() {
    return "mob.chicken.hurt";
  }

  @Override
  protected String getDeathSound() {
    return "mob.chicken.hurt";
  }

  @Override
  public EntityOwl createChild(EntityAgeable ageable) {
    return new EntityOwl(worldObj);
  }

  @Override
  public boolean isBreedingItem(ItemStack stack) {
    return stack != null && stack.getItem() == Items.wheat_seeds;
  }

  @Override
  protected void playStepSound(BlockPos pos, Block blockIn) {
    playSound("mob.chicken.step", 0.15F, 1.0F);
  }

  @Override
  protected Item getDropItem() {
    return Items.feather;
  }

  public static class OwlMoveHelper extends EntityMoveHelper {

    private EntityOwl owl;

    public OwlMoveHelper(EntityOwl owl) {
      super(owl);
      this.owl = owl;
    }

    @Override
    public void onUpdateMoveHelper() {
      
      if (update && !owl.getNavigator().noPath()) {
        double xDelta = posX - owl.posX;
        double yDelta = posY - owl.posY;
        double zDelta = posZ - owl.posZ;
        double distSq = xDelta * xDelta + yDelta * yDelta + zDelta * zDelta;
        double dist = MathHelper.sqrt_double(distSq);
        yDelta = yDelta / dist;
        float yawAngle = (float) (MathHelper.atan2(zDelta, xDelta) * 180.0D / Math.PI) - 90.0F;
        // owl.rotationYaw = limitAngle(owl.rotationYaw, yawAngle, 30.0F);
        owl.rotationYaw = limitAngle(owl.rotationYaw, yawAngle, owl.turnRate);
        owl.renderYawOffset = owl.rotationYaw;

        float moveSpeed = (float) (speed * owl.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
        // float moveFactor = 0.125F;
        float moveFactor = 1;
//        System.out.println("EntityOwl.OwlMoveHelper.onUpdateMoveHelper: " + owl.posZ + " " + posZ);
        if(yDelta > 0) {          
          //Ensure enough lift to get up to the target          
          yDelta = Math.max(0.1, yDelta);
        }
        
        owl.setAIMoveSpeed(owl.getAIMoveSpeed() + (moveSpeed - owl.getAIMoveSpeed()) * moveFactor);
        owl.motionY += owl.getAIMoveSpeed() * yDelta * owl.climbRate;

        // Look
        double d7 = owl.posX + (xDelta / dist * 2.0D);
        double d8 = owl.getEyeHeight() + owl.posY + (yDelta / dist * 1.0D);
        double d9 = owl.posZ + (zDelta / dist * 2.0D);

        EntityLookHelper entitylookhelper = owl.getLookHelper();
        double lookX = entitylookhelper.getLookPosX();
        double lookY = entitylookhelper.getLookPosY();
        double lookZ = entitylookhelper.getLookPosZ();

        if (!entitylookhelper.getIsLooking()) {
          lookX = d7;
          lookY = d8;
          lookZ = d9;
        }
        owl.getLookHelper().setLookPosition(lookX + (d7 - lookX) * 0.125D, lookY + (d8 - lookY) * 0.125D, lookZ + (d9 - lookZ) * 0.125D, 10.0F, 40.0F);
      } else {
        owl.setAIMoveSpeed(0.0F);
      }
    }

  }

}
