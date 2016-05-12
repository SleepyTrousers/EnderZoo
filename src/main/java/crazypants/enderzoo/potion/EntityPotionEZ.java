package crazypants.enderzoo.potion;

import java.util.List;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.IoUtil;
import crazypants.enderzoo.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.math.text.translation.AxisAlignedBB;
import net.minecraft.util.math.math.text.translation.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityPotionEZ extends EntityThrowable implements IEntityAdditionalSpawnData {

  private ItemStack potion;

  public EntityPotionEZ(World worldIn) {
    super(worldIn);
  }

  public EntityPotionEZ(World worldIn, EntityLivingBase throwerIn, ItemStack potion) {
    super(worldIn, throwerIn);
    this.potion = potion;
  }

  public EntityPotionEZ(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  @Override
  protected float getGravityVelocity() {
    return 0.05F;
  }

  @Override
  protected float getVelocity() {
    return 0.5F;
  }

  @Override
  protected float getInaccuracy() {
    return -20.0F;
  }

  public ItemStack getPotion() {
    return potion;
  }

  public void setPotion(ItemStack potion) {
    this.potion = potion;
  }

  @Override
  protected void onImpact(MovingObjectPosition p_70184_1_) {
    if (!worldObj.isRemote && potion != null) {

      List<PotionEffect> effects = EnderZoo.itemPotionEZ.getEffects(potion);
      if (effects.isEmpty()) {
        return;
      }

      AxisAlignedBB axisalignedbb = getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D);
      List<EntityLivingBase> entities = worldObj.<EntityLivingBase> getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

      for (EntityLivingBase entity : entities) {
        double d0 = this.getDistanceSqToEntity(entity);

        if (d0 < 16.0D) {
          double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

          if (entity == p_70184_1_.entityHit) {
            d1 = 1.0D;
          }

          for (PotionEffect effect : effects) {
            int pid = effect.getPotionID();
            if (Potion.potionTypes[pid].isInstant()) {
              Potion.potionTypes[pid].affectEntity(this, getThrower(), entity, effect.getAmplifier(), d1);
            } else {
              int j = (int) (d1 * effect.getDuration() + 0.5D);
              entity.addPotionEffect(new PotionEffect(pid, j, effect.getAmplifier()));
            }
          }
        }
      }
      PacketHandler.sendToAllAround(new PacketSpawnSplashEffects(this, effects.get(0).getPotionID()), this);
      setDead();
    }
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound tagCompund) {
    super.readEntityFromNBT(tagCompund);
    if (tagCompund.hasKey("Potion", 10)) {
      potion = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("Potion"));
    }
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound tagCompound) {
    super.writeEntityToNBT(tagCompound);
    if (potion != null) {
      tagCompound.setTag("Potion", potion.writeToNBT(new NBTTagCompound()));
    }
  }

  @Override
  public void writeSpawnData(ByteBuf buffer) {

    if (potion != null) {
      IoUtil.writeNBTTagCompound(potion.writeToNBT(new NBTTagCompound()), buffer);
    } else {
      IoUtil.writeNBTTagCompound(null, buffer);
    }

  }

  @Override
  public void readSpawnData(ByteBuf additionalData) {
    NBTTagCompound nbt = IoUtil.readNBTTagCompound(additionalData);
    if(nbt != null) {
      potion = ItemStack.loadItemStackFromNBT(nbt);
    } else {
      potion = null;
    }
  }
}
