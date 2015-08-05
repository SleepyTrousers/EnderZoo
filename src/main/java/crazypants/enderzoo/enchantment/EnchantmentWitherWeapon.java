package crazypants.enderzoo.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import crazypants.enderzoo.config.Config;

public class EnchantmentWitherWeapon extends Enchantment {

  protected EnchantmentWitherWeapon(int id) {
    super(id, new ResourceLocation("enderzoo.witherWeapon"), Config.enchantmentWitherWeaponWeight, EnumEnchantmentType.WEAPON);
    setName("enderzoo.witherWeapon");

  }

  @Override
  public int getMinEnchantability(int p_77321_1_) {
    return Config.enchantmentWitherWeaponMinEnchantability;
  }

  @Override
  public int getMaxEnchantability(int p_77317_1_) {
    return Config.enchantmentWitherWeaponMaxEnchantability;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  //public void func_151368_a(EntityLivingBase source, Entity entityHit, int p_151368_3_) {
  public void onEntityDamaged(EntityLivingBase user, Entity entityHit, int level) {
    //calc damage modifier    
    if (entityHit instanceof EntityLivingBase) {
      ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(Potion.wither.getId(), Config.enchantmentWitherWeaponDuration));
    }
  }

}
