package crazypants.enderzoo.gen.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class CompositeBuildRule implements IBuildRule {

  private final List<IBuildRule> rules = new ArrayList<IBuildRule>();
  
  public void add(IBuildRule rule) {
    rules.add(rule);
  }

  @Override
  public boolean isValidLocation(Point3i loc, StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (IBuildRule rule : rules) {
      if(!rule.isValidLocation(loc, template, structures, world, random, chunkX, chunkZ)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isValidChunk(StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (IBuildRule rule : rules) {
      if(!rule.isValidChunk(template, structures, world, random, chunkX, chunkZ)) {
        return false;
      }
    }
    return true;
  }

}
