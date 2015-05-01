package crazypants.enderzoo.gen.structure.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureGenerator;

public class CompositeValidator implements ILocationValidator {

  private final List<ILocationValidator> validators = new ArrayList<ILocationValidator>();
  
  public void add(ILocationValidator rule) {
    validators.add(rule);
  }

  @Override
  public boolean isValidLocation(Structure structure, WorldStructures existingStructures, World world, Random random, int chunkX, int chunkZ) {
    for (ILocationValidator rule : validators) {
      if(!rule.isValidLocation(structure, existingStructures, world, random, chunkX, chunkZ)) {
//        System.out.println("CompositeValidator.isValidLocation: Failed rule: " + rule);
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isValidChunk(StructureGenerator template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (ILocationValidator rule : validators) {
      if(!rule.isValidChunk(template, structures, world, random, chunkX, chunkZ)) {
//        System.out.println("CompositeValidator.isValidChunk: Failed rule: " + rule);
        return false;
      }
    }
    return true;
  }

  public Collection<ILocationValidator> getValidators() {
    return validators;
  }

}
