package crazypants.enderzoo.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityFallenMount extends EntityHorse {

  public static final int EGG_BG_COL = 0x365A25;
  public static final int EGG_FG_COL = 0xA0A0A0;
  public static String NAME = "enderzoo.FallenMount";

  public static final double MOUNTED_ATTACK_MOVE_SPEED = 2.5;
  
  private boolean wasRidden = false;
  private final EntityAINearestAttackableTarget findTargetAI;
  private EntityAIAttackOnCollide attackAI;

  public EntityFallenMount(World world) {
    super(world);
    setHorseTamed(true);
    setGrowingAge(0);
    setHorseSaddled(true);

    tasks.taskEntries.clear();
    tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(6, new EntityAIWander(this, 0.7D));
    tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    tasks.addTask(8, new EntityAILookIdle(this));

    findTargetAI = new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true);    
    attackAI = new EntityAIAttackOnCollide(this, EntityPlayer.class, MOUNTED_ATTACK_MOVE_SPEED, false);
    updateAttackAI(); 
  }
  
  

  @Override
  protected void applyEntityAttributes() {  
    super.applyEntityAttributes();
    getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
    //TODO
    getAttributeMap().getAttributeInstance(SharedMonsterAttributes.attackDamage).setBaseValue(4);
  }

  @Override
  protected boolean isMovementBlocked() {
    return isRearing();
  }

  @Override
  public boolean interact(EntityPlayer p_70085_1_) {
    ItemStack itemstack = p_70085_1_.inventory.getCurrentItem();
    if(itemstack != null && itemstack.getItem() == Items.spawn_egg) {
      return super.interact(p_70085_1_);
    }
    return false;
  }

  @Override
  public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
    setHorseType(3);
    setHorseSaddled(true);
    setGrowingAge(0);
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2);
    getAttributeMap().getAttributeInstanceByName("horse.jumpStrength").setBaseValue(0.5);
    setHealth(getMaxHealth());
    return data;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    if(!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
      setDead();
    }
  }

  @Override
  public void onLivingUpdate() {
    //    if(worldObj.isDaytime() && !worldObj.isRemote) {
    //      float f = getBrightness(1.0F);
    //      if(f > 0.5F && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F
    //          && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))) {
    //        setFire(8);
    //      }
    //    }
    super.onLivingUpdate();
    setEatingHaystack(false);
    setEatingHaystack(false);
    setEating(false);
        
    if(wasRidden != isRidden()) {
      updateAttackAI();
      wasRidden = isRidden();      
    }   
  }

  protected boolean isRidden() {
    return riddenByEntity != null;
  }

  private void updateAttackAI() {
    targetTasks.removeTask(findTargetAI);
    tasks.removeTask(attackAI);
    if(!isRidden()) {
      targetTasks.addTask(2, findTargetAI);
      tasks.addTask(4, attackAI);
    }    
  }

  public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
    //Need to pretend we arn't being ridden else it will update as if a player was riding us    
    Entity prev = riddenByEntity;
    riddenByEntity = null;
    super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
    riddenByEntity = prev;
  }

  @Override
  public boolean attackEntityAsMob(Entity target) {
    if(isRidden() || isDead) {
      return false;
    }
    super.attackEntityAsMob(target);
    if(!isRearing()) {
      makeHorseRearWithSound();      
    }    
    float damage = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();    
    return target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
  }

}
