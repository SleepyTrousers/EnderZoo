package crazypants.enderzoo.gen.rules;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import crazypants.enderzoo.gen.ChunkBounds;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class LevelGroundRule implements IBuildRule {

  private boolean canSpawnOnWater = false;

  private int maxSampleCount = 100;

  private int sampleSpacing = 4;

  private int tolerance = 2;

  private Border border = new Border();

  public LevelGroundRule() {
    border.setBorderXZ(1);
  }

  @Override
  public boolean isValidChunk(StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    return true;
  }

  @Override
  public boolean isValidLocation(Point3i loc, StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    
    boolean clipOnChunkBounds = true;
    ChunkBounds clip = null;
    if(clipOnChunkBounds) {
      clip = new ChunkBounds(chunkX, chunkZ);
    }

    
    AxisAlignedBB bb = template.getBounds().getOffsetBoundingBox(loc.x, loc.y, loc.z);
    int minX = (int) bb.minX;
    int maxX = (int) bb.maxX;
    int minZ = (int) bb.minZ;
    int maxZ = (int) bb.maxZ;

    Point3i size = template.getSize();

    int numSamsX = Math.max(1, (size.x / sampleSpacing));
    int numSamsZ = Math.max(1, (size.z / sampleSpacing));

    int totalSamples = numSamsX * numSamsZ;
    if(totalSamples > maxSampleCount) {
      double ratio = ((double) maxSampleCount / totalSamples);
      ratio = Math.sqrt(ratio);
      numSamsX = (int) (numSamsX * ratio);
      numSamsZ = (int) (numSamsZ * ratio);
    }

    int xSpacing = Math.round(Math.max(1, (float) size.x / (numSamsX + 1)));
    int zSpacing = Math.round(Math.max(1, (float) size.z / (numSamsZ + 1)));
    
    
    int[] minMax = new int[] { Integer.MAX_VALUE, -Integer.MAX_VALUE };
    Point3i sampleLoc = new Point3i(loc);
    Point3i surfacePos = new Point3i();
    for (int x = minX - border.get(ForgeDirection.WEST); x <= maxX + border.get(ForgeDirection.EAST); x += xSpacing) {
      for (int z = minZ - border.get(ForgeDirection.NORTH); z <= maxZ + border.get(ForgeDirection.SOUTH); z += zSpacing) {

        if(clip == null || clip.isBlockInBounds(x, z)) {
          sampleLoc.set(x, loc.y, z);
          if(!testLocation(sampleLoc, world, minMax, surfacePos)) {
            return false;
          }
        }

        //TODO:  The is not taking into acount that this point migt be clipped due to the border
        //make sure we always get the corners
        if(z < maxZ && z + zSpacing > maxZ) {
          z = maxZ - zSpacing;
        }

      }

      //make sure we always get the corners
      if(x < maxX && x + xSpacing > maxX) {
        x = maxX - xSpacing;
      }

    }

    if(minMax[0] > minMax[1]) { //no hits found
      return false;
    }

    return (minMax[1] - minMax[0]) <= tolerance;
  }

  protected boolean testLocation(Point3i sampleLocation, World world, int[] minMax, Point3i surfacePos) {
    Block blk = StructureUtil.getSurfaceBlock(world, sampleLocation.x, sampleLocation.z,
        Math.max(0, sampleLocation.y - tolerance - 1), Math.min(256, sampleLocation.y + tolerance + 1), surfacePos, true, canSpawnOnWater);
    if(blk != null) {
      if(!canSpawnOnWater && FluidRegistry.lookupFluidForBlock(blk) != null) {
        return false;
      }
      minMax[0] = Math.min(minMax[0], surfacePos.y);
      minMax[1] = Math.max(minMax[1], surfacePos.y);
      if((minMax[1] - minMax[0]) > tolerance) {
        return false;
      }
      return true;
    }
    return false;
  }
}
