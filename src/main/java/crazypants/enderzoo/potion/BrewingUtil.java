package crazypants.enderzoo.potion;

import crazypants.enderzoo.EnderZoo;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

public class BrewingUtil {

  public static ItemStack createHarmingPotion(boolean isAugmented, boolean isSplash) {
    ItemStack res;
    if(isSplash) {
      res = new ItemStack(Items.SPLASH_POTION);
    } else {
      res = new ItemStack(Items.POTIONITEM);
    }
    PotionUtils.addPotionToItemStack(res, PotionTypes.HARMING);
    return res;            
  }

  public static ItemStack createWitherPotion(boolean isProlonged, boolean isSplash) {
    ItemStack res;
    if(isSplash) {
      res = new ItemStack(Items.SPLASH_POTION);
    } else {
      res = new ItemStack(Items.POTIONITEM);
    }
    if(isProlonged) {
      PotionUtils.addPotionToItemStack(res, EnderZoo.potions.getWitheringLong());
    } else {
      PotionUtils.addPotionToItemStack(res, EnderZoo.potions.getWithering());
    }
    return res;     
  }

  public static ItemStack createHealthPotion(boolean isProlonged, boolean isAugmented, boolean isSplash) {
    ItemStack res;
    if(isSplash) {
      res = new ItemStack(Items.SPLASH_POTION);
    } else {
      res = new ItemStack(Items.POTIONITEM);
    }
    if(isProlonged || isAugmented) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.STRONG_HEALING);
    } else {
      PotionUtils.addPotionToItemStack(res, PotionTypes.HEALING);
    }
    return res;
  }

  public static ItemStack createRegenerationPotion(boolean isProlonged, boolean isAugmented, boolean isSplash) {
    ItemStack res;
    if(isSplash) {
      res = new ItemStack(Items.SPLASH_POTION);
    } else {
      res = new ItemStack(Items.POTIONITEM);
    }
    if(isAugmented) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.STRONG_REGENERATION);
    } else if(isProlonged) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.LONG_REGENERATION);
    } else {
      PotionUtils.addPotionToItemStack(res, PotionTypes.REGENERATION);
    }
    return res;
  }

  public static void writeCustomEffectToNBT(PotionEffect effect, ItemStack stack) {
    NBTTagCompound nbt;
    if(stack.hasTagCompound()) {
      nbt = stack.getTagCompound();
    } else {
      nbt = new NBTTagCompound();
      stack.setTagCompound(nbt);
    }    
    writeCustomEffectToNBT(effect, nbt);    
  }

  public static void writeCustomEffectToNBT(PotionEffect effect, NBTTagCompound nbt) {
    NBTTagCompound effectNBT = new NBTTagCompound();        
    effect.writeCustomPotionEffectToNBT(effectNBT);
    
    NBTTagList effectList = new NBTTagList();
    effectList.appendTag(effectNBT);            
    
    nbt.setTag("CustomPotionEffects", effectList);
  }

}
