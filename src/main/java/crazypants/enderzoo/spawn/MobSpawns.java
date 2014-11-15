package crazypants.enderzoo.spawn;

import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.common.registry.EntityRegistry;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.config.SpawnConfig;
import crazypants.enderzoo.spawn.impl.SpawnEntry;

public class MobSpawns {

  private static final boolean PRINT_DETAIL = Config.spawnConfigPrintDetailedOutput;

  private MobSpawns() {
  }

  public static void registerSpawns() {
    List<SpawnEntry> entries = SpawnConfig.loadSpawnConfig();
    Log.info("Applying " + entries.size() + " spawn entries from config.");
    for (SpawnEntry entry : entries) {
      addSpawn(entry);
    }

  }

  public static void addSpawn(ISpawnEntry entry) {
    Class<? extends EntityLiving> clz = (Class<? extends EntityLiving>) EntityList.stringToClassMapping.get(entry.getMobName());
    if(clz == null) {
      Log.warn("Skipping spawn entry " + entry.getId() + " as mob " + entry.getMobName() + " is not registered");
      return;
    }

    if(entry.isRemove()) {
      if(PRINT_DETAIL) {
        //yeah, I know I could print them as debug messages but that is more painful to change...
        Log.info("EnderIO.MobSpawns.addSpawn: Removing spawns defined in entry: " + entry + " for biomes: ");
        System.out.print(" - ");
      }
      for (IBiomeFilter filter : entry.getFilters()) {
        BiomeGenBase[] biomes = filter.getMatchedBiomes();
        if(PRINT_DETAIL) {
          printBiomeNames(biomes);
        }
        EntityRegistry.removeSpawn(clz, entry.getCreatureType(), biomes);
      }
      if(PRINT_DETAIL) {
        System.out.println();
      }
      return;
    }

    if(PRINT_DETAIL) {
      Log.info("MobSpawns.addSpawn: Adding spawns defined in entry: " + entry + " for biomes: ");
      System.out.print(" - ");
    }
    for (IBiomeFilter filter : entry.getFilters()) {
      BiomeGenBase[] biomes = filter.getMatchedBiomes();
      if(PRINT_DETAIL) {
        printBiomeNames(biomes);
      }
      EntityRegistry.addSpawn(clz, entry.getRate(), entry.getMinGroupSize(), entry.getMaxGroupSize(), entry.getCreatureType(), biomes);
    }
    if(PRINT_DETAIL) {
      System.out.println();
    }

  }

  protected static void printBiomeNames(BiomeGenBase[] biomes) {
    for (BiomeGenBase biome : biomes) {
      if(biome != null) {
        System.out.print(biome.biomeName + ", ");
      } else {
        System.out.print("null, ");
      }
    }
  }

}
