package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZooTab;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemWitheringDust extends Item {

  public static final String NAME = "witheringDust";

  public static ItemWitheringDust create() {
    ItemWitheringDust res = new ItemWitheringDust();
    res.init();
    return res;
  }

  private ItemWitheringDust() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }
  
  private void init() {
    GameRegistry.register(this);    
       
    //TODO: 1.9 Recipes for wither potions
  }
  
  
  
}