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
import crazypants.enderzoo.entity.EntityWitherCat;
import crazypants.enderzoo.entity.EntityWitherWitch;
import crazypants.enderzoo.entity.MobInfo;
import crazypants.enderzoo.entity.render.RenderConcussionCreeper;
import crazypants.enderzoo.entity.render.RenderDireSlime;
import crazypants.enderzoo.entity.render.RenderDirewolf;
import crazypants.enderzoo.entity.render.RenderEnderminy;
import crazypants.enderzoo.entity.render.RenderFallenKnight;
import crazypants.enderzoo.entity.render.RenderFallenMount;
import crazypants.enderzoo.entity.render.RenderWitherCat;
import crazypants.enderzoo.entity.render.RenderWitherWitch;
import crazypants.enderzoo.item.GuardiansBowModelLoader;
import crazypants.enderzoo.item.ItemConfusingDust;
import crazypants.enderzoo.item.ItemEnderFragment;
import crazypants.enderzoo.item.ItemForCreativeMenuIcon;
import crazypants.enderzoo.item.ItemGuardiansBow;
import crazypants.enderzoo.item.ItemSpawnEgg;
import crazypants.enderzoo.item.ItemWitheringDust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

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
  public void preInit() {
    super.preInit();
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
    RenderingRegistry.registerEntityRenderingHandler(EntityPrimedCharge.class, RenderPrimedCharge.FACTORY);
  }

  @Override
  public void init() {
    super.init();
  
    regRenderer(EnderZoo.itemWitheringDust, ItemWitheringDust.NAME);
    regRenderer(EnderZoo.itemConfusingDust, ItemConfusingDust.NAME);
    regRenderer(EnderZoo.itemEnderFragment, ItemEnderFragment.NAME);
    regRenderer(EnderZoo.itemForCreativeMenuIcon, ItemForCreativeMenuIcon.NAME);

    for (MobInfo inf : MobInfo.values()) {
      if (inf.isEnabled()) {
        regRenderer(EnderZoo.itemSpawnEgg, inf.ordinal(), ItemSpawnEgg.NAME);
      }
    }

    if (Config.guardiansBowEnabled) {
      regRenderer(EnderZoo.itemGuardiansBow, ItemGuardiansBow.NAME);
      GuardiansBowModelLoader.registerVariants();
    }
    if (Config.confusingChargeEnabled) {
      regRenderer(Item.getItemFromBlock(EnderZoo.blockConfusingCharge), BlockConfusingCharge.NAME);
    }
    if (Config.concussionChargeEnabled) {
      regRenderer(Item.getItemFromBlock(EnderZoo.blockConcussionCharge), BlockConcussionCharge.NAME);
    }
    if (Config.enderChargeEnabled) {
      regRenderer(Item.getItemFromBlock(EnderZoo.blockEnderCharge), BlockEnderCharge.NAME);
    }
  }

  private void regRenderer(Item item, int meta, String name) {
    regRenderer(item, meta, EnderZoo.MODID, name);
  }

  private void regRenderer(Item item, int meta, String modId, String name) {
    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    // ItemMeshDefinition d;
    String resourceName;
    if (modId != null) {
      resourceName = modId + ":" + name;
    } else {
      resourceName = name;
    }
    renderItem.getItemModelMesher().register(item, meta, new ModelResourceLocation(resourceName, "inventory"));
  }

  private void regRenderer(Item item, String name) {
    regRenderer(item, 0, name);
  }

  @Override
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(Potion.confusion.getId(), duration, 1, false, true));
    Minecraft.getMinecraft().thePlayer.timeInPortal = 1;
  }

}
