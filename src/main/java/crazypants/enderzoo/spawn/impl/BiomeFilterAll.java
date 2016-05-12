package crazypants.enderzoo.spawn.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeFilterAll extends AbstractBiomeFilter {

  @Override
  public BiomeGenBase[] getMatchedBiomes() {

    if (types.isEmpty() && names.isEmpty()) {
      return new BiomeGenBase[0];
    }
    
    Set<BiomeGenBase> result = new HashSet<BiomeGenBase>();
    Iterator<BiomeGenBase> it = BiomeGenBase.biomeRegistry.iterator();
    while(it.hasNext()) {
      BiomeGenBase candidate = it.next();
      if (candidate != null && isMatchingBiome(candidate)) {
        result.add(candidate);
      }
    }    
    return result.toArray(new BiomeGenBase[result.size()]);
  }

  @Override
  public boolean isMatchingBiome(BiomeGenBase biome) {

    if (isExcluded(biome)) {
      return false;
    }
    if (!names.isEmpty() && !names.contains(biome.getBiomeName())) {
      return false;
    }
    for (BiomeDictionary.Type type : types) {
      if (!BiomeDictionary.isBiomeOfType(biome, type)) {
        return false;
      }
    }
    return true;
  }
}
