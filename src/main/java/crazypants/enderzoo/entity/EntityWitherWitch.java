package crazypants.enderzoo.entity;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.potion.BrewingUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityWitherWitch extends EntityMob implements IRangedAttackMob {

  public static final String NAME = "enderzoo.WitherWitch";
  public static final int EGG_BG_COL = 0x26520D;
  public static final int EGG_FG_COL = 0x905E43;
  
  private ItemStack[] drops = new ItemStack[] {
      new ItemStack(EnderZoo.itemWitheringDust),
      new ItemStack(EnderZoo.itemWitheringDust), 
      new ItemStack(EnderZoo.itemWitheringDust), 
      BrewingUtil.createHealthPotion(false, false, true), 
      BrewingUtil.createWitherPotion(false, true),
      BrewingUtil.createWitherPotion(false, true), 
      BrewingUtil.createRegenerationPotion(false, false, true)      
  };
  

  private int attackTimer;
  private int healTimer; 
  private boolean isHealing;

  public EntityWitherWitch(World world) {
    super(world);
    this.tasks.addTask(1, new EntityAISwimming(this));        
    this.tasks.addTask(1, new EntityAISwimming(this));
    this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
    this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
    this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    this.tasks.addTask(3, new EntityAILookIdle(this));
    this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));    
  }

  @Override
  protected boolean isAIEnabled() {
    return true;
  }

  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Config.witherWitchHealth);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
  }

  protected float applyPotionDamageCalculations(DamageSource damageSource, float damage) {
    //same as a vanilla witch
    damage = super.applyPotionDamageCalculations(damageSource, damage);
    if(damageSource.getEntity() == this) {
      damage = 0.0F;
    }
    if(damageSource.isMagicDamage()) {
      damage = (float) ((double) damage * 0.15D);
    }    
    return damage;
  }

  @Override
  public boolean isPotionApplicable(PotionEffect potion) {    
    return potion.getPotionID() != Potion.wither.id && super.isPotionApplicable(potion);
  }

  protected void dropFewItems(boolean hitByPlayer, int lootingLevel) {
    int numDrops = rand.nextInt(1) + 1;
    if(lootingLevel > 0) {
      numDrops += rand.nextInt(lootingLevel + 1);
    }
    for (int i = 0; i < numDrops; ++i) {      
      ItemStack item = drops[rand.nextInt(drops.length)].copy();            
      entityDropItem(item, 0);      
    }
  }

  public void onLivingUpdate() {    
    if(worldObj.isRemote) {
      super.onLivingUpdate();
      return;
    }
    attackTimer--;
    healTimer--;  
    if(shouldStartHeal()) {      
      ItemStack potion;
      if(rand.nextFloat() > 0.75) {
        potion = BrewingUtil.createRegenerationPotion(false, true, true); 
      } else {
        potion = BrewingUtil.createHealthPotion(false, false, true); 
      }      
      setCurrentItemOrArmor(0, potion);
      healTimer = 10;
      isHealing = true;
    } else if(entityToAttack != null && getHeldItem() == null) {      
      ItemStack potion = BrewingUtil.createWitherPotion(false, true);
      setCurrentItemOrArmor(0, potion);
      attackTimer = 10;  
      healTimer = 40;
    }
    if(isHealing && healTimer <= 0) {
      throwHealthPotion();
      isHealing = false;
    }
    super.onLivingUpdate();    
  }

  
  public boolean shouldStartHeal() {
    if(isPotionActive(Potion.regeneration)) {
      return false;
    }
    return getHealth() < getMaxHealth() * 0.75 && rand.nextFloat() > 0.5 && healTimer <= 0;
  }

  public void attackEntityWithRangedAttack(EntityLivingBase entity, float rangeRatio) {   
    if(entityToAttack == null) {
      entityToAttack = entity;
    }
    if(attackTimer <= 0 && getHeldItem() != null && !isHealing) {
      
      double x = entity.posX + entity.motionX - posX;
      double y = entity.posY + (double) entity.getEyeHeight() - 1.100000023841858D - posY;
      double z = entity.posZ + entity.motionZ - posZ;
      float groundDistance = MathHelper.sqrt_double(x * x + z * z);

      ItemStack potion = getHeldItem();      
      attackTimer = getHeldItem().getMaxItemUseDuration();      
      
      EntityPotion entitypotion = new EntityPotion(worldObj, this, potion);      
      entitypotion.rotationPitch -= -20.0F;
      entitypotion.setThrowableHeading(x, y + (double) (groundDistance * 0.2F), z, 0.75F, 8.0F);
      worldObj.spawnEntityInWorld(entitypotion);               
    }
  }
  
  private void throwHealthPotion() {
    ItemStack potion = getHeldItem();
    EntityPotion entitypotion = new EntityPotion(worldObj, this, potion);      
    Vec3 lookVec = getLookVec();  
    
    entitypotion.setThrowableHeading(lookVec.xCoord * 0.5, -1, lookVec.zCoord * 0.5, 0.75F, 1.0F);
    worldObj.spawnEntityInWorld(entitypotion);
    setCurrentItemOrArmor(0, null);
    healTimer = 80;        
  }
   
}
