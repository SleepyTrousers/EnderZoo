package crazypants.enderzoo.spawn.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import crazypants.enderzoo.spawn.IBiomeFilter;
import crazypants.enderzoo.spawn.ISpawnEntry;

public class SpawnEntry implements ISpawnEntry {

  private final String id;
  private final String mobName;
  private final int rate;

  private EnumCreatureType creatureType = EnumCreatureType.monster;
  private int minGroupSize = 1;
  private int maxGroupSize = 3;
  private boolean isRemove = false;

  private final List<IBiomeFilter> filters = new ArrayList<IBiomeFilter>();

  private final List<DimensionFilter> dimFilters = new ArrayList<DimensionFilter>();

  public SpawnEntry(String id, String mobName, int rate) {
    this.id = id;
    this.mobName = mobName;
    this.rate = rate;
  }

  public void addBiomeFilter(IBiomeFilter filter) {
    filters.add(filter);
  }

  @Override
  public List<IBiomeFilter> getFilters() {
    return filters;
  }

  public void addDimensioFilter(DimensionFilter filter) {
    dimFilters.add(filter);
  }

  @Override
  public EnumCreatureType getCreatureType() {
    return creatureType;
  }

  public void setCreatureType(EnumCreatureType creatureType) {
    this.creatureType = creatureType;
  }

  @Override
  public int getMinGroupSize() {
    return minGroupSize;
  }

  public void setMinGroupSize(int minGroupSize) {
    this.minGroupSize = minGroupSize;
  }

  @Override
  public int getMaxGroupSize() {
    return maxGroupSize;
  }

  public void setMaxGroupSize(int maxGroupSize) {
    this.maxGroupSize = maxGroupSize;
  }

  @Override
  public boolean isRemove() {
    return isRemove;
  }

  public void setIsRemove(boolean isRemove) {
    this.isRemove = isRemove;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getMobName() {
    return mobName;
  }

  @Override
  public int getRate() {
    return rate;
  }

  @Override
  public boolean canSpawnInDimension(World world) {
    for (DimensionFilter f : dimFilters) {
      if (!f.canSpawnInDimension(world)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "SpawnEntry [id=" + id + ", mobName=" + mobName + ", rate=" + rate + ", creatureType=" + creatureType + ", minGroupSize=" + minGroupSize
        + ", maxGroupSize=" + maxGroupSize + ", isRemove=" + isRemove + ", filter=" + filters + "]";
  }

}
