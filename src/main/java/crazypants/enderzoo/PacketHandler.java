package crazypants.enderzoo;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler {

  public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("enderzoo");

  private static int ID = 0;

  public static int nextID() {
    return ID++;
  }

  public static void sendToAllAround(IMessage message, Entity e, int range) {
    INSTANCE.sendToAllAround(message, new TargetPoint(e.getEntityWorld().provider.getDimension(), e.posX, e.posY, e.posZ, range));
  }

  public static void sendToAllAround(IMessage message, Entity e) {
    sendToAllAround(message, e, 64);
  }

}
