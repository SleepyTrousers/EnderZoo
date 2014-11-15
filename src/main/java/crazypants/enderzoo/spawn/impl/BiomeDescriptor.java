package crazypants.enderzoo.spawn.impl;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import crazypants.enderzoo.spawn.IBiomeDescriptor;

public class BiomeDescriptor implements IBiomeDescriptor {

  private final String name;
  private final BiomeDictionary.Type type;
  private final boolean isExclude;

  public BiomeDescriptor(Type type, boolean isExclude) {
    name = null;
    this.type = type;
    this.isExclude = isExclude;
  }

  public BiomeDescriptor(String name, boolean isExclude) {
    this.name = name;
    type = null;
    this.isExclude = isExclude;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public BiomeDictionary.Type getType() {
    return type;
  }

  @Override
  public boolean isExclude() {
    return isExclude;
  }

}
