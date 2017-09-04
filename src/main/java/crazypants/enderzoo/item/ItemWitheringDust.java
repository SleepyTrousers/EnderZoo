package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.RegistryHandler;
import net.minecraft.item.Item;

public class ItemWitheringDust extends Item {

  public static final String NAME = "witheringdust";  

  public static ItemWitheringDust create() {
    ItemWitheringDust res = new ItemWitheringDust();
    res.init();
    return res;
  }

  private ItemWitheringDust() {
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }
  
  private void init() {
	RegistryHandler.ITEMS.add(this);        
  }
 
}