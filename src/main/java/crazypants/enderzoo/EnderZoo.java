package crazypants.enderzoo;
import static crazypants.enderzoo.EnderZoo.MODID;
import static crazypants.enderzoo.EnderZoo.MOD_NAME;
import static crazypants.enderzoo.EnderZoo.VERSION;
import java.util.ArrayList;
import java.util.List;
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
import crazypants.enderzoo.item.ItemOwlEgg;
import crazypants.enderzoo.item.ItemSpawnEgg;
import crazypants.enderzoo.item.ItemWitheringDust;
import crazypants.enderzoo.potion.Potions;
import crazypants.enderzoo.spawn.MobSpawnEventHandler;
import crazypants.enderzoo.spawn.MobSpawns;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, guiFactory = "crazypants.enderzoo.config.ConfigFactoryEnderZoo")
public class EnderZoo {
    public static final String MODID = "enderzoo";
    public static final String MOD_NAME = "Ender Zoo";
    public static final String VERSION = "@VERSION@";
    @Instance(value=MODID)
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
      MinecraftForge.EVENT_BUS.register(instance);//for new forge recipe events
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
      potions.registerPotions();
      //            DebugUtil.instance.setEnabled(true);
      FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderzoo.waila.WailaCompat.load");
      proxy.preInit();
    }
    private void registerEntity(MobInfo mob) {
      EntityRegistry.registerModEntity(new ResourceLocation(MODID, mob.getName()),
          mob.getClz(), mob.getName(), mob.getEntityId(), this, 64, 3, true);
    }
    @EventHandler
    public void load(FMLInitializationEvent event) {
      //instance = this;
      proxy.init();
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
      //Register enchantments
      Enchantments.getInstance();
      MobSpawns.instance.loadSpawnConfig();
      if (Config.enderZooDifficultyModifierEnabled || Config.globalDifficultyModifierEnabled) {
        spawnEventHandler = new MobSpawnEventHandler();
        spawnEventHandler.init();
      }
    }
    private void addRecipes() {
      ResourceLocation rl;
      //OreDictionary.registerOre("sand", new ItemStack(Blocks.SAND, 1, OreDictionary.WILDCARD_VALUE));//sand is already in dict by default
      if (Config.confusingChargeEnabled) {
        ItemStack cc = new ItemStack(blockConfusingCharge);
        rl = new ResourceLocation(MODID, "recipe" + resourceLocationCounter);
        addRecipe(new ShapedOreRecipe(rl, cc, "csc", "sgs", "csc", 'c', itemConfusingDust, 's', "sand", 'g', Items.GUNPOWDER), rl);
        resourceLocationCounter++;
      }
      if (Config.enderChargeEnabled) {
        ItemStack cc = new ItemStack(blockEnderCharge);
        rl = new ResourceLocation(MODID, "recipe" + resourceLocationCounter);
        addRecipe(new ShapedOreRecipe(rl, cc, "csc", "sgs", "csc", 'c', itemEnderFragment, 's', "sand", 'g', Items.GUNPOWDER), rl);
        resourceLocationCounter++;
      }
      if (Config.concussionChargeEnabled) {
        ItemStack cc = new ItemStack(blockConcussionCharge);
        rl = new ResourceLocation(MODID, "recipe" + resourceLocationCounter);
        addRecipe(new ShapedOreRecipe(rl, cc, "eee", "sgs", "ccc", 'c', itemConfusingDust, 'e', itemEnderFragment, 's', "sand", 'g', Items.GUNPOWDER), rl);
        resourceLocationCounter++;
      }
      rl = new ResourceLocation(MODID, "recipe" + resourceLocationCounter);
      addRecipe(new ShapedOreRecipe(rl, new ItemStack(Items.ENDER_PEARL), " f ", "fff", " f ", 'f', itemEnderFragment), rl);
      resourceLocationCounter++;
    }
    private int resourceLocationCounter = 0;
    private List<IRecipe> recipes = new ArrayList<IRecipe>();
    private List<Item> items = new ArrayList<Item>();
    private List<Block> blocks = new ArrayList<Block>();
    private List<Enchantment> enchants = new ArrayList<Enchantment>();
    private List<SoundEvent> sounds = new ArrayList<SoundEvent>();
    private List<Potion> potionlist = new ArrayList<Potion>();
    private List<PotionType> potiontype = new ArrayList<PotionType>();
    private void addRecipe(IRecipe r, ResourceLocation rl) {
      r.setRegistryName(rl);
      this.recipes.add(r);
    }
    public void register(Item r) {
      this.items.add(r);
    }
    public void register(Block r) {
      this.blocks.add(r);
    }
    public void register(Enchantment r) {
      this.enchants.add(r);
    }
    public void register(SoundEvent r) {
      this.sounds.add(r);
    }
    public void register(Potion r) {
      this.potionlist.add(r);
    }
    public void register(PotionType r) {
      this.potiontype.add(r);
    }
    @SubscribeEvent
    public void onRegisterRecipe(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().registerAll(this.recipes.toArray(new IRecipe[0]));
    }
    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
      event.getRegistry().registerAll(this.items.toArray(new Item[0]));
      addRecipes();
    }
    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
      event.getRegistry().registerAll(this.blocks.toArray(new Block[0]));
    }
    @SubscribeEvent
    public void onRegisterEnchantments(RegistryEvent.Register<Enchantment> event) {
      event.getRegistry().registerAll(this.enchants.toArray(new Enchantment[0]));
    }
    @SubscribeEvent
    public void onRegisterSoundEvent(RegistryEvent.Register<SoundEvent> event) {
      event.getRegistry().registerAll(this.sounds.toArray(new SoundEvent[0]));
    }
    @SubscribeEvent
    public void onRegisterPotion(RegistryEvent.Register<Potion> event) {
      event.getRegistry().registerAll(this.potionlist.toArray(new Potion[0]));
    }
    @SubscribeEvent
    public void onRegisterPotionType(RegistryEvent.Register<PotionType> event) {
      event.getRegistry().registerAll(this.potiontype.toArray(new PotionType[0]));
    }
}
