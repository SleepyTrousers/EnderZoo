package crazypants.enderzoo.potion;

import java.util.List;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPotionEZ extends ItemPotion {

  public static final String NAME = "potionEZ";

  public static ItemPotionEZ create() {
    ItemPotionEZ res = new ItemPotionEZ();
    res.init();
    return res;
  }

  private ItemPotionEZ() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(true);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
    // BrewingRecipeRegistry.addRecipe(BrewingUtil.createAwkwardPotion(), new
    // ItemStack(this), new ItemStack(Items.apple));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromDamage(int meta) {
    return PotionHelper.getLiquidColor(meta, false);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack stack, int renderPass) {
    if (renderPass > 0) {
      return 16777215;
    }
    List<PotionEffect> effects = getEffects(stack);
    return PotionHelper.calcPotionLiquidColor(effects);
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
    if (isSplash(itemStackIn.getMetadata())) {
      if (!playerIn.capabilities.isCreativeMode) {
        --itemStackIn.stackSize;
      }
      worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      worldIn.spawnEntityInWorld(new EntityPotionEZ(worldIn, playerIn, itemStackIn));
      return itemStackIn;
    } else {
      playerIn.setItemInUse(itemStackIn, getMaxItemUseDuration(itemStackIn));
      return itemStackIn;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {

    PotionEffect effect = new PotionEffect(EnderZoo.floatingPotion.id, 100);
    addVariants(subItems, effect);

    effect = new PotionEffect(EnderZoo.floatingPotion.id, 30, 1);
    addVariants(subItems, effect);
  }

  private void addVariants(List<ItemStack> subItems, PotionEffect effect) {
    ItemStack subItem;
    subItem = new ItemStack(this, 1, 1);
    BrewingUtil.writeCustomEffectToNBT(effect, subItem);
    subItems.add(subItem);

    subItem = new ItemStack(this, 1, 16384);
    BrewingUtil.writeCustomEffectToNBT(effect, subItem);
    subItems.add(subItem);
  }

}
