package crazypants.enderzoo.spawn.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeFilterAll extends AbstractBiomeFilter {

  @Override
  public BiomeGenBase[] getMatchedBiomes() {

    if(types.isEmpty() && names.isEmpty()) {
      return new BiomeGenBase[0];
    }
    Set<BiomeGenBase> result = new HashSet<BiomeGenBase>();
    for (BiomeGenBase candidate : BiomeGenBase.getBiomeGenArray()) {
      if(isMatchingBiome(candidate)) {
        result.add(candidate);
      }
    }
    return result.toArray(new BiomeGenBase[result.size()]);
  }
  
  @Override
  public boolean isMatchingBiome(BiomeGenBase biome) {
    
    if(isExcluded(biome)) {
      return false;
    }
    if(!names.contains(biome.biomeName)) {
      return false;
    }
    for(BiomeDictionary.Type type : types) {
      if(!BiomeDictionary.isBiomeOfType(biome, type)) {
        return false;
      }
    }   
    return true; 
  }
}
