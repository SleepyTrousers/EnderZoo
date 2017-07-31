package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemEnderFragment extends Item {

  public static final String NAME = "enderfragment";

  public static ItemEnderFragment create() {
    ItemEnderFragment res = new ItemEnderFragment();
    EnderZoo.instance.register(res);
    return res;
  }

  private ItemEnderFragment() {
    setUnlocalizedName(NAME);
    setRegistryName(new ResourceLocation(EnderZoo.MODID,NAME));
    setCreativeTab(EnderZooTab.tabEnderZoo);    
    setHasSubtypes(false);
  }
 
}
