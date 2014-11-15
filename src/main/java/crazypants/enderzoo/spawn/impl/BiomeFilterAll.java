package crazypants.enderzoo.spawn.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeFilterAll extends AbstractBiomeFilter {

  @Override
  public BiomeGenBase[] getMatchedBiomes() {

    if(types.isEmpty() && names.isEmpty()) {
      return new BiomeGenBase[0];
    }

    List<BiomeGenBase> result = new ArrayList<BiomeGenBase>();
    for (BiomeGenBase candidate : BiomeGenBase.getBiomeGenArray()) {
      if(candidate != null && !isExcluded(candidate) && matchesAll(candidate)) {
        result.add(candidate);
      }
    }
    return result.toArray(new BiomeGenBase[result.size()]);
  }

  private boolean matchesAll(BiomeGenBase candidate) {
    Type[] canTypes = BiomeDictionary.getTypesForBiome(candidate);
    for (BiomeDictionary.Type type : types) {
      if(!contains(canTypes, type)) {
        return false;
      }
    }
    for (String name : names) {
      if(name == null || !name.equals(candidate.biomeName)) {
        return false;
      }
    }
    return true;
  }

  private boolean contains(Type[] in, Type type) {
    for (Type t : in) {
      if(t == type) {
        return true;
      }
    }
    return false;
  }

}
