package crazypants.enderzoo.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import crazypants.enderzoo.config.Config;

public enum MobInfo {
  ENDERMINY(EntityEnderminy.class, EntityEnderminy.NAME, EntityEnderminy.EGG_BG_COL, EntityEnderminy.EGG_FG_COL, Config.enderminyEnabled,
      Config.enderminyHealth, Config.enderminyAttackDamage), CONCUSSION_CREEPER(EntityConcussionCreeper.class, EntityConcussionCreeper.NAME,
      EntityConcussionCreeper.EGG_BG_COL, EntityConcussionCreeper.EGG_FG_COL, Config.concussionCreeperEnabled, Config.concussionCreeperHealth, 4), FALLEN_KNIGHT(
      EntityFallenKnight.class, EntityFallenKnight.NAME, EntityFallenKnight.EGG_BG_COL, EntityFallenKnight.EGG_FG_COL, Config.fallenKnightEnabled,
      Config.fallenKnightHealth, Config.fallenKnightBaseDamage), FALLEN_MOUNT(EntityFallenMount.class, EntityFallenMount.NAME, EntityFallenMount.EGG_BG_COL,
      EntityFallenMount.EGG_FG_COL, Config.fallenMountEnabled, Config.fallenMountHealth, Config.fallenMountBaseAttackDamage), WITHER_WITCH(
      EntityWitherWitch.class, EntityWitherWitch.NAME, EntityWitherWitch.EGG_BG_COL, EntityWitherWitch.EGG_FG_COL, Config.witherWitchEnabled,
      Config.witherWitchHealth, 4), WITHER_CAT(EntityWitherCat.class, EntityWitherCat.NAME, EntityWitherCat.EGG_BG_COL, EntityWitherCat.EGG_FG_COL,
      Config.witherCatEnabled, Config.witherCatHealth, Config.witherCatAttackDamage), DIRE_WOLF(EntityDireWolf.class, EntityDireWolf.NAME,
      EntityDireWolf.EGG_BG_COL, EntityDireWolf.EGG_FG_COL, Config.direWolfEnabled, Config.direWolfHealth, Config.direWolfAttackDamage), DIRE_SLIME(
      EntityDireSlime.class, EntityDireSlime.NAME, EntityDireSlime.EGG_BG_COL, EntityDireSlime.EGG_FG_COL, Config.direSlimeEnabled, Config.direSlimeHealth,
      Config.direSlimeAttackDamage);

  final Class<? extends EntityLiving> clz;
  final String name;
  final int bgCol;
  final int fgCol;
  final boolean enabled;
  final double maxHealth;
  final double attackDamage;

  private MobInfo(Class<? extends EntityLiving> clz, String name, int bgCol, int fgCol, boolean enabled, double baseHealth, double baseAttack) {
    this.clz = clz;
    this.name = name;
    this.bgCol = bgCol;
    this.fgCol = fgCol;
    this.enabled = enabled;
    maxHealth = baseHealth;
    attackDamage = baseAttack;
  }

  public Class<? extends EntityLiving> getClz() {
    return clz;
  }

  public String getName() {
    return name;
  }

  public int getEggBackgroundColor() {
    return bgCol;
  }

  public int getEggForegroundColor() {
    return fgCol;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void applyAttributes(EntityLivingBase entity) {
    entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
    entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.attackDamage).setBaseValue(attackDamage);
  }

}
