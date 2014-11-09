package crazypants.enderzoo;

import net.minecraft.entity.Entity;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler {

  public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("enderzoo");

  private static int ID = 0;

  public static int nextID() {
    return ID++;
  }

  public static void sendToAllAround(IMessage message, Entity e, int range) {
    INSTANCE.sendToAllAround(message, new TargetPoint(e.worldObj.provider.dimensionId, e.posX, e.posY, e.posZ, range));
  }

  public static void sendToAllAround(IMessage message, Entity e) {
    sendToAllAround(message, e, 64);
  }

}
