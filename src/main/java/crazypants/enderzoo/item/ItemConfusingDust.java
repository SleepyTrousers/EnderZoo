package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.RegistryHandler;
import net.minecraft.item.Item;

public class ItemConfusingDust extends Item {

  public static final String NAME = "confusingdust";

  public static ItemConfusingDust create() {
    ItemConfusingDust res = new ItemConfusingDust();
    res.init();
    return res;
  }

  private ItemConfusingDust() {
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }

  private void init() {
    RegistryHandler.ITEMS.add(this);
  }

}
