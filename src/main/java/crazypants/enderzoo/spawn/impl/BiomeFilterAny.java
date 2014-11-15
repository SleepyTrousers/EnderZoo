package crazypants.enderzoo.spawn.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeFilterAny extends AbstractBiomeFilter {

  @Override
  public BiomeGenBase[] getMatchedBiomes() {

    if(types.isEmpty() && names.isEmpty()) {
      return new BiomeGenBase[0];
    }

    List<BiomeGenBase> passedBiomes = new ArrayList<BiomeGenBase>();
    for (BiomeDictionary.Type type : types) {
      BiomeGenBase[] biomes = BiomeDictionary.getBiomesForType(type);
      for (BiomeGenBase candidate : biomes) {
        if(!isExcluded(candidate)) {
          passedBiomes.add(candidate);
        }
      }
    }
    for (BiomeGenBase candidate : BiomeGenBase.getBiomeGenArray()) {
      if(candidate != null && names.contains(candidate.biomeName) & !isExcluded(candidate)) {
        passedBiomes.add(candidate);
      }
    }
    return passedBiomes.toArray(new BiomeGenBase[passedBiomes.size()]);
  }



}
