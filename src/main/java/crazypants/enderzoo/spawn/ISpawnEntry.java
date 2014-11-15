package crazypants.enderzoo.spawn;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;

public interface ISpawnEntry {

  String getId();

  String getMobName();

  EnumCreatureType getCreatureType();

  int getRate();

  int getMaxGroupSize();

  int getMinGroupSize();

  boolean isRemove();

  List<IBiomeFilter> getFilters();

}
