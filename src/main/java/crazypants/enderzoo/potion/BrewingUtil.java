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
      res = new ItemStack(Items.splash_potion);
    } else {
      res = new ItemStack(Items.potionitem);
    }
    PotionUtils.addPotionToItemStack(res, PotionTypes.harming);
    return res;            
  }

  public static ItemStack createWitherPotion(boolean isProlonged, boolean isSplash) {
    ItemStack res;
    if(isSplash) {
      res = new ItemStack(Items.splash_potion);
    } else {
      res = new ItemStack(Items.potionitem);
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
      res = new ItemStack(Items.splash_potion);
    } else {
      res = new ItemStack(Items.potionitem);
    }
    if(isProlonged || isAugmented) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.strong_healing);
    } else {
      PotionUtils.addPotionToItemStack(res, PotionTypes.healing);
    }
    return res;
  }

  public static ItemStack createRegenerationPotion(boolean isProlonged, boolean isAugmented, boolean isSplash) {
    ItemStack res;
    if(isSplash) {
      res = new ItemStack(Items.splash_potion);
    } else {
      res = new ItemStack(Items.potionitem);
    }
    if(isAugmented) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.strong_regeneration);
    } else if(isProlonged) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.long_regeneration);
    } else {
      PotionUtils.addPotionToItemStack(res, PotionTypes.regeneration);
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
