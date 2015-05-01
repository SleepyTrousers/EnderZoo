package crazypants.enderzoo.gen.structure.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureGenerator;

public class DimensionValidator implements ILocationValidator {

  private Set<String> includes = new HashSet<String>();
  private Set<String> excludes = new HashSet<String>();
  
  public DimensionValidator() {    
  }
  
  public void addDimension(String name, boolean isExclude) {
    if(name == null) {
      return;
    }
    if(isExclude) {
      excludes.add(name);
    } else {
      includes.add(name);
    }
  }
  
  public void addAll(List<String> dimensions, boolean isExclude) {
    if(dimensions == null) {
      return;      
    }
    for(String dim : dimensions) {
      addDimension(dim, isExclude);
    }    
  }
  
  @Override
  public boolean isValidChunk(StructureGenerator template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    String bName = world.getBiomeGenForCoords(chunkX, chunkZ).biomeName;
    if(!includes.isEmpty() && !includes.contains(bName)) {
      return false;
    }    
    return !excludes.contains(bName);
  }

  @Override
  public boolean isValidLocation(Structure structure, WorldStructures existingStructures, World world, Random random, int chunkX, int chunkZ) {
    return true;
  }

  

}
