package crazypants.enderzoo.item;

import net.minecraft.item.Item;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.RegistryHandler;

public class ItemEnderFragment extends Item {

  public static final String NAME = "enderfragment";

  public static ItemEnderFragment create() {
    ItemEnderFragment res = new ItemEnderFragment();
    res.init();
    return res;
  }

  private ItemEnderFragment() {
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);    
    setHasSubtypes(false);
  }

  private void init() {
	RegistryHandler.ITEMS.add(this);
  }
}
