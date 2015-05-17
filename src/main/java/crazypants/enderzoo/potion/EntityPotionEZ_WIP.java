package crazypants.enderzoo.potion;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPotionEZ_WIP extends EntityThrowable {

  private ItemStack potion;

  public EntityPotionEZ_WIP(World world) {
    super(world);
  }

  public EntityPotionEZ_WIP(World world, EntityLivingBase thrownBy, ItemStack potion) {
    super(world, thrownBy);
    this.potion = potion;
  }

  @Override
  protected void onImpact(MovingObjectPosition objPosition) {
    if (worldObj.isRemote) {
      return;
    }
    //    List<PotionEffect> effects = EnderZoo.itemPotionEZ.getEffects(potion);
    //    if(effects != null && !effects.isEmpty()) {
    //      
    //      AxisAlignedBB axisalignedbb = boundingBox.expand(4.0D, 2.0D, 4.0D);
    //      List<EntityLivingBase> applyToEntities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
    //      if(applyToEntities != null && !applyToEntities.isEmpty()) {
    //
    //        for (EntityLivingBase entityHit : applyToEntities) {
    //          double distanceFromHitSq = getDistanceSqToEntity(entityHit);
    //          if(distanceFromHitSq < 16.0D) {
    //            double distanceFromHitRatio = 1.0D - Math.sqrt(distanceFromHitSq) / 4.0D;
    //            if(entityHit == objPosition.entityHit) {
    //              distanceFromHitRatio = 1.0D;
    //            }
    //            applyEffects(effects, entityHit, distanceFromHitRatio);            
    //          }
    //        }
    //      }
    //    }

    worldObj.playAuxSFX(2002, (int) Math.round(posX), (int) Math.round(posY), (int) Math.round(posZ), 16460);
    setDead();

  }

  public void applyEffects(List<PotionEffect> effects, EntityLivingBase entityHit, double distanceFromHitRatio) {
    for (PotionEffect effect : effects) {
      int potionId = effect.getPotionID();
      if (Potion.potionTypes[potionId].isInstant()) {
        Potion.potionTypes[potionId].affectEntity(getThrower(), entityHit, effect.getAmplifier(), distanceFromHitRatio);
      } else {
        int duration = (int) (distanceFromHitRatio * effect.getDuration() + 0.5);
        if (duration > 20) {
          entityHit.addPotionEffect(new PotionEffect(potionId, duration, effect.getAmplifier()));
        }
      }
    }
  }

  @Override
  protected float getGravityVelocity() {
    return 0.05F;
  }

  @Override
  protected float func_70182_d() {
    return 0.5F;
  }

  @Override
  protected float func_70183_g() {
    return -20.0F;
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound root) {

    super.readEntityFromNBT(root);
    if (root.hasKey("potion")) {
      System.out.println("EntityPotionEZ.readEntityFromNBT: read potion");
      potion = ItemStack.loadItemStackFromNBT(root.getCompoundTag("potion"));
    }
    if (potion == null) {
      System.out.println("EntityPotionEZ.readEntityFromNBT:no potion ");
      setDead();
    }
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound root) {

    super.writeEntityToNBT(root);
    if (potion != null) {
      System.out.println("EntityPotionEZ.writeEntityToNBT: wrote potion");
      root.setTag("potion", potion.writeToNBT(new NBTTagCompound()));
    } else {
      System.out.println("EntityPotionEZ.writeEntityToNBT: no poition");
    }
  }

}
