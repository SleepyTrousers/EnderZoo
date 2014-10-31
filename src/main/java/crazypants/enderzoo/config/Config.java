package crazypants.enderzoo.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.Log;

public final class Config {

  public static class Section {
    public final String name;
    public final String lang;

    public Section(String name, String lang) {
      this.name = name;
      this.lang = lang;
      register();
    }

    private void register() {
      sections.add(this);
    }

    public String lc() {
      return name.toLowerCase();
    }
  }

  public static final List<Section> sections;

  static {
    sections = new ArrayList<Section>();
  }

  public static Configuration config;

  public static File configDirectory;
  
  public static final Section sectionMobConfig = new Section("Mob Config", "mobconfig");
  
  public static boolean enderminyEnabled = true;
  public static int enderminySpawnRate = 60;
  public static boolean enderminyAttacksPlayerOnSight = false;
  public static boolean enderminyAttacksCreepers = true;
  public static int enderminyAttackDamage = 10;
  public static int enderminyHealth = 20;
  public static boolean enderminyGroupAgro = true;
  public static int enderminyMaxGroupSize = 3;
  public static boolean enderminySpawnInLitAreas = false;  
  public static boolean enderminySpawnOnlyOnGrass = true;
  public static int enderminyMinSpawnY = 0;
  
  public static boolean concussionCreeperEnabled = true;
  public static int concussionCreeperSpawnRate = 60;
  public static int concussionCreeperMaxTeleportRange = 32;
  public static int concussionCreeperConfusionDuration = 100;
  public static int concussionCreeperExplosionRange = 5;

  public static boolean fallenKnightEnabled = true;
  public static int fallenKnightSpawnRate = 60;


  public static void load(FMLPreInitializationEvent event) {

    FMLCommonHandler.instance().bus().register(new Config());
    configDirectory = new File(event.getModConfigurationDirectory(), EnderZoo.MODID.toLowerCase());
    if(!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File configFile = new File(configDirectory, "EnderZoo.cfg");
    config = new Configuration(configFile);
    syncConfig();
  }

  public static void syncConfig() {
    try {
      Config.processConfig(config);
    } catch (Exception e) {
      Log.error("EnderZoo has a problem loading it's configuration");
      e.printStackTrace();
    } finally {
      if(config.hasChanged()) {
        config.save();
      }
    }
  }

  @SubscribeEvent
  public void onConfigChanged(OnConfigChangedEvent event) {
    if(event.modID.equals(EnderZoo.MODID)) {
      Log.info("Updating config...");
      syncConfig();
    }
  }

  public static void processConfig(Configuration config) {
    
    enderminyEnabled = config.getBoolean("enderminyEnabled", sectionMobConfig.name, enderminyEnabled, "Wether Enderminies are enabled");
    enderminySpawnRate = config.get(sectionMobConfig.name, "enderminySpawnRate", enderminySpawnRate, 
        "Sets the spawn rate of Enderminies. 10=Enderman spawn rate, 100=Zombie spawn rate").getInt(enderminySpawnRate);
    enderminyAttacksPlayerOnSight = config.getBoolean("enderminyAttacksPlayerOnSight", sectionMobConfig.name, enderminyAttacksPlayerOnSight, 
        "When true an Enderminy will attack a player if it looks at them, otherwise they are neutral mobs.");
    enderminyAttacksCreepers = config.getBoolean("enderminyAttacksCreepers", sectionMobConfig.name, enderminyAttacksCreepers, 
        "When true Enderminies will attack creepers");
    enderminyAttackDamage = config.get(sectionMobConfig.name, "enderminyAttackDamage", enderminyAttackDamage, 
        "Attack damage of Enderminies. 7=Enderman damage, 3=Zombie damage").getInt(enderminyAttackDamage);
    enderminyHealth = config.get(sectionMobConfig.name, "enderminyHealth", enderminyHealth, 
        "Health of Enderminies. 40=Enderman health, 20=Zombie health").getInt(enderminyHealth);
    enderminyGroupAgro = config.getBoolean("enderminyGroupAgro", sectionMobConfig.name, enderminyGroupAgro, 
        "When true attacking one Enderminy will cause other Enderminies who witness the attack to attack the player as well");
    enderminyMaxGroupSize= config.get(sectionMobConfig.name, "enderminyMaxGroupSize", enderminyMaxGroupSize, 
        "Maximum number of Enderminies that will spawn in a single group").getInt(enderminyMaxGroupSize);
    enderminySpawnInLitAreas = config.getBoolean("enderminySpawnInLitAreas", sectionMobConfig.name, enderminySpawnInLitAreas, 
        "When true enderminies will spawn in well lit areas, when false they will only spawn in dark areas.");
    enderminySpawnOnlyOnGrass = config.getBoolean("enderminySpawnOnlyOnGrass", sectionMobConfig.name, enderminySpawnOnlyOnGrass, 
        "When true enderminies will spawn only on grass blocks.");
    enderminyMinSpawnY = config.get(sectionMobConfig.name, "enderminyMinSpawnY", enderminyMinSpawnY, 
        "The minimum Y level at which enderminies will spawn").getInt(enderminyMinSpawnY);
    
    concussionCreeperEnabled = config.getBoolean("concussionCreeperEnabled", sectionMobConfig.name, concussionCreeperEnabled, "Wether ConcussionCreepers are enabled");
    concussionCreeperSpawnRate = config.get(sectionMobConfig.name, "concussionCreeperSpawnRate", concussionCreeperSpawnRate, 
        "Sets the spawn rate of ConcussionCreepers. 10=Enderman spawn rate, 100=Zombie spawn rate").getInt(concussionCreeperSpawnRate);
    concussionCreeperMaxTeleportRange = config.get(sectionMobConfig.name, "concussionCreeperMaxTeleportRange", concussionCreeperMaxTeleportRange, 
        "Sets the max range entites can be telported when the creeper explodes").getInt(concussionCreeperMaxTeleportRange);
    concussionCreeperConfusionDuration = config.get(sectionMobConfig.name, "concussionCreeperConfusionDuration", concussionCreeperConfusionDuration, 
        "Sets the durtaion in ticks of the confusion effect applied on explosion").getInt(concussionCreeperConfusionDuration);
    concussionCreeperExplosionRange = config.get(sectionMobConfig.name, "concussionCreeperExplosionRange", concussionCreeperExplosionRange, 
        "The range of the 'teleport explosion'").getInt(concussionCreeperExplosionRange);    
    

    fallenKnightEnabled = config.getBoolean("fallenKnightEnabled", sectionMobConfig.name, fallenKnightEnabled, "Wether Fallen Knights are enabled");
    fallenKnightSpawnRate = config.get(sectionMobConfig.name, "fallenKnightSpawnRate", fallenKnightSpawnRate, 
        "Sets the spawn rate of Fallen Knights. 10=Enderman spawn rate, 100=Zombie spawn rate").getInt(fallenKnightSpawnRate);
  }

  private Config() {
  }

}
