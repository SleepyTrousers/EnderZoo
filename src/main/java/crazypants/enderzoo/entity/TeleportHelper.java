package crazypants.enderzoo.entity;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class TeleportHelper {

  private static final int DEFAULT_RND_TP_DISTANCE = 16;
  private static Random rand = new Random();

  public static boolean teleportRandomly(EntityLivingBase entity, int distance) {
    double d0 = entity.posX + (rand.nextDouble() - 0.5D) * distance;
    double d1 = entity.posY + rand.nextInt(distance + 1) - distance / 2;
    double d2 = entity.posZ + (rand.nextDouble() - 0.5D) * distance;
    return teleportTo(entity, d0, d1, d2, false);
  }

  public static boolean teleportRandomly(EntityLivingBase entity) {
    return teleportRandomly(entity, DEFAULT_RND_TP_DISTANCE);
  }

  public static boolean teleportToEntity(EntityLivingBase entity, Entity toEntity) {
    Vec3 vec3 = new Vec3(entity.posX - toEntity.posX,
        entity.getEntityBoundingBox().minY + entity.height / 2.0F - toEntity.posY + toEntity.getEyeHeight(), entity.posZ - toEntity.posZ);
    vec3 = vec3.normalize();
    double d0 = 16.0D;
    double d1 = entity.posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3.xCoord * d0;
    double d2 = entity.posY + (rand.nextInt(16) - 8) - vec3.yCoord * d0;
    double d3 = entity.posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3.zCoord * d0;
    return teleportTo(entity, d1, d2, d3, false);
  }

  public static boolean teleportTo(EntityLivingBase entity, double x, double y, double z, boolean fireEndermanEvent) {

    EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
    if (fireEndermanEvent) {
      if (MinecraftForge.EVENT_BUS.post(event)) {
        return false;
      }
    }

    double origX = entity.posX;
    double origY = entity.posY;
    double origZ = entity.posZ;
    entity.posX = event.targetX;
    entity.posY = event.targetY;
    entity.posZ = event.targetZ;

    int xInt = MathHelper.floor_double(entity.posX);
    int yInt = Math.max(1, MathHelper.floor_double(entity.posY));
    int zInt = MathHelper.floor_double(entity.posZ);

    boolean doTeleport = false;
    
    if (entity.worldObj.isBlockLoaded(new BlockPos(xInt, yInt, zInt), true)) {
      boolean foundGround = false;
      while (!foundGround && yInt > 0) {
        IBlockState bs = entity.worldObj.getBlockState(new BlockPos(xInt, yInt - 1, zInt));
        if (bs != null && bs.getBlock() != null && bs.getBlock().getMaterial().blocksMovement()) {
          foundGround = true;
        } else {
          --entity.posY;
          --yInt;
        }
      }

      if (foundGround) {
        entity.setPosition(entity.posX, entity.posY, entity.posZ);
        if (entity.worldObj.getCollidingBoundingBoxes(entity, entity.getEntityBoundingBox()).isEmpty()
            && !entity.worldObj.isAnyLiquid(entity.getEntityBoundingBox())) {
          doTeleport = true;
        } else if (yInt <= 0) {
          doTeleport = false;
        }
      }
    }

    if (!doTeleport) {
      entity.setPosition(origX, origY, origZ);
      return false;
    }

    entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);

    short short1 = 128;
    for (int l = 0; l < short1; ++l) {
      double d6 = l / (short1 - 1.0D);
      float f = (rand.nextFloat() - 0.5F) * 0.2F;
      float f1 = (rand.nextFloat() - 0.5F) * 0.2F;
      float f2 = (rand.nextFloat() - 0.5F) * 0.2F;
      double d7 = origX + (entity.posX - origX) * d6 + (rand.nextDouble() - 0.5D) * entity.width * 2.0D;
      double d8 = origY + (entity.posY - origY) * d6 + rand.nextDouble() * entity.height;
      double d9 = origZ + (entity.posZ - origZ) * d6 + (rand.nextDouble() - 0.5D) * entity.width * 2.0D;
      entity.worldObj.spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
    }

    entity.worldObj.playSoundEffect(origX, origY, origZ, "mob.endermen.portal", 1.0F, 1.0F);
    entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
    return true;

  }

}
