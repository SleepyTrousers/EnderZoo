package crazypants.enderzoo.spawn.impl;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeFilterAny extends AbstractBiomeFilter {

  @Override
  public BiomeGenBase[] getMatchedBiomes() {

    if (types.isEmpty() && names.isEmpty()) {
      return new BiomeGenBase[0];
    }
    Set<BiomeGenBase> passedBiomes = new HashSet<BiomeGenBase>();
    for (BiomeGenBase candidate : BiomeGenBase.getBiomeGenArray()) {
      if (candidate != null && isMatchingBiome(candidate)) {
        passedBiomes.add(candidate);
      }
    }

    return passedBiomes.toArray(new BiomeGenBase[passedBiomes.size()]);
  }

  @Override
  public boolean isMatchingBiome(BiomeGenBase biome) {
    if (isExcluded(biome)) {
      return false;
    }
    if (names.contains(biome.biomeName)) {
      return true;
    }
    for (BiomeDictionary.Type type : types) {
      if (BiomeDictionary.isBiomeOfType(biome, type)) {
        return true;
      }
    }
    return false;
  }

}
