package crazypants.enderzoo.entity.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import crazypants.enderzoo.entity.SpawnUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class FlyingPathFinder extends PathFinder {
  
  private Path path = new Path();
  private PathPoint[] pathOptions = new PathPoint[32];
  private NodeProcessor nodeProcessor;

  public FlyingPathFinder(NodeProcessor nodeProcessorIn) {
    super(nodeProcessorIn);
    this.nodeProcessor = nodeProcessorIn;
  }

  //createEntityPathTo
  @Override
  public PathEntity func_186333_a(IBlockAccess blockaccess, EntityLiving entityFrom, Entity entityTo, float dist) {   
    return createEntityPathTo(blockaccess, entityFrom, entityTo.posX, entityTo.getEntityBoundingBox().minY, entityTo.posZ, dist);
  }

//createEntityPathTo
  @Override
  public PathEntity func_186336_a(IBlockAccess blockaccess, EntityLiving entityIn, BlockPos targetPos, float dist) {
    return createEntityPathTo(blockaccess, entityIn, targetPos.getX() + 0.5F, targetPos.getY() + 0.5F, targetPos.getZ() + 0.5F, dist);
  }

  private PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity ent, double x, double y, double z, float distance) {

    path.clearPath();
    
    
    if(! (ent instanceof EntityLiving)) {
      return null;
    }
    EntityLiving entityIn = (EntityLiving)ent;
    
    //nodeProcessor.initProcessor(blockaccess, entityIn);
    nodeProcessor.func_186315_a(blockaccess, (EntityLiving) entityIn);
    
    
//    PathPoint startPoint = nodeProcessor.getPathPointTo(entityIn); //1.8
//    PathPoint endPoint = nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
    PathPoint startPoint = nodeProcessor.func_186318_b();
    PathPoint endPoint = nodeProcessor.func_186325_a(x, y, z);
    
    
    Vec3d targ = new Vec3d(x, y, z);
    Vec3d ePos = entityIn.getPositionVector();
    double yDelta = targ.yCoord - ePos.yCoord;

    double horizDist = new Vec3d(x,0,z).distanceTo(new Vec3d(ePos.xCoord,0,ePos.zCoord));
    
    int climbY = 0;
    if (horizDist > 4 && entityIn.onGround) {
      climbY = 1 * MathHelper.clamp_int((int)(horizDist/8), 1, 3);
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
    double climbDistance = Math.min(horizDist / 2.0, climbY);

    Vec3d horizDirVec = new Vec3d(targ.xCoord,0,targ.zCoord);
    horizDirVec = horizDirVec.subtract(new Vec3d(ePos.xCoord, 0, ePos.zCoord));
    horizDirVec = horizDirVec.normalize();
    Vec3d offset = new Vec3d(horizDirVec.xCoord * climbDistance, climbY, horizDirVec.zCoord * climbDistance);
    
    PathPoint climbPoint = new PathPoint(rnd(startPoint.xCoord + offset.xCoord), rnd(startPoint.yCoord + offset.yCoord), rnd(startPoint.zCoord + offset.zCoord));        
    if(!SpawnUtil.isSpaceAvailableForSpawn(entityIn.worldObj, (EntityLiving)entityIn, false)) {
      return createDefault(blockaccess, entityIn, distance, x,y,z);
    }
    
    PathPoint[] points = addToPath(entityIn, startPoint, climbPoint, distance);    
    nodeProcessor.postProcess();
        
    if (points == null) { //failed to climb so go default      
      return createDefault(blockaccess, entityIn, distance, x,y,z);
    }
    resPoints.addAll(Arrays.asList(points));
    
    //then path from the climb point to destination
    path.clearPath();    
    nodeProcessor.func_186315_a(blockaccess, (EntityLiving) entityIn);
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
    
//    pathpointStart.totalPathDistance = 0.0F;
//    pathpointStart.distanceToNext = pathpointStart.distanceToSquared(pathpointEnd);
//    pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
//    pathpointStart.index = -1;        
    PPUtil.setTotalPathDistance(pathpointStart, 0f);
    float dist = pathpointStart.distanceToSquared(pathpointEnd);
    PPUtil.setDistanceToNext(pathpointStart, dist);
    PPUtil.setDistanceToTarget(pathpointStart, dist);
    PPUtil.setIndex(pathpointStart, -1);
    

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
      
      int numPathOptions = nodeProcessor.func_186320_a(pathOptions, dequeued, pathpointEnd, maxDistance);
      //int numPathOptions = nodeProcessor.findPathOptions(pathOptions, entityIn, dequeued, pathpointEnd, maxDistance);

      for (int j = 0; j < numPathOptions; ++j) {
        PathPoint cadidatePoint = pathOptions[j];        
        float newTotalDistance = PPUtil.getTotalPathDistance(dequeued) + dequeued.distanceToSquared(cadidatePoint);
        if (newTotalDistance < maxDistance * 2.0F && (!cadidatePoint.isAssigned() || newTotalDistance < PPUtil.getTotalPathDistance(cadidatePoint))) {
          //cadidatePoint.previous = dequeued;
          PPUtil.setPrevious(cadidatePoint, dequeued);
          //cadidatePoint.totalPathDistance = newTotalDistance;
          PPUtil.setTotalPathDistance(cadidatePoint, newTotalDistance);        
          //cadidatePoint.distanceToNext = cadidatePoint.distanceToSquared(pathpointEnd);
          PPUtil.setDistanceToNext(cadidatePoint, cadidatePoint.distanceToSquared(pathpointEnd));
          if (cadidatePoint.isAssigned()) {
            //path.changeDistance(cadidatePoint, cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext);
            path.changeDistance(cadidatePoint, PPUtil.getTotalPathDistance(cadidatePoint) + PPUtil.getDistanceToNext(cadidatePoint));
          } else {            
            PPUtil.setDistanceToTarget(cadidatePoint, PPUtil.getTotalPathDistance(cadidatePoint) + PPUtil.getDistanceToNext(cadidatePoint));
            path.addPoint(cadidatePoint);
          }
        }
      }
         
//      PathPoint cadidatePoint = new PathPoint(pathpointEnd.xCoord, pathpointEnd.yCoord, pathpointEnd.zCoord);
//      float newTotalDistance = dequeued.totalPathDistance + dequeued.distanceToSquared(cadidatePoint);
//      cadidatePoint.previous = dequeued;
//      cadidatePoint.totalPathDistance = newTotalDistance;
//      cadidatePoint.distanceToNext = cadidatePoint.distanceToSquared(pathpointEnd);
//      if (cadidatePoint.isAssigned()) {
//        path.changeDistance(cadidatePoint, cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext);
//      } else {
//        cadidatePoint.distanceToTarget = cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext;
//        path.addPoint(cadidatePoint);
//      }                 
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

  private PathEntity createDefault(IBlockAccess blockaccess, EntityLiving entityIn, float distance, double x, double y, double z) {
    this.path.clearPath();
    this.nodeProcessor.func_186315_a(blockaccess, entityIn);
    
//    PathPoint pathpoint = nodeProcessor.getPathPointTo(entityIn);
//    PathPoint pathpoint1 =nodeProcessor.getPathPointToCoords(entityIn, x, y, z);    
    PathPoint pathpoint = nodeProcessor.func_186318_b();
    PathPoint pathpoint1 = nodeProcessor.func_186325_a(x, y, z);
    
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

    
    for (PathPoint pathpoint = end; PPUtil.getPrevious(pathpoint) != null; pathpoint = PPUtil.getPrevious(pathpoint)) {
      ++i;
    }

    PathPoint[] apathpoint = new PathPoint[i];
    PathPoint pathpoint1 = end;
    --i;
    for (apathpoint[i] = end; PPUtil.getPrevious(pathpoint1) != null; apathpoint[i] = pathpoint1) {
      pathpoint1 = PPUtil.getPrevious(pathpoint1);
      --i;
    }

    return apathpoint;
  }
}
