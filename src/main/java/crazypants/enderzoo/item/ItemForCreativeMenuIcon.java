package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.RegistryHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemForCreativeMenuIcon extends Item {

  public static final String NAME = "enderZooIcon";

  public static ItemForCreativeMenuIcon create() {
    ItemForCreativeMenuIcon res = new ItemForCreativeMenuIcon();
    res.init();
    return res;
  }

  private ItemForCreativeMenuIcon() {
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);    
    setHasSubtypes(false);
  }

  private void init() {
	RegistryHandler.ITEMS.add(this);
  }
  
  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
	  
  }

}
