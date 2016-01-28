package crazypants.enderzoo.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.FlyingPathFinder;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class FlyingPathNavigate extends PathNavigateSwimmer {

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
    return new Vec3(this.theEntity.posX, this.theEntity.posY + 1, this.theEntity.posZ);
  }

  @Override
  public void onUpdateNavigation() {
    super.onUpdateNavigation();
  }

  @Override
  protected void pathFollow() {
    Vec3 entPos = getEntityPosition();
    float entWidthSq = theEntity.width * theEntity.width;
    int i = 6;

    Vec3 targetPos = this.currentPath.getVectorFromIndex(theEntity, currentPath.getCurrentPathIndex());
    double distToCurrTargSq = entPos.squareDistanceTo(targetPos);
    if (distToCurrTargSq < entWidthSq) {
      this.currentPath.incrementPathIndex();
    }

    for (int j = Math.min(currentPath.getCurrentPathIndex() + i, currentPath.getCurrentPathLength() - 1); j > currentPath.getCurrentPathIndex(); --j) {
      Vec3 vec31 = currentPath.getVectorFromIndex(this.theEntity, j);

      if (vec31.squareDistanceTo(entPos) <= 36.0D && this.isDirectPathBetweenPoints(entPos, vec31, 0, 0, 0)) {
        currentPath.setCurrentPathIndex(j);
        break;
      }
    }

    this.checkForStuck(entPos);
  }

  @Override
  protected boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
    MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(posVec31,
        new Vec3(posVec32.xCoord, posVec32.yCoord + this.theEntity.height * 0.5D, posVec32.zCoord), false, true, false);
    return movingobjectposition == null || movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
  }

  // private static class FlyingPathFinder extends PathFinder {
  //
  // public FlyingPathFinder() {
  // super(new FlyNodeProcessor());
  // }
  //
  // private Path path = new Path();
  // private PathPoint[] pathOptions = new PathPoint[32];
  // private NodeProcessor nodeProcessor;
  //
  // public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity
  // entityFrom, Entity entityTo, float dist)
  // {
  // return this.createEntityPathTo(blockaccess, entityFrom, entityTo.posX,
  // entityTo.getEntityBoundingBox().minY, entityTo.posZ, dist);
  // }
  //
  // public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity
  // entityIn, BlockPos targetPos, float dist)
  // {
  // return this.createEntityPathTo(blockaccess, entityIn,
  // (double)((float)targetPos.getX() + 0.5F), (double)((float)targetPos.getY()
  // + 0.5F), (double)((float)targetPos.getZ() + 0.5F), dist);
  // }
  //
  // private PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity
  // entityIn, double x, double y, double z, float distance)
  // {
  // this.path.clearPath();
  // this.nodeProcessor.initProcessor(blockaccess, entityIn);
  // PathPoint pathpoint = this.nodeProcessor.getPathPointTo(entityIn);
  // PathPoint pathpoint1 = this.nodeProcessor.getPathPointToCoords(entityIn, x,
  // y, z);
  // PathEntity pathentity = this.addToPath(entityIn, pathpoint, pathpoint1,
  // distance);
  // this.nodeProcessor.postProcess();
  // return pathentity;
  // }
  //
  // private PathEntity addToPath(Entity entityIn, PathPoint pathpointStart,
  // PathPoint pathpointEnd, float maxDistance)
  // {
  // pathpointStart.totalPathDistance = 0.0F;
  // pathpointStart.distanceToNext =
  // pathpointStart.distanceToSquared(pathpointEnd);
  // pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
  // this.path.clearPath();
  // this.path.addPoint(pathpointStart);
  // PathPoint pathpoint = pathpointStart;
  //
  // while (!this.path.isPathEmpty())
  // {
  // PathPoint pathpoint1 = this.path.dequeue();
  //
  // if (pathpoint1.equals(pathpointEnd))
  // {
  // return this.createEntityPath(pathpointStart, pathpointEnd);
  // }
  //
  // if (pathpoint1.distanceToSquared(pathpointEnd) <
  // pathpoint.distanceToSquared(pathpointEnd))
  // {
  // pathpoint = pathpoint1;
  // }
  //
  // pathpoint1.visited = true;
  // int i = this.nodeProcessor.findPathOptions(this.pathOptions, entityIn,
  // pathpoint1, pathpointEnd, maxDistance);
  //
  // for (int j = 0; j < i; ++j)
  // {
  // PathPoint pathpoint2 = this.pathOptions[j];
  // float f = pathpoint1.totalPathDistance +
  // pathpoint1.distanceToSquared(pathpoint2);
  //
  // if (f < maxDistance * 2.0F && (!pathpoint2.isAssigned() || f <
  // pathpoint2.totalPathDistance))
  // {
  // pathpoint2.previous = pathpoint1;
  // pathpoint2.totalPathDistance = f;
  // pathpoint2.distanceToNext = pathpoint2.distanceToSquared(pathpointEnd);
  //
  // if (pathpoint2.isAssigned())
  // {
  // this.path.changeDistance(pathpoint2, pathpoint2.totalPathDistance +
  // pathpoint2.distanceToNext);
  // }
  // else
  // {
  // pathpoint2.distanceToTarget = pathpoint2.totalPathDistance +
  // pathpoint2.distanceToNext;
  // this.path.addPoint(pathpoint2);
  // }
  // }
  // }
  // }
  //
  // if (pathpoint == pathpointStart)
  // {
  // return null;
  // }
  // else
  // {
  // return this.createEntityPath(pathpointStart, pathpoint);
  // }
  // }
  //
  // private PathEntity createEntityPath(PathPoint start, PathPoint end)
  // {
  // int i = 1;
  //
  // for (PathPoint pathpoint = end; pathpoint.previous != null; pathpoint =
  // pathpoint.previous)
  // {
  // ++i;
  // }
  //
  // PathPoint[] apathpoint = new PathPoint[i];
  // PathPoint pathpoint1 = end;
  // --i;
  //
  // for (apathpoint[i] = end; pathpoint1.previous != null; apathpoint[i] =
  // pathpoint1)
  // {
  // pathpoint1 = pathpoint1.previous;
  // --i;
  // }
  //
  // return new PathEntity(apathpoint);
  // }
  //
  //
  //
  // }

}
