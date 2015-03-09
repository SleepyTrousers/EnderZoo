package crazypants.enderzoo;

import static crazypants.enderzoo.EnderZoo.MODID;
import static crazypants.enderzoo.EnderZoo.MOD_NAME;
import static crazypants.enderzoo.EnderZoo.VERSION;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.oredict.ShapedOreRecipe;
import crazypants.enderzoo.charge.BlockConcussionCharge;
import crazypants.enderzoo.charge.BlockConfusingCharge;
import crazypants.enderzoo.charge.BlockEnderCharge;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.enchantment.Enchantments;
import crazypants.enderzoo.entity.MobInfo;
import crazypants.enderzoo.item.ItemConfusingDust;
import crazypants.enderzoo.item.ItemEnderFragment;
import crazypants.enderzoo.item.ItemForCreativeMenuIcon;
import crazypants.enderzoo.item.ItemGuardiansBow;
import crazypants.enderzoo.item.ItemSpawnEgg;
import crazypants.enderzoo.item.ItemWitheringDust;
import crazypants.enderzoo.spawn.MobSpawnEventHandler;
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
  public static ItemConfusingDust itemConfusingDust;
  public static ItemEnderFragment itemEnderFragment;
  public static ItemForCreativeMenuIcon itemForCreativeMenuIcon;
  public static ItemGuardiansBow itemGuardiansBow;
    
  public static BlockConfusingCharge blockConfusingCharge;
  public static BlockEnderCharge blockEnderCharge;
  public static BlockConcussionCharge blockConcussionCharge;
  
  public static MobSpawnEventHandler spawnEventHandler;

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

    if(Config.confusingChargeEnabled) {
      blockConfusingCharge = BlockConfusingCharge.create();
    }
    if(Config.enderChargeEnabled) {
      blockEnderCharge = BlockEnderCharge.create();
    }
    if(Config.concussionChargeEnabled) {
      blockConcussionCharge = BlockConcussionCharge.create();
    }

    //    System.err.println("EnderZoo.preInit: DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.err.println("EnderZoo.preInit: DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.err.println("EnderZoo.preInit: DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.err.println("EnderZoo.preInit: DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.err.println("EnderZoo.preInit: DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.err.println("EnderZoo.preInit: DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.err.println("EnderZoo.preInit: DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.err.println("EnderZoo.preInit: DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    DebugUtil.instance.setEnabled(true);

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

    //Register enchantments
    Enchantments.getInstance();

    MobSpawns.instance.loadSpawnConfig();
    addRecipes();

    if(Config.enderZooDifficultyModifierEnabled || Config.globalDifficultyModifierEnabled) {
      spawnEventHandler = new MobSpawnEventHandler();
      spawnEventHandler.init();
    }

  }

  private void addRecipes() {
//    if(Config.confusingChargeEnabled) {
//      OreDictionary.registerOre("sand", new ItemStack(Blocks.sand, 1, OreDictionary.WILDCARD_VALUE));
//      ItemStack cc = new ItemStack(blockConfusingCharge);
//      GameRegistry.addRecipe(new ShapedOreRecipe(cc, "csc", "sgs", "csc", 'c', itemConfusingDust, 's', "sand", 'g', Items.gunpowder));
//    }
//    if(Config.enderChargeEnabled) {
//      OreDictionary.registerOre("sand", new ItemStack(Blocks.sand, 1, OreDictionary.WILDCARD_VALUE));
//      ItemStack cc = new ItemStack(blockEnderCharge);
//      GameRegistry.addRecipe(new ShapedOreRecipe(cc, "csc", "sgs", "csc", 'c', itemEnderFragment, 's', "sand", 'g', Items.gunpowder));
//    }
//    if(Config.concussionChargeEnabled) {
//      OreDictionary.registerOre("sand", new ItemStack(Blocks.sand, 1, OreDictionary.WILDCARD_VALUE));
//      ItemStack cc = new ItemStack(blockConcussionCharge);
//      GameRegistry.addRecipe(new ShapedOreRecipe(cc, "eee", "sgs", "ccc", 'c', itemConfusingDust, 'e', itemEnderFragment, 's', "sand", 'g', Items.gunpowder));
//    }
    GameRegistry.addShapedRecipe(new ItemStack(Items.ender_pearl), " f ", "fff", " f ", 'f', itemEnderFragment);

  }

}
