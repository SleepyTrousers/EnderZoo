package crazypants.enderzoo.item;

import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.config.Config;

public class ItemWitheringDust extends Item {

  private static final String NAME = "witheringDust";

  public static ItemWitheringDust create() {
    ItemWitheringDust res = new ItemWitheringDust();
    res.init();
    return res;
  }

  private ItemWitheringDust() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setTextureName("enderzoo:itemWitheringDust");
    setHasSubtypes(false);
  }
  
  private void init() {
    GameRegistry.registerItem(this, NAME);    
    try {
      HashMap myPotionRequirements = (HashMap)ReflectionHelper.getPrivateValue(PotionHelper.class, null, "potionRequirements", "field_77927_l");      
      String mask = "0 & 1 & !2 &  3 & 0+6";
      if(Config.useAltWitherPotionEffectMask) {
        mask = "0 & 1 & 2 &  3 & 0+6";
      }
      myPotionRequirements.put(Integer.valueOf(Potion.wither.getId()), mask);
    } catch (Exception e) {
      Log.error("ItemWitheringDust: Could not register wither potion recipe " + e);
    }    
  }
  
  @Override
  public String getPotionEffect(ItemStack p_150896_1_) {
    if(Config.useAltWitherPotionEffectMask) {
      return "+0+1+2+3&4-4+13";
    }
    return "+0+1-2+3&4-4+13";
  }
  
}
