package crazypants.enderzoo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityFallenKnight;
import crazypants.enderzoo.entity.EntityFallenMount;
import crazypants.enderzoo.entity.EntityConcussionCreeper;
import crazypants.enderzoo.entity.EntityEnderminy;
import crazypants.enderzoo.entity.render.RenderConcussionCreeper;
import crazypants.enderzoo.entity.render.RenderEnderminy;
import crazypants.enderzoo.entity.render.RenderFallenKnight;
import crazypants.enderzoo.entity.render.RenderFallenMount;


public class ClientProxy extends CommonProxy {

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().theWorld;
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().thePlayer;
  }

  @Override
  public void load() {
    super.load(); 
    if(Config.enderminyEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityEnderminy.class, new RenderEnderminy());
    }
    if(Config.concussionCreeperEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityConcussionCreeper.class, new RenderConcussionCreeper());
    }
    if(Config.fallenKnightEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallenKnight.class, new RenderFallenKnight());      
    }
    if(Config.fallenMountEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallenMount.class, new RenderFallenMount());
    }
  }
  
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(Potion.confusion.getId(), duration, 1, true));
    Minecraft.getMinecraft().thePlayer.timeInPortal = 1;
  }

}
