package crazypants.enderzoo.item;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.EnderZooTab;

public class ItemEnderFragment extends Item {

  private static final String NAME = "enderFragment";

  public static ItemEnderFragment create() {
    ItemEnderFragment res = new ItemEnderFragment();
    res.init();
    return res;
  }

  private ItemEnderFragment() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setTextureName("enderzoo:itemEnderFragment");
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }
}
