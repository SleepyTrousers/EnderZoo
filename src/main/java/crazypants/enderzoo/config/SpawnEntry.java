package crazypants.enderzoo.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import crazypants.enderzoo.entity.MobInfo;

public class SpawnEntry {

  private static final BiomeDictionary.Type[] BASE_LAND_TYPES = new BiomeDictionary.Type[] {
      BiomeDictionary.Type.MESA,
      BiomeDictionary.Type.FOREST,
      BiomeDictionary.Type.PLAINS,
      BiomeDictionary.Type.MOUNTAIN,
      BiomeDictionary.Type.HILLS,
      BiomeDictionary.Type.SWAMP,
      BiomeDictionary.Type.SANDY,
      BiomeDictionary.Type.SNOWY,
      BiomeDictionary.Type.WASTELAND,
      BiomeDictionary.Type.BEACH,
  };
  
  private final String id;
  private final String mobName;
  private final int rate;
  
  private EnumCreatureType creatureType = EnumCreatureType.monster;  
  private int minGroupSize = 1;
  private int maxGroupSize = 3;
  private boolean isRemove = false;
  
  final List<BiomeDictionary.Type> types = new ArrayList<BiomeDictionary.Type>();
  
  public SpawnEntry(String id, String mobName, int rate) {    
    this.id = id;
    this.mobName = mobName;
    this.rate = rate;
  }

  public void addBiomeType(BiomeDictionary.Type type) {
    types.add(type);
  }
  
  public void addBaseLandTypes() {
    for(BiomeDictionary.Type type : BASE_LAND_TYPES) {
      addBiomeType(type);  
    }
  }
  
  public BiomeDictionary.Type[] getBiomeTypeArray() {
    return types.toArray(new BiomeDictionary.Type[types.size()]);
  }  
  
  public EnumCreatureType getCreatureType() {
    return creatureType;
  }

  public void setCreatureType(EnumCreatureType creatureType) {
    this.creatureType = creatureType;
  }

  public int getMinGroupSize() {
    return minGroupSize;
  }

  public void setMinGroupSize(int minGroupSize) {
    this.minGroupSize = minGroupSize;
  }

  public int getMaxGroupSize() {
    return maxGroupSize;
  }

  public void setMaxGroupSize(int maxGroupSize) {
    this.maxGroupSize = maxGroupSize;
  }

  public boolean isRemove() {
    return isRemove;
  }

  public void setIsRemove(boolean isRemove) {
    this.isRemove = isRemove;
  }

  public String getId() {
    return id;
  }

  public String getMobName() {
    return mobName;
  }

  public int getRate() {
    return rate;
  }

  public List<BiomeDictionary.Type> getTypes() {
    return types;
  }

  @Override
  public String toString() {
    return "SpawnEntry [id=" + id + ", mobName=" + mobName + ", rate=" + rate + ", creatureType=" + creatureType + ", minGroupSize=" + minGroupSize
        + ", maxGroupSize=" + maxGroupSize + ", isRemove=" + isRemove + ", types=" + types + "]";
  }

}
