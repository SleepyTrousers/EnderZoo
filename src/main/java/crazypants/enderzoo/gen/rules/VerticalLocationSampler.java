package crazypants.enderzoo.gen.rules;

import java.util.Random;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class VerticalLocationSampler implements ILocationSampler {

  private static final Random rnd = new Random();
  
  private final int distanceFromSurface;

  public VerticalLocationSampler(int distanceFromSurface) {
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

  protected Point3i getRandomBlock(IBlockAccess world, int chunkX, int chunkZ, int maxOffsetX, int maxOffsetZ, int distanceFromSurface,
      int requiredVerticalSpace) {
    int x = chunkX * 16 + rnd.nextInt(maxOffsetX);
    int z = chunkZ * 16 + rnd.nextInt(maxOffsetZ);

    //Find the surface y
    int y = 256;
    while (world.isAirBlock(x, y, z) && y > 0) {
      --y;
    }

    //Correct for distance from surface
    y += distanceFromSurface;
    if(y > 0 && y < 256 + requiredVerticalSpace) {
      return new Point3i(x, y, z);
    }
    return null;
  }

}
