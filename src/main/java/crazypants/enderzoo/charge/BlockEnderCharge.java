package crazypants.enderzoo.charge;

import java.util.List;
import java.util.Random;

import crazypants.enderzoo.PacketHandler;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityUtil;
import crazypants.enderzoo.entity.TeleportHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnderCharge extends BlockConfusingCharge {

  public static final String NAME = "blockendercharge";

  public static BlockEnderCharge create() {
    if(!Config.enderChargeEnabled) {
      return null;
    }

    BlockEnderCharge res = new BlockEnderCharge();
    res.init();
    return res;
  }

  protected BlockEnderCharge() {
    super(NAME);
  }

  @Override
  public void explode(EntityPrimedCharge entity) {
    PacketHandler.sendToAllAround(new PacketExplodeEffect(entity, this), entity);
    doEntityTeleport(entity);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void explodeEffect(World world, double x, double y, double z) {
    world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D);
    doTeleportEffect(world, x, y, z);
  }

  public static void doEntityTeleport(EntityPrimedCharge entity) {
    World world = entity.getEntityWorld();
    world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1F,
        1.4f + ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F));    
    world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 2F,
        1 + ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F));

    AxisAlignedBB bb = EntityUtil.getBoundsAround(entity, Config.enderChargeRange);
    List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
    for (EntityLivingBase ent : ents) {
      boolean done = false;
      for (int i = 0; i < 20 && !done; i++) {
        done = TeleportHelper.teleportRandomly(ent, Config.enderChargeMaxTeleportRange);
      }
    }
  }
  
  @SideOnly(Side.CLIENT)
  public static void doTeleportEffect(World world, double x, double y, double z) {
    Random random = world.rand;
    for (int i = 0; i < 100; ++i) {
      double d = random.nextDouble() * 2D;
      double mag = 2;
      double motionX = (0.5 - random.nextDouble()) * mag * d;
      double motionY = (0.5 - random.nextDouble()) * mag;
      double motionZ = (0.5 - random.nextDouble()) * mag * d;
      Particle entityfx = new ParticlePortal.Factory().createParticle
          (i, world, x + motionX * 0.1, y + motionY * 0.1, z + motionZ * 0.1, motionX, motionY,
          motionZ, (int[])null);
      Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
    }
  }

}
