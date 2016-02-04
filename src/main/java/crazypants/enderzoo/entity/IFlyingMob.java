package crazypants.enderzoo.entity;

import crazypants.enderzoo.entity.navigate.FlyingPathNavigate;
import net.minecraft.entity.EntityCreature;

public interface IFlyingMob extends IEnderZooMob {

  float getMaxTurnRate();
  
  float getMaxClimbRate();
  
  FlyingPathNavigate getFlyingNavigator();
  
  EntityCreature asEntityCreature();
}
