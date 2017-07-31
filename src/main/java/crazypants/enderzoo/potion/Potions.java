package crazypants.enderzoo.potion;

import com.google.common.base.Predicate;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

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

  private FloatingPotion floatingPotion;

  public Potions() {
    withering = new PotionType(WITHERING, new PotionEffect(MobEffects.WITHER, 900));
    witheringLong = new PotionType(WITHERING, new PotionEffect(MobEffects.WITHER, 2400));

    confusion = new PotionType(CONFUSION, new PotionEffect(MobEffects.NAUSEA, 900));
    confusionLong = new PotionType(CONFUSION_LONG, new PotionEffect(MobEffects.NAUSEA, 2400));

    if (Config.floatingPotionEnabled) {
      floatingPotion = FloatingPotion.create();
      floating = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionDuration));
      floatingLong = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionDurationLong));
      floatingTwo = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionTwoDuration, 1));
    }
  }

  public void registerPotions() {
    // Wither
    withering.setRegistryName(new ResourceLocation(EnderZoo.MODID,WITHERING));
    EnderZoo.instance.register(withering);
    witheringLong.setRegistryName(new ResourceLocation(EnderZoo.MODID,WITHERING_LONG));
    EnderZoo.instance.register(witheringLong);

    registerPotionTypeConversion(PotionTypes.AWKWARD, EnderZoo.itemWitheringDust, withering);
    registerPotionTypeConversion(withering, Items.REDSTONE, witheringLong);

    // Confusion
    confusion.setRegistryName(new ResourceLocation(EnderZoo.MODID,CONFUSION));
    EnderZoo.instance.register(confusion);
    confusionLong.setRegistryName(new ResourceLocation(EnderZoo.MODID,CONFUSION_LONG));
    EnderZoo.instance.register(confusionLong);

    registerPotionTypeConversion(PotionTypes.AWKWARD, EnderZoo.itemConfusingDust, confusion);
    registerPotionTypeConversion(confusion, Items.REDSTONE, confusionLong);

    // Rising
    if (Config.floatingPotionEnabled) {
      floating.setRegistryName(new ResourceLocation(EnderZoo.MODID,FLOATING));
      EnderZoo.instance.register(floating);
      floatingLong.setRegistryName(new ResourceLocation(EnderZoo.MODID,FLOATING_LONG));
      EnderZoo.instance.register(floatingLong);
      floatingTwo.setRegistryName(new ResourceLocation(EnderZoo.MODID,FLOATING_TWO));
      EnderZoo.instance.register(floatingTwo);

      registerPotionTypeConversion(PotionTypes.AWKWARD, EnderZoo.itemOwlEgg, floating);
      registerPotionTypeConversion(floating, Items.REDSTONE, floatingLong);
      registerPotionTypeConversion(floating, Items.GLOWSTONE_DUST, floatingTwo);
    }

  }

  /**
   * Registers a conversion from one PotionType to another PotionType, with the
   * given reagent
   */
//  private void registerPotionTypeConversion(PotionType input, Predicate<ItemStack> reagentPredicate, PotionType output) {
  private void registerPotionTypeConversion(PotionType input, Item reagentPredicate, PotionType output) {

    try {
      PotionHelper.func_193357_a(input, reagentPredicate, output);
      //regTypeConvMethod.invoke(null, input, reagentPredicate, output);
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
