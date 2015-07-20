package crazypants.enderzoo.enchantment;

import net.minecraft.enchantment.Enchantment;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.config.Config;

public class Enchantments {

  private static Enchantments instance;

  public static Enchantments getInstance() {
    if (instance == null) {
      instance = new Enchantments();
      instance.registerEnchantments();
    }
    return instance;
  }

  private void registerEnchantments() {
    int id = Config.enchantmentWitherArrowId;
    if (id < 0) {
      id = getEmptyEnchantId();
    }
    if (id < 0) {
      Log.error("Could not find an empty enchantment ID to add enchanments");
      return;
    }
    new EnchantmentWitherArrow(id);

    id = Config.enchantmentWitherWeaponId;
    if (id < 0) {
      id = getEmptyEnchantId();
    }
    if (id < 0) {
      Log.error("Could not find an empty enchantment ID to add enchanments");
      return;
    }
    new EnchantmentWitherWeapon(id);
  }

  private int getEmptyEnchantId() {
    for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
      if (Enchantment.enchantmentsList[i] == null) {
        return i;
      }
    }
    return -1;
  }

}
