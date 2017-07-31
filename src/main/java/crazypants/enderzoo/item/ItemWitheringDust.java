package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemWitheringDust extends Item {

  public static final String NAME = "witheringdust";  

  public static ItemWitheringDust create() {
    ItemWitheringDust res = new ItemWitheringDust();
    EnderZoo.instance.register(res);    
    return res;
  }

  private ItemWitheringDust() {
    setUnlocalizedName(NAME);
    setRegistryName(new ResourceLocation(EnderZoo.MODID,NAME));
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }
  
}