package crazypants.enderzoo.potion;

import java.util.Random;

import crazypants.enderzoo.EnderZoo;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSpawnSplashEffects implements IMessage, IMessageHandler<PacketSpawnSplashEffects, IMessage> {

  double x;
  double y;
  double z;

  Potion potion;

  public PacketSpawnSplashEffects() {
  }

  public PacketSpawnSplashEffects(Entity ent, int potionId) {
    x = ent.posX;
    y = ent.posY;
    z = ent.posZ;
    potion = Potion.potionTypes[potionId];
  }

  public PacketSpawnSplashEffects(Entity ent, Potion potion) {
    x = ent.posX;
    y = ent.posY;
    z = ent.posZ;
    this.potion = potion;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeDouble(x);
    buf.writeDouble(y);
    buf.writeDouble(z);
    buf.writeInt(potion.id);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readDouble();
    y = buf.readDouble();
    z = buf.readDouble();
    int potionId = buf.readInt();
    potion = Potion.potionTypes[potionId];
  }

  @Override
  public IMessage onMessage(PacketSpawnSplashEffects message, MessageContext ctx) {
    EntityPlayer player = EnderZoo.proxy.getClientPlayer();
    if (message.potion != null && player != null) {
      message.doSpawn((WorldClient) player.getEntityWorld());
    }
    return null;
  }

  private void doSpawn(WorldClient world) {
    //Code taken from RenderGlobal.playAuxSFX, type 2002, splash potion
    double d13 = x;
    double d14 = y;
    double d16 = z;

    Random random = world.rand;
    int data = 0;
    for (int i1 = 0; i1 < 8; ++i1) {
      world.spawnParticle(EnumParticleTypes.ITEM_CRACK, d13, d14, d16, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D,
          new int[] { Item.getIdFromItem(Items.potionitem), data });
    }

    int j1 = potion.getLiquidColor();
    float f = (j1 >> 16 & 255) / 255.0F;
    float f1 = (j1 >> 8 & 255) / 255.0F;
    float f2 = (j1 >> 0 & 255) / 255.0F;
    EnumParticleTypes enumparticletypes = EnumParticleTypes.SPELL;

    if (Items.potionitem.isEffectInstant(data)) {
      enumparticletypes = EnumParticleTypes.SPELL_INSTANT;
    }

    for (int l1 = 0; l1 < 100; ++l1) {
      double d22 = random.nextDouble() * 4.0D;
      double d23 = random.nextDouble() * Math.PI * 2.0D;
      double d24 = Math.cos(d23) * d22;
      double d9 = 0.01D + random.nextDouble() * 0.5D;
      double d11 = Math.sin(d23) * d22;

      EntityFX entityfx = spawnEntityFX(random, enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d13 + d24 * 0.1D, d14 + 0.3D,
          d16 + d11 * 0.1D, d24, d9, d11, new int[0]);

      if (entityfx != null) {
        float f3 = 0.75F + random.nextFloat() * 0.25F;
        entityfx.setRBGColorF(f * f3, f1 * f3, f2 * f3);
        entityfx.multiplyVelocity((float) d22);
      }
    }

    world.playSoundAtPos(new BlockPos(x, y, z), "game.potion.smash", 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
  }

  private EntityFX spawnEntityFX(Random rand, int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset,
      double zOffset, int... parameters) {
    Minecraft mc = Minecraft.getMinecraft();
    if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
      int i = mc.gameSettings.particleSetting;

      if (i == 1 && rand.nextInt(3) == 0) {
        i = 2;
      }

      double d0 = mc.getRenderViewEntity().posX - xCoord;
      double d1 = mc.getRenderViewEntity().posY - yCoord;
      double d2 = mc.getRenderViewEntity().posZ - zCoord;

      if (ignoreRange) {
        return mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
      } else {
        return d0 * d0 + d1 * d1 + d2 * d2 > 256.0D ? null
            : (i > 1 ? null : mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters));
      }
    } else {
      return null;
    }
  }
}
