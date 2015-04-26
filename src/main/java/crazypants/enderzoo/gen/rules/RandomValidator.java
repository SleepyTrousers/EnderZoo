package crazypants.enderzoo.gen.rules;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class RandomValidator implements ILocationValidator {

  private final float chancePerChunk;

  public RandomValidator(float chancePerChunk) {
    this.chancePerChunk = chancePerChunk;
  }

  @Override
  public boolean isValidChunk(StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    if(random.nextFloat() <= chancePerChunk) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isValidLocation(Point3i loc, StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    return true;
  }

}
