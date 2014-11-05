package crazypants.enderzoo.entity;

import net.minecraft.entity.EntityLiving;
import crazypants.enderzoo.config.Config;

public enum MobInfo {
  ENDERMINY(EntityEnderminy.class, EntityEnderminy.NAME, EntityEnderminy.EGG_BG_COL, EntityEnderminy.EGG_FG_COL, Config.enderminyEnabled),
  CONCUSSION_CREEPER(EntityConcussionCreeper.class, EntityConcussionCreeper.NAME, EntityConcussionCreeper.EGG_BG_COL, EntityConcussionCreeper.EGG_FG_COL, Config.concussionCreeperEnabled),
  DARK_KNIGHT(EntityFallenKnight.class, EntityFallenKnight.NAME, EntityFallenKnight.EGG_BG_COL, EntityFallenKnight.EGG_FG_COL, Config.fallenKnightEnabled),
  DARK_MOUNT(EntityFallenMount.class, EntityFallenMount.NAME, EntityFallenMount.EGG_BG_COL, EntityFallenMount.EGG_FG_COL, Config.fallenMountEnabled),
  WITHER_WITCH(EntityWitherWitch.class, EntityWitherWitch.NAME, EntityWitherWitch.EGG_BG_COL, EntityWitherWitch.EGG_FG_COL, Config.witherWitchEnabled),
  WITHER_CAT(EntityWitherCat.class, EntityWitherCat.NAME, EntityWitherCat.EGG_BG_COL, EntityWitherCat.EGG_FG_COL, Config.witherCatEnabled);;  
    
  final Class<? extends EntityLiving> clz;
  final String name;
  final int bgCol;
  final int fgCol;
  final boolean enabled;
  
  private MobInfo(Class<? extends EntityLiving> clz, String name, int bgCol, int fgCol, boolean enabled) {    
    this.clz = clz;
    this.name = name;
    this.bgCol = bgCol;
    this.fgCol = fgCol;
    this.enabled = enabled;
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
  
}
