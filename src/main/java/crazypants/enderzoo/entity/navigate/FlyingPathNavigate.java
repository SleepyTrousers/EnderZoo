package crazypants.enderzoo.entity.navigate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class FlyingPathNavigate extends PathNavigateGround {

  private int totalTicks;
  private int ticksAtLastPos;
  private Vec3 lastPosCheck = new Vec3(0.0D, 0.0D, 0.0D);

  private boolean forceFlying = false;

  public FlyingPathNavigate(EntityLiving entitylivingIn, World worldIn) {
    super(entitylivingIn, worldIn);
  }

  public boolean isForceFlying() {
    return forceFlying && !noPath();
  }

  public void setForceFlying(boolean forceFlying) {
    this.forceFlying = forceFlying;
  }

  @Override
  protected PathFinder getPathFinder() {
    nodeProcessor = new FlyNodeProcessor();
    return new FlyingPathFinder(nodeProcessor);
  }

  @Override
  protected boolean canNavigate() {
    return true;
  }

  @Override
  protected Vec3 getEntityPosition() {
    int y = (int) (theEntity.getEntityBoundingBox().minY + 0.5D);
    return new Vec3(theEntity.posX, y, theEntity.posZ);
  }

  public boolean tryFlyToXYZ(double x, double y, double z, double speedIn) {
    PathEntity pathentity = getPathToPos(new BlockPos((double) MathHelper.floor_double(x), (double) ((int) y), (double) MathHelper.floor_double(z)));
    return setPath(pathentity, speedIn, true);
  }

  public boolean tryFlyToPos(double x, double y, double z, double speedIn) {
    PathEntity pathentity = getPathToXYZ(x, y, z);
    return setPath(pathentity, speedIn, true);
  }

  public boolean tryFlyToEntityLiving(Entity entityIn, double speedIn) {
    PathEntity pathentity = getPathToEntityLiving(entityIn);
    return pathentity != null ? setPath(pathentity, speedIn, true) : false;
  }

  public boolean setPath(PathEntity path, double speed, boolean forceFlying) {
    if (super.setPath(path, speed)) {
      // String str = "FlyingPathNavigate.setPath:";
      // for (int i = 0; i < path.getCurrentPathLength(); i++) {
      // PathPoint pp = path.getPathPointFromIndex(i);
      // str += " [" + pp + "]";
      // }
      // Log.info(str);
      ticksAtLastPos = totalTicks;
      lastPosCheck = getEntityPosition();
      this.forceFlying = forceFlying;
      return true;
    }
    return false;
  }

  @Override
  public boolean setPath(PathEntity path, double speed) {
    return setPath(path, speed, false);
  }

  @Override
  public void onUpdateNavigation() {
    ++totalTicks;
    if (!noPath()) { // if we have a path
      // theEntity.onGround = false;
      // theEntity.isAirBorne = true;
      pathFollow(); // follow it
      if (!noPath()) { // if we haven't finished, then set the new move point
        Vec3 targetPos = currentPath.getPosition(theEntity);
        if (targetPos == null) {
          return;
        }
        double y = targetPos.yCoord;
        if (forceFlying) {
          double aboveBlock = y - (int) y;
          if (aboveBlock < 0.10) {
            y = (int) y + 0.10;
          }
        }
        theEntity.getMoveHelper().setMoveTo(targetPos.xCoord, y, targetPos.zCoord, speed);
      }
    }

  }

  @Override
  protected void pathFollow() {

    Vec3 entPos = getEntityPosition();
    float entWidthSq = theEntity.width * theEntity.width;
    if (currentPath.getCurrentPathIndex() == currentPath.getCurrentPathLength() - 1 && theEntity.onGround) {
      entWidthSq = 0.01f; // we need to be right on top of the last point if on
                          // the ground so we don't hang on ledges
    }

    Vec3 targetPos = currentPath.getVectorFromIndex(theEntity, currentPath.getCurrentPathIndex());

    double distToCurrTargSq = entPos.squareDistanceTo(targetPos);
    if (distToCurrTargSq < entWidthSq) {
      currentPath.incrementPathIndex();
    }
    // starting six points ahead (or the end point) see if we can go directly
    // there
    int i = 6;
    for (int j = Math.min(currentPath.getCurrentPathIndex() + i, currentPath.getCurrentPathLength() - 1); j > currentPath.getCurrentPathIndex(); --j) {
      targetPos = currentPath.getVectorFromIndex(theEntity, j);
      if (targetPos.squareDistanceTo(entPos) <= 36.0D && isDirectPathBetweenPoints(entPos, targetPos, 0, 0, 0)) {
        currentPath.setCurrentPathIndex(j);
        break;
      }
    }
    checkForStuck(entPos);
  }

  @Override
  protected boolean isDirectPathBetweenPoints(Vec3 startPos, Vec3 endPos, int sizeX, int sizeY, int sizeZ) {

    Vec3 target = new Vec3(endPos.xCoord, endPos.yCoord + theEntity.height * 0.5D, endPos.zCoord);
    if (!isClear(startPos, target)) {
      return false;
    }
    AxisAlignedBB bb = theEntity.getEntityBoundingBox();
    startPos = new Vec3(bb.maxX, bb.maxY, bb.maxZ);
    if (!isClear(startPos, target)) {
      return false;
    }
    return true;

  }

  private boolean isClear(Vec3 startPos, Vec3 target) {
    MovingObjectPosition hit = worldObj.rayTraceBlocks(startPos, target, true, true, false);
    return hit == null || hit.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
  }

  @Override
  protected void checkForStuck(Vec3 positionVec3) {

    if (totalTicks - ticksAtLastPos > 10 && positionVec3.squareDistanceTo(lastPosCheck) < 0.0625) {
      clearPathEntity();
      ticksAtLastPos = totalTicks;
      lastPosCheck = positionVec3;
      return;
    }

    if (totalTicks - ticksAtLastPos > 50) {
      if (positionVec3.squareDistanceTo(lastPosCheck) < 2.25D) {
        clearPathEntity();
      }

      ticksAtLastPos = totalTicks;
      lastPosCheck = positionVec3;
    }
  }

}
