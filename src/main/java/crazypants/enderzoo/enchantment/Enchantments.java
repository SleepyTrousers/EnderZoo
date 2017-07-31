package crazypants.enderzoo.enchantment;

import crazypants.enderzoo.EnderZoo;

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
    EnderZoo.instance.register(wa);    
    EnchantmentWitherWeapon ww = new EnchantmentWitherWeapon();
    EnderZoo.instance.register(ww);
    
    
  }

  
}