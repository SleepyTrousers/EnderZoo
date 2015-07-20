package crazypants.enderzoo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import crazypants.enderzoo.charge.EntityPrimedCharge;
import crazypants.enderzoo.charge.RenderPrimedCharge;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityConcussionCreeper;
import crazypants.enderzoo.entity.EntityDireSlime;
import crazypants.enderzoo.entity.EntityDireWolf;
import crazypants.enderzoo.entity.EntityEnderminy;
import crazypants.enderzoo.entity.EntityFallenKnight;
import crazypants.enderzoo.entity.EntityFallenMount;
import crazypants.enderzoo.entity.EntityWitherCat;
import crazypants.enderzoo.entity.EntityWitherWitch;
import crazypants.enderzoo.entity.render.RenderConcussionCreeper;
import crazypants.enderzoo.entity.render.RenderDireSlime;
import crazypants.enderzoo.entity.render.RenderDirewolf;
import crazypants.enderzoo.entity.render.RenderEnderminy;
import crazypants.enderzoo.entity.render.RenderFallenKnight;
import crazypants.enderzoo.entity.render.RenderFallenMount;
import crazypants.enderzoo.entity.render.RenderWitherCat;
import crazypants.enderzoo.entity.render.RenderWitherWitch;
import crazypants.enderzoo.item.GuardiansBowRenderer;

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
    if (Config.enderminyEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityEnderminy.class, new RenderEnderminy());
    }
    if (Config.concussionCreeperEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityConcussionCreeper.class, new RenderConcussionCreeper());
    }
    if (Config.fallenKnightEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallenKnight.class, new RenderFallenKnight());
    }
    if (Config.fallenMountEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallenMount.class, new RenderFallenMount());
    }
    if (Config.witherWitchEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityWitherWitch.class, new RenderWitherWitch());
    }
    if (Config.witherCatEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityWitherCat.class, new RenderWitherCat());
    }
    if (Config.direWolfEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityDireWolf.class, new RenderDirewolf());
    }
    RenderingRegistry.registerEntityRenderingHandler(EntityPrimedCharge.class, new RenderPrimedCharge());
    //RenderingRegistry.registerEntityRenderingHandler(EntityPotionEZ_WIP.class, new RenderPotionEntity_WIP());

    if (Config.guardiansBowEnabled) {
      MinecraftForgeClient.registerItemRenderer(EnderZoo.itemGuardiansBow, new GuardiansBowRenderer());
    }
    if (Config.direSlimeEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityDireSlime.class, new RenderDireSlime());
    }
  }

  @Override
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(Potion.confusion.getId(), duration, 1, true));
    Minecraft.getMinecraft().thePlayer.timeInPortal = 1;
  }

}
