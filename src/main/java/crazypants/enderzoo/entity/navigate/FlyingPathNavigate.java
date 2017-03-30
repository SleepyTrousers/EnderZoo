package crazypants.enderzoo.entity.navigate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlyingPathNavigate extends PathNavigateGround {

  private int totalTicks;
  private int ticksAtLastPos;
  private Vec3d lastPosCheck = new Vec3d(0.0D, 0.0D, 0.0D);

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
  protected Vec3d getEntityPosition() {
    int y = (int) (theEntity.getEntityBoundingBox().minY + 0.5D);
    return new Vec3d(theEntity.posX, y, theEntity.posZ);
  }

  public boolean tryFlyToXYZ(double x, double y, double z, double speedIn) {
    Path pathentity = getPathToPos(new BlockPos((double) MathHelper.floor(x), (double) ((int) y), (double) MathHelper.floor(z)));
    return setPath(pathentity, speedIn, true);
  }

  public boolean tryFlyToPos(double x, double y, double z, double speedIn) {
    Path pathentity = getPathToXYZ(x, y, z);
    return setPath(pathentity, speedIn, true);
  }

  public boolean tryFlyToEntityLiving(Entity entityIn, double speedIn) {
    Path pathentity = getPathToEntityLiving(entityIn);
    return pathentity != null ? setPath(pathentity, speedIn, true) : false;
  }

  public boolean setPath(Path path, double speed, boolean forceFlying) {
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
  public boolean setPath(Path path, double speed) {
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
        Vec3d targetPos = currentPath.getPosition(theEntity);
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

    Vec3d entPos = getEntityPosition();
    float entWidthSq = theEntity.width * theEntity.width;
    if (currentPath.getCurrentPathIndex() == currentPath.getCurrentPathLength() - 1 && theEntity.onGround) {
      entWidthSq = 0.01f; // we need to be right on top of the last point if on
                          // the ground so we don't hang on ledges
    }

    Vec3d targetPos = currentPath.getVectorFromIndex(theEntity, currentPath.getCurrentPathIndex());

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
  protected boolean isDirectPathBetweenPoints(Vec3d startPos, Vec3d endPos, int sizeX, int sizeY, int sizeZ) {

    Vec3d target = new Vec3d(endPos.xCoord, endPos.yCoord + theEntity.height * 0.5D, endPos.zCoord);
    if (!isClear(startPos, target)) {
      return false;
    }
    AxisAlignedBB bb = theEntity.getEntityBoundingBox();
    startPos = new Vec3d(bb.maxX, bb.maxY, bb.maxZ);
    if (!isClear(startPos, target)) {
      return false;
    }
    return true;

  }

  private boolean isClear(Vec3d startPos, Vec3d target) {
    RayTraceResult hit = world.rayTraceBlocks(startPos, target, true, true, false);
    return hit == null || hit.typeOfHit == RayTraceResult.Type.MISS;
  }

  @Override
  protected void checkForStuck(Vec3d positionVec3) {

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
