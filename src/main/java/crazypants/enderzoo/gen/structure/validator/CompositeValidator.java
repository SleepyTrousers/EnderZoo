package crazypants.enderzoo.gen.structure.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class CompositeValidator implements ILocationValidator {

  private final List<ILocationValidator> rules = new ArrayList<ILocationValidator>();
  
  public void add(ILocationValidator rule) {
    rules.add(rule);
  }

  @Override
  public boolean isValidLocation(Point3i loc, StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (ILocationValidator rule : rules) {
      if(!rule.isValidLocation(loc, template, structures, world, random, chunkX, chunkZ)) {
//        System.out.println("CompositeValidator.isValidLocation: Failed rule: " + rule);
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isValidChunk(StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (ILocationValidator rule : rules) {
      if(!rule.isValidChunk(template, structures, world, random, chunkX, chunkZ)) {
//        System.out.println("CompositeValidator.isValidChunk: Failed rule: " + rule);
        return false;
      }
    }
    return true;
  }

}
