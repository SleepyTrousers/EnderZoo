package crazypants.enderzoo.charge;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderzoo.EnderZoo;

public class PacketExplodeEffect implements IMessage, IMessageHandler<PacketExplodeEffect, IMessage> {

  double x;
  double y;
  double z;
  ICharge charge;

  public PacketExplodeEffect() {
  }

  public PacketExplodeEffect(Entity ent, ICharge charge) {
    x = ent.posX;
    y = ent.posY;
    z = ent.posZ;
    this.charge = charge;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeDouble(x);
    buf.writeDouble(y);
    buf.writeDouble(z);
    buf.writeInt(charge.getID());

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readDouble();
    y = buf.readDouble();
    z = buf.readDouble();
    int chargeID = buf.readInt();
    charge = ChargeRegister.instance.getCharge(chargeID);
  }

  @Override
  public IMessage onMessage(PacketExplodeEffect message, MessageContext ctx) {
    EntityPlayer player = EnderZoo.proxy.getClientPlayer();
    if (message.charge != null && player != null) {
      message.charge.explodeEffect(player.worldObj, message.x, message.y, message.z);
    }
    return null;
  }

}
