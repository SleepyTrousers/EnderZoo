package crazypants.enderzoo;

import static crazypants.enderzoo.EnderZoo.MODID;
import static crazypants.enderzoo.EnderZoo.MOD_NAME;
import static crazypants.enderzoo.EnderZoo.VERSION;

import crazypants.enderzoo.charge.BlockConcussionCharge;
import crazypants.enderzoo.charge.BlockConfusingCharge;
import crazypants.enderzoo.charge.BlockEnderCharge;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.MobInfo;
import crazypants.enderzoo.item.ItemConfusingDust;
import crazypants.enderzoo.item.ItemEnderFragment;
import crazypants.enderzoo.item.ItemForCreativeMenuIcon;
import crazypants.enderzoo.item.ItemGuardiansBow;
import crazypants.enderzoo.item.ItemOwlEgg;
import crazypants.enderzoo.item.ItemSpawnEgg;
import crazypants.enderzoo.item.ItemWitheringDust;
import crazypants.enderzoo.potion.Potions;
import crazypants.enderzoo.spawn.MobSpawnEventHandler;
import crazypants.enderzoo.spawn.MobSpawns;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, guiFactory = "crazypants.enderzoo.config.ConfigFactoryEnderZoo")
public class EnderZoo {

  public static final String MODID = "enderzoo";
  public static final String MOD_NAME = "Ender Zoo";
  public static final String VERSION = "@VERSION@";

  @Instance(MODID)
  public static EnderZoo instance;

  @SidedProxy(clientSide = "crazypants.enderzoo.ClientProxy", serverSide = "crazypants.enderzoo.CommonProxy")
  public static CommonProxy proxy;

  public static ItemSpawnEgg itemSpawnEgg;
  public static ItemWitheringDust itemWitheringDust;
  public static ItemConfusingDust itemConfusingDust;
  public static ItemEnderFragment itemEnderFragment;
  public static ItemForCreativeMenuIcon itemForCreativeMenuIcon;
  public static ItemGuardiansBow itemGuardiansBow;
  public static ItemOwlEgg itemOwlEgg;

  public static BlockConfusingCharge blockConfusingCharge;
  public static BlockEnderCharge blockEnderCharge;
  public static BlockConcussionCharge blockConcussionCharge;

  public static MobSpawnEventHandler spawnEventHandler;
  
  public static Potions potions;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    itemForCreativeMenuIcon = ItemForCreativeMenuIcon.create();

    Config.load(event);
    for (MobInfo mob : MobInfo.values()) {
      registerEntity(mob);
    }
    
    itemSpawnEgg = ItemSpawnEgg.create();
    itemWitheringDust = ItemWitheringDust.create();
    itemConfusingDust = ItemConfusingDust.create();
    itemEnderFragment = ItemEnderFragment.create();
    itemGuardiansBow = ItemGuardiansBow.create();   
    itemOwlEgg = ItemOwlEgg.create();

    if (Config.confusingChargeEnabled) {
      blockConfusingCharge = BlockConfusingCharge.create();
    }
    if (Config.enderChargeEnabled) {
      blockEnderCharge = BlockEnderCharge.create();
    }
    if (Config.concussionChargeEnabled) {
      blockConcussionCharge = BlockConcussionCharge.create();
    }
    potions = new Potions();

    FMLInterModComms.sendMessage("waila", "register", "crazypants.enderzoo.waila.WailaCompat.load");
    proxy.preInit();
  }

  private void registerEntity(MobInfo mob) {
    EntityRegistry.registerModEntity(new ResourceLocation(MODID,mob.getName()),
        mob.getClz(), mob.getName(), mob.getEntityId(), this, 64, 3, true);
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {
    instance = this;
    proxy.init();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    MobSpawns.instance.loadSpawnConfig();

    if (Config.enderZooDifficultyModifierEnabled || Config.globalDifficultyModifierEnabled) {
      spawnEventHandler = new MobSpawnEventHandler();
      spawnEventHandler.init();
    }

  }

  protected static void addRecipes() {
    OreDictionary.registerOre("sand", new ItemStack(Blocks.SAND, 1, OreDictionary.WILDCARD_VALUE));
    if (Config.confusingChargeEnabled) {      
      ItemStack cc = new ItemStack(blockConfusingCharge);
      GameRegistry.addShapedRecipe(blockConfusingCharge.getRegistryName(), null, cc, "csc", "sgs", "csc", 'c', itemConfusingDust, 's', "sand", 'g', Items.GUNPOWDER);
    }
    if (Config.enderChargeEnabled) {      
      ItemStack cc = new ItemStack(blockEnderCharge);
      GameRegistry.addShapedRecipe(blockEnderCharge.getRegistryName(), null, cc, "csc", "sgs", "csc", 'c', itemEnderFragment, 's', "sand", 'g', Items.GUNPOWDER);
    }
    if (Config.concussionChargeEnabled) {      
      ItemStack cc = new ItemStack(blockConcussionCharge);
      GameRegistry.addShapedRecipe(blockConcussionCharge.getRegistryName(), null, cc, "eee", "sgs", "ccc", 'c', itemConfusingDust, 'e', itemEnderFragment, 's', "sand", 'g', Items.GUNPOWDER);
    }
    GameRegistry.addShapedRecipe(itemEnderFragment.getRegistryName(), null, new ItemStack(Items.ENDER_PEARL), " f ", "fff", " f ", 'f', itemEnderFragment);   
  }

}
