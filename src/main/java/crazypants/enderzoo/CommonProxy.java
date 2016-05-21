package crazypants.enderzoo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class CommonProxy {

  public CommonProxy() {
  }

  public void preInit() {    
  }
  
  public void init() {
  }
  
  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }  

  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, false, true));    
  }

 
  
}
