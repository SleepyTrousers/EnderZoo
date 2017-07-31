package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemConfusingDust extends Item {

  public static final String NAME = "confusingdust";

  public static ItemConfusingDust create() {
    ItemConfusingDust res = new ItemConfusingDust();
    EnderZoo.instance.register(res);
    return res;
  }

  private ItemConfusingDust() {
    setUnlocalizedName(NAME);
    setRegistryName(new ResourceLocation(EnderZoo.MODID,NAME));
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }
 
}
