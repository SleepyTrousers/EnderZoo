package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZooTab;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemConfusingDust extends Item {

  public static final String NAME = "confusingDust";

  public static ItemConfusingDust create() {
    ItemConfusingDust res = new ItemConfusingDust();
    res.init();
    return res;
  }

  private ItemConfusingDust() {
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.register(this);
     
    //TODO: 1.9
//    ItemStack gs = new ItemStack(Items.glowstone_dust);
//    ItemStack rs = new ItemStack(Items.redstone);
//    ItemStack gp = new ItemStack(Items.gunpowder);

//    PotionEffect effect = new PotionEffect(MobEffects.confusion, 300, 1);
//    ItemStack basePotion = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, false));

//    effect = new PotionEffect(this, Config.floatingPotionDurationLong, 0);
//    ItemStack longPotion = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, false));
//
//    effect = new PotionEffect(this, Config.floatingPotionTwoDuration, 1);
//    ItemStack twoPotion = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, false));
//    
//    effect = new PotionEffect(this, Config.floatingPotionDurationSplash, 0);
//    ItemStack basePotionSplash = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, true));
//            
//    effect = new PotionEffect(this, Config.floatingPotionDurationLongSplash, 0);
//    ItemStack longPotionSplash = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, true));
//
//    effect = new PotionEffect(this, Config.floatingPotionTwoDurationSplash, 1);
//    ItemStack twoPotionSplash = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, true));

//    BrewingRecipeRegistry.addRecipe(BrewingUtil.createAwkwardPotion(), new ItemStack(this), basePotion);


//    BrewingRecipeRegistry.addRecipe(basePotion, gp, basePotionSplash);
//    BrewingRecipeRegistry.addRecipe(twoPotion, gp, twoPotionSplash);
//    BrewingRecipeRegistry.addRecipe(longPotion, gp, longPotionSplash);
  }

}
