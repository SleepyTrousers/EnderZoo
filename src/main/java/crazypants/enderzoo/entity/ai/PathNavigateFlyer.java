package crazypants.enderzoo.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigateFlyer extends PathNavigateSwimmer {

  public PathNavigateFlyer(EntityLiving entitylivingIn, World worldIn) {
    super(entitylivingIn, worldIn);
  }

  @Override
  protected PathFinder getPathFinder() {
    return new PathFinder(new FlyNodeProcessor());
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
    Vec3 entPos = this.getEntityPosition();
    float entWidthSq = this.theEntity.width * this.theEntity.width;
    int i = 6;

    Vec3 targetPos = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());
    double distToCurrTargSq = entPos.squareDistanceTo(targetPos);
    if (distToCurrTargSq < entWidthSq) {
      this.currentPath.incrementPathIndex();
    }

    for (int j = Math.min(this.currentPath.getCurrentPathIndex() + i, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath
        .getCurrentPathIndex(); --j) {
      Vec3 vec31 = this.currentPath.getVectorFromIndex(this.theEntity, j);

      if (vec31.squareDistanceTo(entPos) <= 36.0D && this.isDirectPathBetweenPoints(entPos, vec31, 0, 0, 0)) {
        this.currentPath.setCurrentPathIndex(j);
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

}
