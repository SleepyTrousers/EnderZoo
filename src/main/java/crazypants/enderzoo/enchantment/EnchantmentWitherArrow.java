package crazypants.enderzoo.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import crazypants.enderzoo.config.Config;

public class EnchantmentWitherArrow extends Enchantment {

  protected EnchantmentWitherArrow(int id) {
    super(id, Config.enchantmentWitherArrowWeight, EnumEnchantmentType.bow);
    setName("enderzoo.witherArrow");
  }

  @Override
  public int getMinEnchantability(int p_77321_1_) {
    return Config.enchantmentWitherArrowMinEnchantability;
  }

  @Override
  public int getMaxEnchantability(int p_77317_1_) {
    return Config.enchantmentWitherArrowMaxEnchantability;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public void func_151368_a(EntityLivingBase source, Entity entityHit, int p_151368_3_) {
    //calc damage modifier    
    if (entityHit instanceof EntityLivingBase) {
      ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(Potion.wither.getId(), Config.enchantmentWitherArrowDuration));
    }
  }

  //  @Override
  //  public void func_151367_b(EntityLivingBase entityHit, Entity damageSource, int p_151367_3_) {
  //    //other calculate modifier
  //    //System.out.println("EnchantmentWitherArrow.func_151367_b: ");
  //  }

}
