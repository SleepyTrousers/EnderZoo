package crazypants.enderzoo.spawn;

import java.io.File;
import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import cpw.mods.fml.common.registry.EntityRegistry;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.config.SpawnConfig;
import crazypants.enderzoo.config.SpawnEntry;
import crazypants.enderzoo.entity.MobInfo;

public class MobSpawns {

  private static final boolean PRINT_DEBUG = false;

  private MobSpawns() {
  }

  public static void registerSpawns() {
    List<SpawnEntry> entries = SpawnConfig.loadSpawnConfig();
    Log.info("Applying " + entries.size() + " spawn entries from config.");
    for(SpawnEntry entry : entries) {
      addSpawn(entry);
    }

  }

  public static void addSpawn(SpawnEntry entry) {
    Class<? extends EntityLiving> clz = (Class<? extends EntityLiving>)EntityList.stringToClassMapping.get(entry.getMobName());
    if(clz == null) {
      Log.warn("Skipping spawn entry " + entry.getId() + " as mob " + entry.getMobName() + " is not registered");
      return;
    }
    Type[] biomeTypes = entry.getBiomeTypeArray();
    if(biomeTypes.length == 0) {
      Log.warn("Skipping spawn entry " + entry.getId() + " as no biomes specified");
      return;
    }    
    
    for (BiomeDictionary.Type type : biomeTypes) {
      BiomeGenBase[] biomes = BiomeDictionary.getBiomesForType(type);
      if(entry.isRemove()) {
        if(PRINT_DEBUG) {
          //yeah, I know I could print them as debug messages but that is more painful to change...
          Log.info("EnderIO.MobSpawns.addSpawn: Removing spawns defined in entry: " + entry);
        }
        EntityRegistry.removeSpawn(clz, entry.getCreatureType(), biomes);
      } else {
        if(PRINT_DEBUG) {
          Log.info("MobSpawns.addSpawn: Adding spawns defined in entry: " + entry);
        }
        EntityRegistry.addSpawn(clz, entry.getRate(), entry.getMinGroupSize(), entry.getMaxGroupSize(), entry.getCreatureType(), biomes);
        
      }
    }
    
  }


}
