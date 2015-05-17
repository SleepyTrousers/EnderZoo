package crazypants.enderzoo.entity;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.ReflectionHelper;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.config.Config;

public class EntityConcussionCreeper extends EntityCreeper implements IEnderZooMob {

  public static final String NAME = "enderzoo.ConcussionCreeper";
  public static final int EGG_BG_COL = 0x56FF8E;
  public static final int EGG_FG_COL = 0xFF0A22;

  private Field fTimeSinceIgnited;
  private Field fFuseTime;

  public EntityConcussionCreeper(World world) {
    super(world);
    try {
      fTimeSinceIgnited = ReflectionHelper.findField(EntityCreeper.class, "timeSinceIgnited", "field_70833_d");
      fFuseTime = ReflectionHelper.findField(EntityCreeper.class, "fuseTime", "field_82225_f");
    } catch (Exception e) {
      Log.error("Could not create ender creeper  logic as fields not found");
    }
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    MobInfo.CONCUSSION_CREEPER.applyAttributes(this);
  }

  @Override
  public void onUpdate() {

    if (isEntityAlive()) {
      int timeSinceIgnited = getTimeSinceIgnited();
      int fuseTime = getFuseTime();

      if (timeSinceIgnited >= fuseTime - 1) {
        setTimeSinceIgnited(0);

        int range = Config.concussionCreeperExplosionRange;
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
        @SuppressWarnings("unchecked")
        List<EntityLivingBase> ents = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
        for (EntityLivingBase ent : ents) {
          if (ent != this) {
            if (!worldObj.isRemote) {
              boolean done = false;
              for (int i = 0; i < 20 && !done; i++) {
                done = TeleportHelper.teleportRandomly(ent, Config.concussionCreeperMaxTeleportRange);
              }
            }
            if (ent instanceof EntityPlayer) {
              worldObj.playSoundEffect(ent.posX, ent.posY, ent.posZ, "mob.endermen.portal", 1.0F, 1.0F);
              EnderZoo.proxy.setInstantConfusionOnPlayer((EntityPlayer) ent, Config.concussionCreeperConfusionDuration);
            }
          }
        }

        worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        worldObj.spawnParticle("hugeexplosion", posX, posY, posZ, 1.0D, 0.0D, 0.0D);
        setDead();
      }
    }

    super.onUpdate();

  }

  @Override
  protected void dropFewItems(boolean hitByPlayer, int looting) {
    int j = rand.nextInt(3);
    if (looting > 0) {
      j += rand.nextInt(looting + 1);
    }
    for (int k = 0; k < j; ++k) {
      dropItem(getDropItem(), 1);
    }
  }

  @Override
  protected Item getDropItem() {
    int num = rand.nextInt(3);
    if (num == 0) {
      return EnderZoo.itemEnderFragment;
    } else if (num == 1) {
      return EnderZoo.itemConfusingDust;
    } else {
      return Items.gunpowder;
    }
  }

  private void setTimeSinceIgnited(int i) {
    if (fTimeSinceIgnited == null) {
      return;
    }
    try {
      fTimeSinceIgnited.setInt(this, i);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private int getTimeSinceIgnited() {
    if (fTimeSinceIgnited == null) {
      return 0;
    }
    try {
      return fTimeSinceIgnited.getInt(this);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  private int getFuseTime() {
    if (fFuseTime == null) {
      return 0;
    }
    try {
      return fFuseTime.getInt(this);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

}
