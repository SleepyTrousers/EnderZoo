package crazypants.enderzoo.entity;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityFallenMount extends EntityHorse {

  public static final int EGG_BG_COL = 0x365A25;
  public static final int EGG_FG_COL = 0xA0A0A0;
  public static String NAME = "enderzoo.FallenMount";

  public EntityFallenMount(World world) {
    super(world);
    setHorseType(4);
    setHorseTamed(true);
    setGrowingAge(0);
    setHorseSaddled(true);      
  }

  @Override
  protected boolean isMovementBlocked() {
    if(riddenByEntity != null && !(riddenByEntity instanceof EntityPlayer)) {
      return false;
    }
    return super.isMovementBlocked();
  }

  public boolean interact(EntityPlayer p_70085_1_) {
    ItemStack itemstack = p_70085_1_.inventory.getCurrentItem();
    if(itemstack != null && itemstack.getItem() == Items.spawn_egg) {
      return super.interact(p_70085_1_);
    }
    return false;
  }

  public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
    setHorseType(3);
    setHorseSaddled(true);
    setGrowingAge(0);
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
    getAttributeMap().getAttributeInstanceByName("horse.jumpStrength").setBaseValue(0.5D);    
    setHealth(getMaxHealth());
    return data;
  }

}
