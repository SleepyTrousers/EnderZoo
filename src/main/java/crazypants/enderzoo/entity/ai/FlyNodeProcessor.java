package crazypants.enderzoo.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class FlyNodeProcessor extends SwimNodeProcessor {

  @Override
  public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
    int i = 0;

    for (EnumFacing enumfacing : EnumFacing.values()) {
      PathPoint pathpoint = this.getSafePoint(entityIn, currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(),
          currentPoint.zCoord + enumfacing.getFrontOffsetZ());
      if (pathpoint != null && !pathpoint.visited && (pathpoint.distanceTo(targetPoint) < maxDistance || true)) {
        pathOptions[i++] = pathpoint;
      }
    }
    return i;
  }

  private PathPoint getSafePoint(Entity entityIn, int x, int y, int z) {
     int i = this.func_176186_b(entityIn, x, y, z);
     return i == -1 ? this.openPoint(x, y, z) : null;
  }

  private int func_176186_b(Entity entityIn, int x, int y, int z) {

    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

    for (int i = x; i < x + this.entitySizeX; ++i) {
      for (int j = y; j < y + this.entitySizeY; ++j) {
        for (int k = z; k < z + this.entitySizeZ; ++k) {
          Block block = this.blockaccess.getBlockState(blockpos$mutableblockpos.set(i, j, k)).getBlock();
          if (block.getMaterial() != Material.air) {
            return 0;
          }
        }
      }
    }

    return -1;
  }

}
