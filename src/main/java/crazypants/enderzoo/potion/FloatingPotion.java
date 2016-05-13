package crazypants.enderzoo.potion;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;

import crazypants.enderzoo.ColorUtil;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config;
import jline.internal.Log;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FloatingPotion extends Potion {

  private static final String NAME = EnderZoo.MODID + ".floatingPotion";

  public static FloatingPotion create() {
    FloatingPotion res = new FloatingPotion();
    GameRegistry.register(res);
    return res;
  }

  private FloatingPotion() {
    super(false, 0);
    setPotionName(NAME);
  }

  public void addRecipes() {

    ItemStack gs = new ItemStack(Items.glowstone_dust);
    ItemStack rs = new ItemStack(Items.redstone);
    ItemStack gp = new ItemStack(Items.gunpowder);

    PotionEffect effect = new PotionEffect(this, Config.floatingPotionDuration, 0);
    ItemStack basePotion = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, false));

    effect = new PotionEffect(this, Config.floatingPotionDurationLong, 0);
    ItemStack longPotion = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, false));

    effect = new PotionEffect(this, Config.floatingPotionTwoDuration, 1);
    ItemStack twoPotion = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, false));
    
    effect = new PotionEffect(this, Config.floatingPotionDurationSplash, 0);
    ItemStack basePotionSplash = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, true));
            
    effect = new PotionEffect(this, Config.floatingPotionDurationLongSplash, 0);
    ItemStack longPotionSplash = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, true));

    effect = new PotionEffect(this, Config.floatingPotionTwoDurationSplash, 1);
    ItemStack twoPotionSplash = EnderZoo.itemPotionEZ.addSubtype(new PotionConfig(effect, true));

    BrewingRecipeRegistry.addRecipe(BrewingUtil.createAwkwardPotion(), new ItemStack(EnderZoo.itemOwlEgg), basePotion);

    //TODO: Need to submit a PR to forge so non-vanilla recipes can override vanilla behaviour     
    // BrewingRecipeRegistry.addRecipe(basePotion, gs, twoPotion);
    // BrewingRecipeRegistry.addRecipe(basePotion, rs, longPotion);
    try {
      Field rf = BrewingRecipeRegistry.class.getDeclaredField("recipes");
      rf.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<IBrewingRecipe> recipes = (List<IBrewingRecipe>) rf.get(null);
      recipes.add(0, new BrewingRecipe(basePotion, gs, twoPotion));
      recipes.add(0, new BrewingRecipe(basePotion, rs, longPotion));
    } catch (Exception e) {
      e.printStackTrace();
      Log.error("FloatingPotion.addRecipes: Enhanced floating potions unavailable " + e);
    }

    BrewingRecipeRegistry.addRecipe(basePotion, gp, basePotionSplash);
    BrewingRecipeRegistry.addRecipe(twoPotion, gp, twoPotionSplash);
    BrewingRecipeRegistry.addRecipe(longPotion, gp, longPotionSplash);
  }

  @Override
  public boolean isReady(int p_76397_1_, int p_76397_2_) {
    return true;
  }

  @Override
  public void performEffect(EntityLivingBase entityIn, int amplifier) {    
    if (entityIn.posY < 255) {
      if (amplifier > 0) {
        entityIn.motionY += Config.floatingPotionTwoAcceleration;        
        if (entityIn.motionY > Config.floatingPotionTwoSpeed) {
          entityIn.motionY = Config.floatingPotionTwoSpeed;
        }
      } else {     
        entityIn.motionY += Config.floatingPotionAcceleration;
        if (entityIn.motionY > Config.floatingPotionSpeed) {
          entityIn.motionY = Config.floatingPotionSpeed;
        }
      }
    }
  }

  @Override
  public int getLiquidColor() {
    return ColorUtil.getRGB(Color.green.darker().darker());
  }

}
