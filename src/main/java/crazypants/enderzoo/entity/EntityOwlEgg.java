package crazypants.enderzoo.entity;

import crazypants.enderzoo.EnderZoo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.math.text.translation.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityOwlEgg extends EntityThrowable {
  
  public EntityOwlEgg(World worldIn) {
    super(worldIn);
  }

  public EntityOwlEgg(World worldIn, EntityLivingBase throwerIn) {
    super(worldIn, throwerIn);
  }

  public EntityOwlEgg(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  @Override
  protected void onImpact(MovingObjectPosition impact) {
    if (impact.entityHit != null) {
      impact.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
    }

    if (!worldObj.isRemote && rand.nextInt(8) == 0) {
      EntityOwl entitychicken = new EntityOwl(worldObj);
      entitychicken.setGrowingAge(-24000);
      entitychicken.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
      worldObj.spawnEntityInWorld(entitychicken);
    }
    for (int i = 0; i < 8; ++i) {
      worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, (rand.nextFloat() - 0.5D) * 0.08D,
          (rand.nextFloat() - 0.5D) * 0.08D, (rand.nextFloat() - 0.5D) * 0.08D, new int[] { Item.getIdFromItem(EnderZoo.itemOwlEgg) });
    }
    if (!worldObj.isRemote) {
      setDead();
    }
  }
}
