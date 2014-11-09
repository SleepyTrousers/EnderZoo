package crazypants.enderzoo;

import static crazypants.enderzoo.EnderZoo.MODID;
import static crazypants.enderzoo.EnderZoo.MOD_NAME;
import static crazypants.enderzoo.EnderZoo.VERSION;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.enchantment.Enchantments;
import crazypants.enderzoo.entity.MobInfo;
import crazypants.enderzoo.item.ItemSpawnEgg;
import crazypants.enderzoo.item.ItemWitheringDust;
import crazypants.enderzoo.spawn.MobSpawns;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:Forge@10.13.0.1150,)", guiFactory = "crazypants.enderzoo.config.ConfigFactoryEnderZoo")
public class EnderZoo {

  public static final String MODID = "EnderZoo";
  public static final String MOD_NAME = "Ender Zoo";
  public static final String VERSION = "@VERSION@";

  @Instance(MODID)
  public static EnderZoo instance;

  @SidedProxy(clientSide = "crazypants.enderzoo.ClientProxy", serverSide = "crazypants.enderzoo.CommonProxy")
  public static CommonProxy proxy;

  public static ItemSpawnEgg itemSpawnEgg;
  public static ItemWitheringDust itemWitheringDust;
  

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
       
    Config.load(event);
    for (MobInfo mob : MobInfo.values()) {
      registerEntity(mob);

    }    
    itemSpawnEgg = ItemSpawnEgg.create();       
    itemWitheringDust = ItemWitheringDust.create();

    //DebugUtil.instance.setEnabled(true);          
          
    //Register enchantments
    Enchantments.getInstance();

    FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderzoo.waila.WailaCompat.load");
  }

  private void registerEntity(MobInfo mob) {
    if(!mob.isEnabled()) {
      return;
    }
    int entityID = EntityRegistry.findGlobalUniqueEntityId();
    EntityRegistry.registerGlobalEntityID(mob.getClz(), mob.getName(), entityID, mob.getEggBackgroundColor(), mob.getEggForegroundColor());
    EntityRegistry.registerModEntity(mob.getClz(), mob.getName(), entityID, this, 64, 3, true);
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {
    instance = this;
    proxy.load();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    MobSpawns.registerSpawns();
  }

}
