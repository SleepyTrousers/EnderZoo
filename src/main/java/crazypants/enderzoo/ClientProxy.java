package crazypants.enderzoo;

import crazypants.enderzoo.charge.BlockConcussionCharge;
import crazypants.enderzoo.charge.BlockConfusingCharge;
import crazypants.enderzoo.charge.BlockEnderCharge;
import crazypants.enderzoo.charge.EntityPrimedCharge;
import crazypants.enderzoo.charge.RenderPrimedCharge;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityConcussionCreeper;
import crazypants.enderzoo.entity.EntityDireSlime;
import crazypants.enderzoo.entity.EntityDireWolf;
import crazypants.enderzoo.entity.EntityEnderminy;
import crazypants.enderzoo.entity.EntityFallenKnight;
import crazypants.enderzoo.entity.EntityFallenMount;
import crazypants.enderzoo.entity.EntityOwl;
import crazypants.enderzoo.entity.EntityOwlEgg;
import crazypants.enderzoo.entity.EntityWitherCat;
import crazypants.enderzoo.entity.EntityWitherWitch;
import crazypants.enderzoo.entity.MobInfo;
import crazypants.enderzoo.entity.render.RenderConcussionCreeper;
import crazypants.enderzoo.entity.render.RenderDireSlime;
import crazypants.enderzoo.entity.render.RenderDirewolf;
import crazypants.enderzoo.entity.render.RenderEnderminy;
import crazypants.enderzoo.entity.render.RenderEntityOwlEgg;
import crazypants.enderzoo.entity.render.RenderFallenKnight;
import crazypants.enderzoo.entity.render.RenderFallenMount;
import crazypants.enderzoo.entity.render.RenderOwl;
import crazypants.enderzoo.entity.render.RenderWitherCat;
import crazypants.enderzoo.entity.render.RenderWitherWitch;
import crazypants.enderzoo.item.ItemConfusingDust;
import crazypants.enderzoo.item.ItemEnderFragment;
import crazypants.enderzoo.item.ItemForCreativeMenuIcon;
import crazypants.enderzoo.item.ItemGuardiansBow;
import crazypants.enderzoo.item.ItemOwlEgg;
import crazypants.enderzoo.item.ItemWitheringDust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().world;
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().player;
  }

  @Override
  public void preInit() {
    super.preInit();
    
    MinecraftForge.EVENT_BUS.register(this);
    
    if (Config.enderminyEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityEnderminy.class, RenderEnderminy.FACTORY);
    }
    if (Config.concussionCreeperEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityConcussionCreeper.class, RenderConcussionCreeper.FACTORY);
    }
    if (Config.fallenKnightEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallenKnight.class, RenderFallenKnight.FACTORY);
    }
    if (Config.fallenMountEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityFallenMount.class, RenderFallenMount.FACTORY);
    }
    if (Config.witherWitchEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityWitherWitch.class, RenderWitherWitch.FACTORY);
    }
    if (Config.witherCatEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityWitherCat.class, RenderWitherCat.FACTORY);
    }
    if (Config.direWolfEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityDireWolf.class, RenderDirewolf.FACTORY);
    }
    if (Config.direSlimeEnabled) {
      RenderingRegistry.registerEntityRenderingHandler(EntityDireSlime.class, RenderDireSlime.FACTORY);
    }
    if (Config.owlEnabled) {      
      RenderingRegistry.registerEntityRenderingHandler(EntityOwl.class, RenderOwl.FACTORY);
    }
    RenderingRegistry.registerEntityRenderingHandler(EntityPrimedCharge.class, RenderPrimedCharge.FACTORY);    
    RenderingRegistry.registerEntityRenderingHandler(EntityOwlEgg.class, RenderEntityOwlEgg.FACTORY);

  }
  
  @SubscribeEvent
  public void onModelRegister(ModelRegistryEvent e) {
	  regRenderer(EnderZoo.itemWitheringDust, ItemWitheringDust.NAME);
	  regRenderer(EnderZoo.itemConfusingDust, ItemConfusingDust.NAME);
	  regRenderer(EnderZoo.itemEnderFragment, ItemEnderFragment.NAME);
	  regRenderer(EnderZoo.itemOwlEgg, ItemOwlEgg.NAME);
	  regRenderer(EnderZoo.itemForCreativeMenuIcon, ItemForCreativeMenuIcon.NAME);
	  if (Config.guardiansBowEnabled) 
	      regRenderer(EnderZoo.itemGuardiansBow, ItemGuardiansBow.NAME);
	  if (Config.confusingChargeEnabled) 
	    regRenderer(Item.getItemFromBlock(EnderZoo.blockConfusingCharge), BlockConfusingCharge.NAME);
	  if (Config.concussionChargeEnabled)
	    regRenderer(Item.getItemFromBlock(EnderZoo.blockConcussionCharge), BlockConcussionCharge.NAME);
	  if (Config.enderChargeEnabled)
	    regRenderer(Item.getItemFromBlock(EnderZoo.blockEnderCharge), BlockEnderCharge.NAME);
  }

  private void regRenderer(Item item, int meta, String name) {
    regRenderer(item, meta, EnderZoo.MODID, name);
  }

  private void regRenderer(Item item, int meta, String modId, String name) {
    String resourceName;
    if (modId != null) {
      resourceName = modId + ":" + name;
    } else {
      resourceName = name;
    }
    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(resourceName, "inventory"));
  }

  private void regRenderer(Item item, String name) {
    regRenderer(item, 0, name);
  }

  @Override
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, false, true));
    Minecraft.getMinecraft().player.timeInPortal = 1;
  }
  
  @Override
  public String translate(String unlocalized) {
	  return I18n.format(unlocalized);
  }

}
