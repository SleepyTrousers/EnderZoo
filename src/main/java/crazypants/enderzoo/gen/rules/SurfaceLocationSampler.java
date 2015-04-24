package crazypants.enderzoo.gen.rules;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class SurfaceLocationSampler implements ILocationSampler {

  private static final Random rnd = new Random();
  
  private final int distanceFromSurface;

  public SurfaceLocationSampler(int distanceFromSurface) {
    this.distanceFromSurface = distanceFromSurface;
  }

  @Override
  public Point3i generateCandidateLocation(StructureTemplate template, WorldStructures structures, World world,
      Random random, int chunkX, int chunkZ) {

    return findStartPos(template, chunkX, chunkZ, world);
  }
  
  protected Point3i findStartPos(StructureTemplate template, int chunkX, int chunkZ, World world) {
    Point3i candidate;
    if(template.canSpanChunks()) {
      candidate = getRandomBlock(world, chunkX, chunkZ, 16, 16, distanceFromSurface, template.getSize().y);
    } else {
      candidate = getRandomBlock(world, chunkX, chunkZ, 16 - template.getSize().x, 16 - template.getSize().z, distanceFromSurface, template.getSize().y);
    }

    return candidate;
  }

  protected Point3i getRandomBlock(World world, int chunkX, int chunkZ, int maxOffsetX, int maxOffsetZ, int distanceFromSurface,
      int requiredVerticalSpace) {
    int x = chunkX * 16 + rnd.nextInt(maxOffsetX);
    int z = chunkZ * 16 + rnd.nextInt(maxOffsetZ);

    //Find the surface y
    int y = 256;
    Block blk = world.getBlock(x, y, z);
    while (isIgnored(world, x, z, y, blk) && y > 0) {
      --y;
      blk = world.getBlock(x, y, z);
    }

    if(blk != world.getBiomeGenForCoords(x, z).topBlock) {
      return null;
    }

    //Correct for distance from surface
    y += distanceFromSurface;
    if(y > 0 && y < 256 + requiredVerticalSpace) {
      return new Point3i(x, y, z);
    }
    return null;
  }

  protected boolean isIgnored(World world, int x, int z, int y, Block blk) {
    return blk == Blocks.snow_layer || blk == Blocks.web || blk.isAir(world, x, y, z) || StructureUtil.isPlant(blk, world, x, y, z);
  }


}
