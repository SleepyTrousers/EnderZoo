package crazypants.enderzoo.potion;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.PacketHandler;
import crazypants.enderzoo.config.Config;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPotionEZ extends ItemPotion {

  public static final String NAME = "potionEZ";

  private List<ItemStack> subTypes = new ArrayList<ItemStack>();

  public static ItemPotionEZ create() {
    
    EntityRegistry.registerModEntity(EntityPotionEZ.class, "EntityPotionEZ", Config.entityPotionId, EnderZoo.instance, 64, 10, true);
    PacketHandler.INSTANCE.registerMessage(PacketSpawnSplashEffects.class, PacketSpawnSplashEffects.class, PacketHandler.nextID(), Side.CLIENT);
    
    ItemPotionEZ res = new ItemPotionEZ();
    res.init();
    return res;
  }

  private ItemPotionEZ() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(true);
    setRegistryName(NAME);
  }

  private void init() {
    GameRegistry.register(this);      
  }

  //TODO: 1.9
//  @Override
//  @SideOnly(Side.CLIENT)
//  public int getColorFromDamage(int meta) {
//    return PotionHelper.getLiquidColor(meta, false);
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public int getColorFromItemStack(ItemStack stack, int renderPass) {
//    if (renderPass > 0) {
//      return 16777215;
//    }
//    List<PotionEffect> effects = getEffects(stack);
//    return PotionHelper.calcPotionLiquidColor(effects);
//  }

  //TODO: 1.9
//  @Override
//  public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
//    if (isSplash(itemStackIn.getMetadata())) {
//      if (!playerIn.capabilities.isCreativeMode) {
//        --itemStackIn.stackSize;
//      }
//      worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
//      if (!worldIn.isRemote) {         
//        worldIn.spawnEntityInWorld(new EntityPotionEZ(worldIn, playerIn, itemStackIn));
//      }
//      return itemStackIn;
//    } else {
//      playerIn.setItemInUse(itemStackIn, getMaxItemUseDuration(itemStackIn));
//      return itemStackIn;
//    }
//  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    subItems.addAll(subTypes);
  }

  public ItemStack addSubtype(PotionConfig config) {
    if (config == null) {
      return null;
    }
    ItemStack is = createPotionEZ(config);
    subTypes.add(is);
    return is;
  }

  private ItemStack createPotionEZ(PotionConfig pc) {
    // int meta = subTypes.size() + 1;
    int meta = 1;
    if (pc.isSplash()) {
      meta |= 16384;
    }
    ItemStack res = new ItemStack(EnderZoo.itemPotionEZ, 1, meta);
    BrewingUtil.writeCustomEffectToNBT(pc.getEffect(), res);
    return res;
  }

}
