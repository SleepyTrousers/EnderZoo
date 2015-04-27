package crazypants.enderzoo.gen.structure.rules;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class SurfaceLocationSampler implements ILocationSampler {

  private static final Random rnd = new Random();

  private int distanceFromSurface = 0;
  private boolean canPlaceInFluid = false;

  public SurfaceLocationSampler() {
  }

  public int getDistanceFromSurface() {
    return distanceFromSurface;
  }

  public void setDistanceFromSurface(int distanceFromSurface) {
    this.distanceFromSurface = distanceFromSurface;
  }

  public boolean isCanPlaceInFluid() {
    return canPlaceInFluid;
  }

  public void setCanGenerateOnFluid(boolean canPlaceInFluid) {
    this.canPlaceInFluid = canPlaceInFluid;
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

  protected Point3i getRandomBlock(World world, int chunkX, int chunkZ, int maxOffsetX, int maxOffsetZ, int distanceFromSurface, int requiredVerticalSpace) {
    int x = chunkX * 16 + rnd.nextInt(maxOffsetX);
    int z = chunkZ * 16 + rnd.nextInt(maxOffsetZ);

    //Find the surface y
    Block blk;
    Point3i loc = new Point3i();

    blk = StructureUtil.getSurfaceBlock(world, x, z, loc, true, !canPlaceInFluid);
    if(blk != world.getBiomeGenForCoords(x, z).topBlock && blk != world.getBiomeGenForCoords(x, z).fillerBlock) {
      return null;
    }

    //Correct for distance from surface
    loc.y += distanceFromSurface;
    if(loc.y > 0 && loc.y < 256 + requiredVerticalSpace) {
      return loc;
    }
    return null;
  }

}
