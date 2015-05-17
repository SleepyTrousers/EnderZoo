package crazypants.enderzoo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DebugUtil {

  public static DebugUtil instance = new DebugUtil();

  @SideOnly(Side.CLIENT)
  private EntityLivingBase lastMouseOver;

  public void setEnabled(boolean enabled) {
    Object obf = Launch.blackboard.get("fml.deobfuscatedEnvironment");
    if (obf == null || !(Boolean) obf) {
      Log.warn("DebugUtil: Debug was enabled in a non dev. environemnt. Ignoring request.");
      return;
    }

    if (enabled) {
      System.err.println("DebugUtil.setEnabled:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
      MinecraftForge.EVENT_BUS.register(this);
      FMLCommonHandler.instance().bus().register(this);
    } else {
      MinecraftForge.EVENT_BUS.unregister(this);
      FMLCommonHandler.instance().bus().unregister(this);
    }
  }

  @SubscribeEvent
  public void onPlayerTick(PlayerTickEvent evt) {
    if (evt.side != Side.SERVER || evt.phase != Phase.START) {
      return;
    }
    evt.player.setHealth(evt.player.getMaxHealth());
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onPlayerTickClient(PlayerTickEvent evt) {
    if (evt.side != Side.CLIENT || evt.phase != Phase.END) {
      return;
    }
    MovingObjectPosition mo = Minecraft.getMinecraft().objectMouseOver;
    if (mo != null && mo.entityHit != null && mo.entityHit instanceof EntityLivingBase) {
      EntityLivingBase el = (EntityLivingBase) mo.entityHit;
      if (el != lastMouseOver) {
        double baseAttack = 0;
        double attack = 0;
        IAttributeInstance damAtt = el.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.attackDamage);
        if (damAtt != null) {
          baseAttack = damAtt.getBaseValue();
          attack = damAtt.getAttributeValue();
        }
        System.out.println("DebugUtil.onPlayerTickClient: Health: " + el.getMaxHealth() + " Base Damage: " + baseAttack + " Damage: " + attack);
      }
      lastMouseOver = el;
    } else {
      lastMouseOver = null;
    }

  }

  @SubscribeEvent
  public void onMonsterSpawn(LivingSpawnEvent evt) {
    if (evt.entityLiving != null) { //&& !evt.entityLiving.getClass().getName().contains("enderzoo")) {
      //      evt.setResult(Result.DENY);
    }
  }

}
