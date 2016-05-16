package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZooTab;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemConfusingDust extends Item {

  public static final String NAME = "confusingDust";

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
    GameRegistry.register(this);
  }

}
