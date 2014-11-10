package crazypants.enderzoo.item;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemForCreativeMenuIcon extends Item {

  private static final String NAME = "enderZooIcon";

  public static ItemForCreativeMenuIcon create() {
    ItemForCreativeMenuIcon res = new ItemForCreativeMenuIcon();
    res.init();
    return res;
  }

  private ItemForCreativeMenuIcon() {
    setUnlocalizedName(NAME);
    setCreativeTab(null);
    setTextureName("enderzoo:enderZooIcon");
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

}
