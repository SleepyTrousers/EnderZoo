package crazypants.enderzoo.entity.ai;

import crazypants.enderzoo.entity.EntityUtil;
import crazypants.enderzoo.vec.Point3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class EntityAIFlyingLand extends EntityAIBase {

  private EntityCreature entity;
  protected double speed;
  private double targetX;
  private double targetY;
  private double targetZ;

  private int onGroundCount = 0;

  public EntityAIFlyingLand(EntityCreature creature, double speedIn) {
    entity = creature;
    speed = speedIn;
    setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {
    if (entity.onGround || !entity.getNavigator().noPath()) {
      return false;
    }

    int xSearchRange = 3;
    int ySearchRange = 4;
    Vec3 target = null;

    BlockPos ep = entity.getPosition();
    Point3i blockLocationResult = new Point3i();
    IBlockState block = EntityUtil.getSurfaceBlock(entity.worldObj, ep.getX(), ep.getZ(), 1, ep.getY(), blockLocationResult, true, false);
    if (block != null) {
      int distFromGround = ep.getY() - blockLocationResult.y;
      if (distFromGround < 2) {
        target = new Vec3(blockLocationResult.x + 0.5, blockLocationResult.y + 1, blockLocationResult.z);
      } else {
        ySearchRange += ep.getY() - blockLocationResult.y;
      }
    }
    if (target == null) {
      target = findLandingTarget(xSearchRange, ySearchRange);
    }
    if (target == null) {
      // System.out.println("EntityAIFlyingLand.shouldExecute: no target
      // found");
      return false;
    }
    targetX = target.xCoord;
    targetY = target.yCoord;
    targetZ = target.zCoord;
    return true;
  }

  private Vec3 findLandingTarget(int horizSearchRange, int ySearchRange) {
    BlockPos ep = entity.getPosition();
    Point3i surfaceLoc = new Point3i();
    for (int x = -horizSearchRange; x <= horizSearchRange; x++) {
      for (int z = -horizSearchRange; z <= horizSearchRange; z++) {
        IBlockState res = EntityUtil.getSurfaceBlock(entity.worldObj, ep.getX() + x, ep.getZ() + z, 1, ep.getY(), surfaceLoc, false, false);
        if (res != null) {
          return new Vec3(surfaceLoc.x + 0.5, surfaceLoc.y + 1, surfaceLoc.z + 0.5);
        }
      }
    }

    return null;
  }

  @Override
  public void startExecuting() {
    onGroundCount = 0;
    entity.getNavigator().tryMoveToXYZ(targetX, targetY, targetZ, speed);
  }

  @Override
  public boolean continueExecuting() {
    if (entity.onGround) {
      
      onGroundCount++;
      if(onGroundCount >= 40) {
        //If we have been on the ground for a couple of seconds
        //time to chill no matter what
        entity.getNavigator().clearPathEntity();
        return false;
      }
      
      //Stop if we are on the ground in the middle of a block
      double fx = entity.posX - Math.floor(entity.posX);
      double fz = entity.posX - Math.floor(entity.posX);
      if (fx > 0.4 && fx < 0.6 && fz > 0.4 && fz < 0.6) {
        // System.out.println("EntityAIFlyingLand.continueExecuting: Stop");
        BlockPos bellow = entity.getPosition().down();
        IBlockState bs = entity.worldObj.getBlockState(bellow);
        if (!bs.getBlock().isAir(entity.worldObj, bellow)) {
          entity.getNavigator().clearPathEntity();
          //System.out.println("EntityAIFlyingLand.continueExecuting: Stop " + entity.posX + " " + entity.posZ);
          return false;
        }
      }
    }

    boolean isStillNavigating = !entity.getNavigator().noPath();
    if (!isStillNavigating) {
      entity.onGround = EntityUtil.isOnGround(entity);
      entity.isAirBorne = !entity.onGround;
    }    
    return isStillNavigating;
  }

}
