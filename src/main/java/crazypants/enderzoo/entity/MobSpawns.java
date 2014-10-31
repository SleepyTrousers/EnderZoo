package crazypants.enderzoo.entity;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import cpw.mods.fml.common.registry.EntityRegistry;
import crazypants.enderzoo.config.Config;

public class MobSpawns {

  private BiomeDictionary.Type[] BASE_LAND_TYPES = new BiomeDictionary.Type[] {
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

  public MobSpawns() {
  }

  public void registerSpawns() {    
    addSpawn(MobInfo.ENDERMINY, Config.enderminySpawnRate, 1, Config.enderminyMaxGroupSize, BiomeDictionary.Type.FOREST, Type.SWAMP, Type.JUNGLE);         
    addSpawn(MobInfo.CONCUSSION_CREEPER, Config.concussionCreeperSpawnRate, 1, 2, BASE_LAND_TYPES);
    addSpawn(MobInfo.DARK_KNIGHT, Config.fallenKnightSpawnRate, 1, 2, BiomeDictionary.Type.PLAINS);
  }

  private void addSpawn(MobInfo mob, int rate, int minGroupSize, int maxGroupSize, Type... types) {
    if(!mob.isEnabled()) {
      return;
    }
    for (BiomeDictionary.Type type : types) {
      BiomeGenBase[] biomes = BiomeDictionary.getBiomesForType(type);
      EntityRegistry.addSpawn(mob.getClz(), rate, minGroupSize, maxGroupSize, EnumCreatureType.monster, biomes);
    }
  }

}
