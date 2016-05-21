package crazypants.enderzoo.potion;

import java.lang.reflect.Method;

import com.google.common.base.Predicate;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config;
import jline.internal.Log;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Potions {

  private static final String WITHERING = "withering";
  private static final String WITHERING_LONG = "witheringLong";

  private static final String CONFUSION = "confusion";
  private static final String CONFUSION_LONG = "confusionLong";

  private static final String FLOATING = "floating";
  private static final String FLOATING_TWO = "floatingTwo";
  private static final String FLOATING_LONG = "floatingLong";

  private PotionType withering;
  private PotionType witheringLong;

  private PotionType confusion;
  private PotionType confusionLong;

  private PotionType floating;
  private PotionType floatingLong;
  private PotionType floatingTwo;  

  private Method regTypeConvMethod;

  private FloatingPotion floatingPotion;

  public Potions() {
    withering = new PotionType(WITHERING, new PotionEffect(MobEffects.WITHER, 900));
    witheringLong = new PotionType(WITHERING, new PotionEffect(MobEffects.WITHER, 2400));

    confusion = new PotionType(CONFUSION, new PotionEffect(MobEffects.NAUSEA, 900));
    confusionLong = new PotionType(CONFUSION, new PotionEffect(MobEffects.NAUSEA, 2400));

    if (Config.floatingPotionEnabled) {
      floatingPotion = FloatingPotion.create();
      floating = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionDuration));
      floatingLong = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionDurationLong));
      floatingTwo = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionTwoDuration, 1));
    }
        
    try {
      regTypeConvMethod = ReflectionHelper.findMethod(PotionHelper.class, null, new String[] { "registerPotionTypeConversion", "func_185204_a" },
          PotionType.class, Predicate.class, PotionType.class);
    } catch (Exception e) {
      Log.error("Could not find method to register potions. Potions will not be brewable.");
      e.printStackTrace();
    }

  }

  public void registerPotions() {
    // wither potion

    Predicate<ItemStack> redstone = new ItemPredicateInstance(Items.REDSTONE);
    Predicate<ItemStack> glowstone = new ItemPredicateInstance(Items.GLOWSTONE_DUST);

    // Wither
    PotionType.REGISTRY.register(Config.witherPotionID, new ResourceLocation(WITHERING), withering);
    PotionType.REGISTRY.register(Config.witherPotionLongID, new ResourceLocation(WITHERING_LONG), witheringLong);

    Predicate<ItemStack> witheringDust = new ItemPredicateInstance(EnderZoo.itemWitheringDust);
    registerPotionTypeConversion(PotionTypes.AWKWARD, witheringDust, withering);
    registerPotionTypeConversion(withering, redstone, witheringLong);

    // Confusion
    PotionType.REGISTRY.register(Config.confusingPotionID, new ResourceLocation(CONFUSION), confusion);
    PotionType.REGISTRY.register(Config.confusingPotionLongID, new ResourceLocation(CONFUSION_LONG), confusionLong);

    Predicate<ItemStack> confusionDust = new ItemPredicateInstance(EnderZoo.itemConfusingDust);
    registerPotionTypeConversion(PotionTypes.AWKWARD, confusionDust, confusion);
    registerPotionTypeConversion(confusion, redstone, confusionLong);

    // Rising
    if (Config.floatingPotionEnabled) {
      PotionType.REGISTRY.register(Config.floatingPotionID, new ResourceLocation(FLOATING), floating);
      PotionType.REGISTRY.register(Config.floatingPotionLongID, new ResourceLocation(FLOATING_LONG), floatingLong);
      PotionType.REGISTRY.register(Config.floatingPotionTwoID, new ResourceLocation(FLOATING_TWO), floatingTwo);

      Predicate<ItemStack> owlEgg = new ItemPredicateInstance(EnderZoo.itemOwlEgg);
      registerPotionTypeConversion(PotionTypes.AWKWARD, owlEgg, floating);
      registerPotionTypeConversion(floating, redstone, floatingLong);
      registerPotionTypeConversion(floating, glowstone, floatingTwo);
    }

  }

  /**
   * Registers a conversion from one PotionType to another PotionType, with the
   * given reagent
   */
  private void registerPotionTypeConversion(PotionType input, Predicate<ItemStack> reagentPredicate, PotionType output) {
    if (regTypeConvMethod == null) {
      return;
    }
    try {
      regTypeConvMethod.invoke(null, input, reagentPredicate, output);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public PotionType getWithering() {
    return withering;
  }

  public PotionType getWitheringLong() {
    return witheringLong;
  }

  public PotionType getConfusion() {
    return confusion;
  }

  public PotionType getConfusionLong() {
    return confusionLong;
  }

  public PotionType getFloating() {
    return floating;
  }

  public PotionType getFloatingLong() {
    return floatingLong;
  }

  public PotionType getFloatingTwo() {
    return floatingTwo;
  }

  public FloatingPotion getFloatingPotion() {
    return floatingPotion;
  }



  static class ItemPredicateInstance implements Predicate<ItemStack> {
    private final Item item;
    private final int meta;

    public ItemPredicateInstance(Item itemIn) {
      this(itemIn, -1);
    }

    public ItemPredicateInstance(Item itemIn, int metaIn) {
      this.item = itemIn;
      this.meta = metaIn;
    }

    public boolean apply(ItemStack p_apply_1_) {
      return p_apply_1_ != null && p_apply_1_.getItem() == this.item && (this.meta == -1 || this.meta == p_apply_1_.getMetadata());
    }
  }

}
