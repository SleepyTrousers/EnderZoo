package crazypants.enderzoo.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
    setCreativeTab(null);    
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.register(this);
  }

}
