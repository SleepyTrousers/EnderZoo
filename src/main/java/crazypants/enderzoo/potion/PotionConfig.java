package crazypants.enderzoo.potion;

import net.minecraft.potion.PotionEffect;

public class PotionConfig {

  private final PotionEffect effect;
  private final boolean isSplash;

  public PotionConfig(PotionEffect effect, boolean isSplash) {
    this.effect = effect;
    this.isSplash = isSplash;
  }

  public PotionEffect getEffect() {
    return effect;
  }

  public boolean isSplash() {
    return isSplash;
  }

}
