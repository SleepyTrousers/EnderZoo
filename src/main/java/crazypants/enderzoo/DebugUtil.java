package crazypants.enderzoo;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class DebugUtil {

  public static DebugUtil instance = new DebugUtil();

  public void setEnabled(boolean enabled) {
    if(enabled) {
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
    if(evt.side != Side.SERVER || evt.phase != Phase.START) {
      return;
    }
    evt.player.setHealth(evt.player.getMaxHealth());
  }

  @SubscribeEvent
  public void onMonsterSpawn(LivingSpawnEvent evt) {
    if(evt.entityLiving != null) { //&& !evt.entityLiving.getClass().getName().contains("enderzoo")) {
      evt.setResult(Result.DENY);
    }
  }

}
