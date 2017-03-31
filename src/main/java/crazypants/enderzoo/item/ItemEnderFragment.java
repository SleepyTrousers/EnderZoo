package crazypants.enderzoo.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import crazypants.enderzoo.EnderZooTab;

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
    GameRegistry.register(this);
  }
}
