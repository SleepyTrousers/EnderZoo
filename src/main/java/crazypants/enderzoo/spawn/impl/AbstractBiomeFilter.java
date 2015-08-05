package crazypants.enderzoo.spawn.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.spawn.IBiomeDescriptor;
import crazypants.enderzoo.spawn.IBiomeFilter;

public abstract class AbstractBiomeFilter implements IBiomeFilter {

  protected final List<BiomeDictionary.Type> types = new ArrayList<BiomeDictionary.Type>();
  protected final List<BiomeDictionary.Type> typeExcludes = new ArrayList<BiomeDictionary.Type>();

  protected final List<String> names = new ArrayList<String>();
  protected final List<String> nameExcludes = new ArrayList<String>();

  @Override
  public void addBiomeDescriptor(IBiomeDescriptor biome) {
    if (biome.getType() != null) {
      if (biome.isExclude()) {
        typeExcludes.add(biome.getType());
      } else {
        types.add(biome.getType());
      }
    } else if (biome.getName() != null) {
      if (biome.isExclude()) {
        nameExcludes.add(biome.getName());
      } else {
        names.add(biome.getName());
      }
    }
  }

  protected boolean isExcluded(BiomeGenBase candidate) {
    for (BiomeDictionary.Type exType : typeExcludes) {
      if (BiomeDictionary.isBiomeOfType(candidate, exType)) {
        if (Config.spawnConfigPrintDetailedOutput) {
          System.out.print("Excluded " + candidate.biomeName + ", ");
        }
        return true;

      }
    }
    for (String exName : nameExcludes) {
      if (exName != null && exName.equals(candidate.biomeName)) {
        System.out.print("Excluded " + candidate.biomeName + ", ");
        return false;
      }
    }
    return false;
  }

}
