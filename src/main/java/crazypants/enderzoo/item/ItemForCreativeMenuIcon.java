package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZoo;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemForCreativeMenuIcon extends Item {

  public static final String NAME = "enderZooIcon";

  public static ItemForCreativeMenuIcon create() {
    ItemForCreativeMenuIcon res = new ItemForCreativeMenuIcon();
    EnderZoo.instance.register(res);
    return res;
  }

  private ItemForCreativeMenuIcon() {
    setUnlocalizedName(NAME);
    setRegistryName(new ResourceLocation(EnderZoo.MODID,NAME));
    setCreativeTab(null);    
    setHasSubtypes(false);
  }
}
