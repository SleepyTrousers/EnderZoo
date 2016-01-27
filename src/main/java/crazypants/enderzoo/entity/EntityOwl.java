package crazypants.enderzoo.entity;

import java.util.List;

import crazypants.enderzoo.entity.ai.EntityAIPanicFlying;
import crazypants.enderzoo.entity.ai.PathNavigateFlyer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
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

  private float bodyAngle = 0;
  private float targetBodyAngle = 0;
  private float wingAngle;

  public EntityOwl(World worldIn) {
    super(worldIn);
    setSize(0.3F, 0.8F);
    stepHeight = 1.0F;

    // tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(1, new EntityAIPanicFlying(this,2D));
    // tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.wheat_seeds,
    // false));
    // tasks.addTask(5, new EntityAIWander(this, 1.0D));
    // tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class,
    // 6.0F));
    tasks.addTask(7, new EntityAILookIdle(this));

    moveHelper = new OwlMoveHelper(this);

  }

  @Override
  public float getEyeHeight() {
    return height;
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
  }

  @Override
  protected PathNavigate getNewNavigator(World worldIn) {
    return new PathNavigateFlyer(this, worldIn);
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

  @Override
  public void onLivingUpdate() {
    // setDead();
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
    wingRotation += wingRotDelta * 2.0F;

    if (bodyAngle <= 0) {
      targetBodyAngle = 90;
    } else if (bodyAngle >= 90) {
      targetBodyAngle = 0;
    }
  }

  @Override
  public void moveFlying(float strafe, float forward, float friction) {
    super.moveFlying(strafe, forward, friction);
  }

  @Override
  public void moveEntity(double x, double y, double z) {
    super.moveEntity(x, y, z);
  }

  @Override
  public void moveEntityWithHeading(float strafe, float forward) {

    moveFlying(strafe, forward, 0.1f);
    if (!onGround && strafe == 0 && forward == 0) {
      //Drift down if not moving and in the air
      motionY -= 0.01;
    }
    moveEntity(motionX, motionY, motionZ);
    // drag
    motionX *= 0.8;
    motionY *= 0.8;
    motionZ *= 0.8;

    // Check for landing
    List<AxisAlignedBB> collides = worldObj.getCollidingBoundingBoxes(this, getEntityBoundingBox().offset(0, -0.05, 0));
    if (collides != null && !collides.isEmpty()) {
      onGround = true;
    } else {
      onGround = false;
    }
    isAirBorne = !onGround;
  }

  private void calculateWingAngle(float partialTicks) {
    float flapComletion = prevWingRotation + (wingRotation - prevWingRotation) * partialTicks;
    float onGroundTimerThing = prevDestPos + (destPos - prevDestPos) * partialTicks;
    wingAngle = (MathHelper.sin(flapComletion) + 1.0F) * onGroundTimerThing;

    if (onGround) {
      wingAngle = 0;
    }
  }

  private void calculateBodyAngle(float partialTicks) {
    // if (targetBodyAngle == bodyAngle) {
    // return;
    // }
    // if (targetBodyAngle > bodyAngle) {
    // bodyAngle += (5 * partialTicks);
    // } else {
    // bodyAngle -= (5 * partialTicks);
    // }
    bodyAngle = 5;
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

  public static class OwlMoveHelper extends EntityMoveHelper {

    private static double climbRate = 0.1D;
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
        owl.rotationYaw = limitAngle(owl.rotationYaw, yawAngle, 10.0F);
        owl.renderYawOffset = owl.rotationYaw;

        float moveSpeed = (float) (speed * owl.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
        // float moveFactor = 0.125F;        
        float moveFactor = 1;
//        if(owl.onGround) {
//          moveFactor = 0.1f;
//        }
        climbRate = 0.25;
        owl.setAIMoveSpeed(owl.getAIMoveSpeed() + (moveSpeed - owl.getAIMoveSpeed()) * moveFactor);
        owl.motionY += owl.getAIMoveSpeed() * yDelta * climbRate;

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
