package crazypants.enderzoo.entity.navigate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FlyNodeProcessor extends WalkNodeProcessor {
 
  @Override
  public PathPoint getStart() {    
    EntityLiving entityIn = entity;
    return openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5D),
        MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
  }

  @Override
  public PathPoint getPathPointToCoords(double x, double y, double z) {
    EntityLiving entityIn = entity;    
    return openPoint(MathHelper.floor_double(x - entityIn.width / 2.0F), MathHelper.floor_double(y), MathHelper.floor_double(z - entityIn.width / 2.0F));       
  }

  @Override
  public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
    EntityLiving entityIn = entity;
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
          if (block.getMaterial(bs) != Material.AIR) {
            AxisAlignedBB bb = block.getCollisionBoundingBox(bs, entityIn.worldObj, mutableblockpos);
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
