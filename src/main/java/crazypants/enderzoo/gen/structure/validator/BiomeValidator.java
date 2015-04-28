package crazypants.enderzoo.gen.structure.validator;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.spawn.IBiomeFilter;
import crazypants.enderzoo.spawn.impl.BiomeFilterAny;
import crazypants.enderzoo.vec.Point3i;

public class BiomeValidator implements ILocationValidator {

  private final IBiomeFilter filter;
  
  public BiomeValidator(IBiomeFilter filter) {
    this.filter = filter;
  }

  @Override
  public boolean isValidChunk(StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {                
    BiomeGenBase bgb = world.getBiomeGenForCoords((chunkX << 4) + 1, (chunkZ << 4) + 1);            
    return filter.isMatchingBiome(bgb);
  }

  @Override
  public boolean isValidLocation(Point3i loc, StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    return true;
  }
  
  
}
