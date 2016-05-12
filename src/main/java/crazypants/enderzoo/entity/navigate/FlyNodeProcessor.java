package crazypants.enderzoo.entity.navigate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.math.text.translation.AxisAlignedBB;
import net.minecraft.util.math.math.text.translation.BlockPos;
import net.minecraft.util.math.math.text.translation.MathHelper;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class FlyNodeProcessor extends WalkNodeProcessor {

  @Override
  public PathPoint getPathPointTo(Entity entityIn) {
    return openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5D),
        MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
  }

  @Override
  public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double z) {      
    return openPoint(MathHelper.floor_double(x - entityIn.width / 2.0F), MathHelper.floor_double(y), MathHelper.floor_double(z - entityIn.width / 2.0F));       
  }

  @Override
  public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
    int i = 0;
    for (EnumFacing enumfacing : EnumFacing.values()) {
      PathPoint pathpoint = getSafePoint(entityIn, currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(),
          currentPoint.zCoord + enumfacing.getFrontOffsetZ());
      if (pathpoint != null && !pathpoint.visited && (pathpoint.distanceTo(targetPoint) < maxDistance)) {
        pathOptions[i++] = pathpoint;
      }
    }
    return i;
  }

  private PathPoint getSafePoint(Entity entityIn, int x, int y, int z) {
    boolean i = entityFits(entityIn, x, y, z);
    return i ? openPoint(x, y, z) : null;
  }

  private boolean entityFits(Entity entityIn, int x, int y, int z) {

    BlockPos.MutableBlockPos mutableblockpos = new BlockPos.MutableBlockPos();
    for (int i = x; i < x + entitySizeX; ++i) {
      for (int j = y; j < y + entitySizeY; ++j) {
        for (int k = z; k < z + entitySizeZ; ++k) {
          IBlockState bs = blockaccess.getBlockState(mutableblockpos.set(i, j, k));
          Block block = bs.getBlock();
          if (block.getMaterial() != Material.air) {
            AxisAlignedBB bb = block.getCollisionBoundingBox(entityIn.worldObj, mutableblockpos, bs);
            if(bb != null) {
              return false;
            }
          }
        }
      }
    }

    return true;
  }

}
