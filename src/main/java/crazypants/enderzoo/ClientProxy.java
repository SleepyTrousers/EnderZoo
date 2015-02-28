package crazypants.enderzoo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import crazypants.enderzoo.charge.EntityPrimedCharge;
import crazypants.enderzoo.charge.RenderPrimedCharge;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityConcussionCreeper;
import crazypants.enderzoo.entity.EntityDireWolf;
import crazypants.enderzoo.entity.EntityEnderminy;
import crazypants.enderzoo.entity.EntityFallenKnight;
import crazypants.enderzoo.entity.EntityFallenMount;
import crazypants.enderzoo.entity.EntityWitherCat;
import crazypants.enderzoo.entity.EntityWitherWitch;
import crazypants.enderzoo.entity.render.RenderConcussionCreeper;
import crazypants.enderzoo.entity.render.RenderDirewolf;
import crazypants.enderzoo.entity.render.RenderEnderminy;
import crazypants.enderzoo.entity.render.RenderFallenKnight;
import crazypants.enderzoo.entity.render.RenderFallenMount;
import crazypants.enderzoo.entity.render.RenderWitherCat;
import crazypants.enderzoo.entity.render.RenderWitherWitch;
import crazypants.enderzoo.item.GuardiansBowRenderer;
import crazypants.enderzoo.item.ItemGuardiansBow;
import crazypants.enderzoo.item.ItemWitheringDust;


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
    RenderManager rm = Minecraft.getMinecraft().getRenderManager();
    if(Config.enderminyEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityEnderminy.class, new RenderEnderminy(rm));
    }
    if(Config.concussionCreeperEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityConcussionCreeper.class, new RenderConcussionCreeper(rm));
    }
    if(Config.fallenKnightEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallenKnight.class, new RenderFallenKnight(rm));      
    }
    if(Config.fallenMountEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallenMount.class, new RenderFallenMount(rm));
    }
    if(Config.witherWitchEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityWitherWitch.class, new RenderWitherWitch(rm));
    } 
    if(Config.witherCatEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityWitherCat.class, new RenderWitherCat(rm));
    }
    if(Config.direWolfEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityDireWolf.class, new RenderDirewolf(rm));
    }

//    RenderingRegistry.registerEntityRenderingHandler(EntityPrimedCharge.class, new RenderPrimedCharge());


//    if(Config.guardiansBowEnabled) {
//      MinecraftForgeClient.registerItemRenderer(EnderZoo.itemGuardiansBow, new GuardiansBowRenderer());
//    }

    
        
    regRenderer(EnderZoo.itemWitheringDust, ItemWitheringDust.NAME);
    regRenderer(EnderZoo.itemGuardiansBow, ItemGuardiansBow.NAME);
    
    
  }

  private void regRenderer(Item item, String name) {
    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();  
    renderItem.getItemModelMesher().register(item, 0, new ModelResourceLocation(EnderZoo.MODID + ":" + name, "inventory"));
  }
  
  @Override
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(Potion.confusion.getId(), duration, 1, false, true));
    Minecraft.getMinecraft().thePlayer.timeInPortal = 1;
  }

}
