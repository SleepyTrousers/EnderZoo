package net.minecraft.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;

public class FlyingPathFinder extends PathFinder {
  
  private Path path = new Path();
  private PathPoint[] pathOptions = new PathPoint[32];
  private NodeProcessor nodeProcessor;

  public FlyingPathFinder(NodeProcessor nodeProcessorIn) {
    super(nodeProcessorIn);
    this.nodeProcessor = nodeProcessorIn;
  }

  @Override
  public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityFrom, Entity entityTo, float dist) {   
    return createEntityPathTo(blockaccess, entityFrom, entityTo.posX, entityTo.getEntityBoundingBox().minY, entityTo.posZ, dist);
  }

  @Override
  public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityIn, BlockPos targetPos, float dist) {
    return createEntityPathTo(blockaccess, entityIn, targetPos.getX() + 0.5F, targetPos.getY() + 0.5F, targetPos.getZ() + 0.5F, dist);
  }

  private PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityIn, double x, double y, double z, float distance) {

    path.clearPath();
    nodeProcessor.initProcessor(blockaccess, entityIn);
    
    PathPoint startPoint = nodeProcessor.getPathPointTo(entityIn);
    PathPoint endPoint = nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
    
    Vec3 targ = new Vec3(x, y, z);
    Vec3 ePos = entityIn.getPositionVector();
    double totalDistSq = ePos.squareDistanceTo(targ);
    double totalDist = Math.sqrt(totalDistSq);
    double yDelta = targ.yCoord - ePos.yCoord;

    int climbY = 0;
    if (totalDist > 4 && entityIn.onGround) {
      climbY = 1 * MathHelper.clamp_int((int)(totalDist/8), 1, 3);
      if (yDelta >= 1) {
        climbY += yDelta;
      } else {
        climbY++;
      }
    }

    if (climbY == 0) {
      return createDefault(blockaccess, entityIn, distance, x,y,z);
    }    
    
    List<PathPoint> resPoints = new ArrayList<PathPoint>();
    // climb, then descend
    double climbDistance = Math.min(totalDist / 2.0, climbY);
    Vec3 horizDirVec = new Vec3(targ.xCoord,0,targ.yCoord);
    horizDirVec = horizDirVec.subtract(new Vec3(ePos.xCoord, 0, ePos.yCoord));
    horizDirVec = horizDirVec.normalize();
    Vec3 offset = new Vec3(horizDirVec.xCoord * climbDistance, climbY, horizDirVec.zCoord * climbDistance);
    
    PathPoint climbPoint = new PathPoint(rnd(startPoint.xCoord + offset.xCoord), rnd(startPoint.yCoord + offset.yCoord), rnd(startPoint.zCoord + offset.zCoord));        
    
    PathPoint[] points = addToPath(entityIn, startPoint, climbPoint, distance);    
    nodeProcessor.postProcess();
        
    if (points == null) { //failed to climb so go default      
      return createDefault(blockaccess, entityIn, distance, x,y,z);
    }
    resPoints.addAll(Arrays.asList(points));
    
    //then path from the climb point to destination
    path.clearPath();    
    nodeProcessor.initProcessor(blockaccess, entityIn);
    //climbPoint.index = -1;
    climbPoint = new PathPoint(climbPoint.xCoord, climbPoint.yCoord, climbPoint.zCoord);
    points = addToPath(entityIn, climbPoint, endPoint, distance);
    nodeProcessor.postProcess();

    if (points == null) {
      return createDefault(blockaccess, entityIn, distance, x,y,z);
    }
    resPoints.addAll(Arrays.asList(points));
    if(resPoints.isEmpty()) {
      return null;
    }
    return new PathEntity(resPoints.toArray(new PathPoint[resPoints.size()]));

  }

  private PathPoint[] addToPath(Entity entityIn, PathPoint pathpointStart, PathPoint pathpointEnd, float maxDistance) {
    // set start point values
    pathpointStart.totalPathDistance = 0.0F;
    pathpointStart.distanceToNext = pathpointStart.distanceToSquared(pathpointEnd);
    pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
    pathpointStart.index = -1;

    // clear and add out start point to the path
    path.clearPath();
    path.addPoint(pathpointStart);
    PathPoint curPoint = pathpointStart;

    // while still points in the path
    while (!path.isPathEmpty()) {

      PathPoint dequeued = path.dequeue();

      // we are at the end
      if (dequeued.equals(pathpointEnd)) {
        return createEntityPath(pathpointStart, pathpointEnd);
      }

      // if the dequed point is closer to the ned that our current one, make it
      // the current point
      if (dequeued.distanceToSquared(pathpointEnd) < curPoint.distanceToSquared(pathpointEnd)) {
        curPoint = dequeued;
      }
      dequeued.visited = true;

      // find options for the next point in the path
      int numPathOptions = nodeProcessor.findPathOptions(pathOptions, entityIn, dequeued, pathpointEnd, maxDistance);

      for (int j = 0; j < numPathOptions; ++j) {
        PathPoint cadidatePoint = pathOptions[j];
        float newTotalDistance = dequeued.totalPathDistance + dequeued.distanceToSquared(cadidatePoint);

        if (newTotalDistance < maxDistance * 2.0F && (!cadidatePoint.isAssigned() || newTotalDistance < cadidatePoint.totalPathDistance)) {
          cadidatePoint.previous = dequeued;
          cadidatePoint.totalPathDistance = newTotalDistance;
          cadidatePoint.distanceToNext = cadidatePoint.distanceToSquared(pathpointEnd);
          if (cadidatePoint.isAssigned()) {
            path.changeDistance(cadidatePoint, cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext);
          } else {
            cadidatePoint.distanceToTarget = cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext;
            path.addPoint(cadidatePoint);
          }
        }
      }
         
      PathPoint cadidatePoint = new PathPoint(pathpointEnd.xCoord, pathpointEnd.yCoord, pathpointEnd.zCoord);
      float newTotalDistance = dequeued.totalPathDistance + dequeued.distanceToSquared(cadidatePoint);
      cadidatePoint.previous = dequeued;
      cadidatePoint.totalPathDistance = newTotalDistance;
      cadidatePoint.distanceToNext = cadidatePoint.distanceToSquared(pathpointEnd);
      if (cadidatePoint.isAssigned()) {
        path.changeDistance(cadidatePoint, cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext);
      } else {
        cadidatePoint.distanceToTarget = cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext;
        path.addPoint(cadidatePoint);
      }           
      
      
      
    }

    if (curPoint == pathpointStart) {
      return null;
    } else {
      return createEntityPath(pathpointStart, curPoint);
    }
  }

  private int rnd(double d) {
    return (int)Math.round(d);
  }

  private PathEntity createDefault(IBlockAccess blockaccess, Entity entityIn, float distance, double x, double y, double z) {
    this.path.clearPath();
    this.nodeProcessor.initProcessor(blockaccess, entityIn);
    PathPoint pathpoint = nodeProcessor.getPathPointTo(entityIn);
    PathPoint pathpoint1 =nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
    PathPoint[] p = addToPath(entityIn, pathpoint, pathpoint1, distance);
    PathEntity res;
    if(p == null) {
      res = null;
    } else {
      res = new PathEntity(p);  
    }     
    this.nodeProcessor.postProcess();
    return res;
  }
  
  private static PathPoint[] createEntityPath(PathPoint start, PathPoint end) {
    int i = 1;

    for (PathPoint pathpoint = end; pathpoint.previous != null; pathpoint = pathpoint.previous) {
      ++i;
    }

    PathPoint[] apathpoint = new PathPoint[i];
    PathPoint pathpoint1 = end;
    --i;
    for (apathpoint[i] = end; pathpoint1.previous != null; apathpoint[i] = pathpoint1) {
      pathpoint1 = pathpoint1.previous;
      --i;
    }

    return apathpoint;
  }
}
