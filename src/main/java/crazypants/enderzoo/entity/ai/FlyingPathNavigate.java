package crazypants.enderzoo.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.FlyingPathFinder;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

//public class FlyingPathNavigate extends PathNavigateSwimmer {
public class FlyingPathNavigate extends PathNavigateGround {

  private int totalTicks;
  private int ticksAtLastPos;
  private Vec3 lastPosCheck = new Vec3(0.0D, 0.0D, 0.0D);

  public FlyingPathNavigate(EntityLiving entitylivingIn, World worldIn) {
    super(entitylivingIn, worldIn);
  }

  @Override
  protected PathFinder getPathFinder() {
    return new FlyingPathFinder(new FlyNodeProcessor());
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

  @Override
  public boolean setPath(PathEntity path, double speed) {
    if (super.setPath(path, speed)) {
//      String str = "FlyingPathNavigate.setPath:";
//      for (int i = 0; i < path.getCurrentPathLength(); i++) {
//        PathPoint pp = path.getPathPointFromIndex(i);
//        str += " [" + pp + "]";
//      }
//      Log.info(str);
      ticksAtLastPos = this.totalTicks;
      lastPosCheck = getEntityPosition();
      return true;
    }
    return false;
  }

  @Override
  public void onUpdateNavigation() {

    ++this.totalTicks;

    if (!noPath()) { // if we have a path
      if (canNavigate()) { // and can navigate it
        pathFollow(); // follow it
      } else if (currentPath != null && currentPath.getCurrentPathIndex() < currentPath.getCurrentPathLength()) {
        // Cant navigate the current path
        Vec3 entPosV = getEntityPosition();
        Vec3 targetV = currentPath.getVectorFromIndex(theEntity, currentPath.getCurrentPathIndex());
        // if the ent is higher, but on the same block xz, move to the next path
        // index
        if (entPosV.yCoord > targetV.yCoord && !theEntity.onGround && MathHelper.floor_double(entPosV.xCoord) == MathHelper.floor_double(targetV.xCoord)
            && MathHelper.floor_double(entPosV.zCoord) == MathHelper.floor_double(targetV.zCoord)) {
          currentPath.setCurrentPathIndex(currentPath.getCurrentPathIndex() + 1);
        }
      }

      if (!noPath()) { // if we have a path
        Vec3 targetPos = currentPath.getPosition(theEntity);
        if (targetPos == null) {
          return;
        }
        theEntity.getMoveHelper().setMoveTo(targetPos.xCoord, targetPos.yCoord, targetPos.zCoord, speed);
      }
    }

  }

  @Override
  protected void pathFollow() {
   
    Vec3 entPos = getEntityPosition();
    float entWidthSq = theEntity.width * theEntity.width;
    if (currentPath.getCurrentPathIndex() == currentPath.getCurrentPathLength() - 1 && theEntity.onGround) {
      entWidthSq = 0.01f; // we need to be right on top of the last point if on the ground so we don't hang on ledges
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
    
    if (totalTicks - ticksAtLastPos > 5 && positionVec3.squareDistanceTo(lastPosCheck) < 0.25D) {      
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
