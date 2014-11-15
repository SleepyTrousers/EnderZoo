package crazypants.enderzoo.spawn;

import net.minecraftforge.common.BiomeDictionary;

public interface IBiomeDescriptor {

  BiomeDictionary.Type getType();

  String getName();

  boolean isExclude();

}
