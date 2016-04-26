package crazypants.enderzoo.enchantment;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class EnchantmentWitherArrow extends Enchantment {

  protected EnchantmentWitherArrow(int id) {
    super(id, new ResourceLocation(EnderZoo.MODID, "witherArrow"), Config.enchantmentWitherArrowWeight, EnumEnchantmentType.BOW);
    setName("witherArrow");
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return Config.enchantmentWitherArrowMinEnchantability;
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return Config.enchantmentWitherArrowMaxEnchantability;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public void onEntityDamaged(EntityLivingBase user, Entity entityHit, int level) {
    //calc damage modifier    
    if (entityHit instanceof EntityLivingBase) {
      ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(Potion.wither.getId(), Config.enchantmentWitherArrowDuration));
    }
  }
 
}
