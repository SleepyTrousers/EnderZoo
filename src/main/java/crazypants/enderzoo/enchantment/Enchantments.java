package crazypants.enderzoo.enchantment;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class Enchantments {

  private static Enchantments instance;

  public static Enchantments getInstance() {
    if(instance == null) {
      instance = new Enchantments();
      instance.registerEnchantments();
    }
    return instance;
  }
  
  private void registerEnchantments() {    
    EnchantmentWitherArrow wa = new EnchantmentWitherArrow();
    GameRegistry.register(wa);
    //ForgeRegistries.ENCHANTMENTS.register(wa);       
    EnchantmentWitherWeapon ww = new EnchantmentWitherWeapon();
//    ForgeRegistries.ENCHANTMENTS.register(ww);
    GameRegistry.register(ww);
    
    
  }

  
}