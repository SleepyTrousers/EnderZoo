package crazypants.enderzoo.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.brewing.PotionBrewedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.EnderZooTab;

public class ItemPotionEZ_WIP extends ItemPotion {

  public static ItemPotionEZ_WIP create() {

    ItemPotionEZ_WIP res = new ItemPotionEZ_WIP();
    res.init();

    MinecraftForge.EVENT_BUS.register(res);

    //    int entityID = EntityRegistry.findGlobalUniqueEntityId();
    //    EntityRegistry.registerGlobalEntityID(EntityPotionEZ.class, "EntityPotionEZ", entityID);
    //    EntityRegistry.registerModEntity(EntityPotionEZ.class, "EntityPotionEZ", entityID, this, 32, 20, true);

    return res;
  }

  private static final String NAME = "itemWitherPotion";

  private PotionEffect effect;
  private List<PotionEffect> effects;

  public ItemPotionEZ_WIP() {

    setMaxStackSize(1);
    setHasSubtypes(true);
    setMaxDamage(0);
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    iconString = "potion";

    effect = new PotionEffect(Potion.wither.getId(), 100);
    effects = Collections.singletonList(effect);
  }

  @Override
  public String getPotionEffect(ItemStack p_150896_1_) {
    return "+0+1-2+3&4-4+16";

  }

  protected void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public List<PotionEffect> getEffects(ItemStack stack) {
    return effects;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    if (isSplash(stack.getItemDamage())) {
      if (!player.capabilities.isCreativeMode) {
        --stack.stackSize;
      }
      world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!world.isRemote) {
        world.spawnEntityInWorld(new EntityPotionEZ_WIP(world, player, stack));
      }
      return stack;
    } else {
      player.setItemInUse(stack, getMaxItemUseDuration(stack));
      return stack;
    }
  }

  @Override
  public int getColorFromDamage(int p_77620_1_) {
    return 0x000000;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs tabs, List list) {
    list.add(new ItemStack(this, 1, 8192));
    list.add(new ItemStack(this, 1, 16384));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isEffectInstant(int p_77833_1_) {
    return false;
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    String s = "";

    if (isSplash(stack.getItemDamage())) {
      s = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
    }

    return s + StatCollector.translateToLocal("item.itemPotionEZ.wither.name");
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean aBool) {
    for (PotionEffect potioneffect : getEffects(stack)) {
      String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
      Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
//      Map map = potion.func_111186_k();

      if (potioneffect.getAmplifier() > 0) {
        s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
      }
      if (potioneffect.getDuration() > 20) {
        s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";
      }
      if (potion.isBadEffect()) {
        list.add(EnumChatFormatting.RED + s1);
      } else {
        list.add(EnumChatFormatting.GRAY + s1);
      }
    }
  }

  @SubscribeEvent
  public void onPotionBrewed(PotionBrewedEvent evt) {
    //    System.out.println("ItemPotionEZ.onPotionBrewed: " + evt);
    //    //evt.brewingStacks[0] = new ItemStack(Items.stick);
    //    for(int i=0;i<3;i++) {
    //      ItemStack stack = evt.brewingStacks[i];
    //    //for (ItemStack stack : evt.brewingStacks) {
    //      if(stack != null) {
    //        if(stack.getItemDamage() == 8198) {
    //          evt.brewingStacks[i] = new ItemStack(this, 1, 8192);
    //        }
    //      }        
    //    }
  }

}
