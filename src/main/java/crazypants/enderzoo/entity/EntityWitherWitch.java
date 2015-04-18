package crazypants.enderzoo.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityWitherCat.GrowthMode;
import crazypants.enderzoo.entity.ai.EntityAIRangedAttack;
import crazypants.enderzoo.potion.BrewingUtil;
import crazypants.enderzoo.vec.Point3i;

public class EntityWitherWitch extends EntityMob implements IRangedAttackMob, IEnderZooMob {

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
  private EntityLivingBase attackedWithPotion;

  private int healTimer;
  private boolean isHealing;

  private boolean spawned;
  private boolean firstUpdate = true;

  private final List<EntityWitherCat> cats = new ArrayList<EntityWitherCat>();
  private List<NBTTagCompound> loadedCats;
  private final EntityAIRangedAttack rangedAttackAI;
  private int noActiveTargetTime;

  public EntityWitherWitch(World world) {
    super(world);
    rangedAttackAI = new EntityAIRangedAttack(this, 1, 60, 10);
    tasks.addTask(1, new EntityAISwimming(this));
    tasks.addTask(1, new EntityAISwimming(this));    
    tasks.addTask(2, rangedAttackAI);
    tasks.addTask(2, new EntityAIWander(this, 1.0D));
    tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    tasks.addTask(3, new EntityAILookIdle(this));
    targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
  }

  @Override
  protected boolean isAIEnabled() {
    return true;
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    MobInfo.WITHER_WITCH.applyAttributes(this);
  }

  @Override
  protected float applyPotionDamageCalculations(DamageSource damageSource, float damage) {
    //same as a vanilla witch
    damage = super.applyPotionDamageCalculations(damageSource, damage);
    if(damageSource.getEntity() == this) {
      damage = 0.0F;
    }
    if(damageSource.isMagicDamage()) {
      damage = (float) (damage * 0.15D);
    }
    return damage;
  }

  @Override
  public boolean isPotionApplicable(PotionEffect potion) {
    return potion.getPotionID() != Potion.wither.id && super.isPotionApplicable(potion);
  }

  @Override
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

  @Override
  public void setRevengeTarget(EntityLivingBase target) {
    EntityLivingBase curTarget = getAITarget();
    super.setRevengeTarget(target);
    if(curTarget == target || worldObj.isRemote || target == null) {
      return;
    }
    float distToSrc = getDistanceToEntity(target);
    if(distToSrc > getNavigator().getPathSearchRange() && distToSrc < 50) {
      getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).setBaseValue(distToSrc + 2);
    }
  }

  @Override
  public IEntityLivingData onSpawnWithEgg(IEntityLivingData p_110161_1_) {
    spawned = true;
    return super.onSpawnWithEgg(p_110161_1_);
  }

  @Override
  public void onLivingUpdate() {
    if(worldObj.isRemote) {
      super.onLivingUpdate();
      return;
    }

    if(firstUpdate) {
      if(spawned) {
        spawnCats();
      } else {
        loadCats();
      }
      firstUpdate = false;
    }
    doAttackActions();
    manageCats();

    super.onLivingUpdate();
  }

  protected void doAttackActions() {
    attackTimer--;
    healTimer--;

    EntityLivingBase target = getActiveTarget();
    if(target == null) {
      noActiveTargetTime++;
    } else {
      noActiveTargetTime = 0;
    }

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
    } else if(target != null && getHeldItem() == null) {
      ItemStack potion;
      if(getActiveTarget().isPotionActive(Potion.wither)) {
        potion = BrewingUtil.createHarmingPotion(EntityUtil.isHardDifficulty(worldObj), true);
      } else {
        potion = BrewingUtil.createWitherPotion(false, true);
      }
      setCurrentItemOrArmor(0, potion);
      attackTimer = 10;
      healTimer = 40;
    } else if(noActiveTargetTime > 40 && !isHealing && getHeldItem() != null) {
      setCurrentItemOrArmor(0, null);
      attackedWithPotion = null;
    }
    if(isHealing && healTimer <= 0) {
      throwHealthPotion();
      isHealing = false;
    }
  }

  protected EntityLivingBase getActiveTarget() {
    EntityLivingBase res = getAttackTarget();
    if(res == null) {
      res = rangedAttackAI.getAttackTarget();
    }
    return res;
  }

  protected boolean shouldStartHeal() {
    if(isPotionActive(Potion.regeneration)) {
      return false;
    }
    return getHealth() < getMaxHealth() * 0.75 && rand.nextFloat() > 0.5 && healTimer <= 0;
  }

  @Override
  public void attackEntityWithRangedAttack(EntityLivingBase entity, float rangeRatio) {
    if(attackTimer <= 0 && getHeldItem() != null && !isHealing) {

      attackedWithPotion = entity;

      double x = entity.posX + entity.motionX - posX;
      double y = entity.posY + entity.getEyeHeight() - 1.100000023841858D - posY;
      double z = entity.posZ + entity.motionZ - posZ;
      float groundDistance = MathHelper.sqrt_double(x * x + z * z);

      ItemStack potion = getHeldItem();
      attackTimer = getHeldItem().getMaxItemUseDuration();

      EntityPotion entitypotion = new EntityPotion(worldObj, this, potion);
      entitypotion.rotationPitch -= -20.0F;
      entitypotion.setThrowableHeading(x, y + groundDistance * 0.2F, z, 0.75F, 8.0F);
      worldObj.spawnEntityInWorld(entitypotion);

      setCurrentItemOrArmor(0, null);
    }
  }

  protected void throwHealthPotion() {
    ItemStack potion = getHeldItem();
    EntityPotion entitypotion = new EntityPotion(worldObj, this, potion);
    Vec3 lookVec = getLookVec();

    entitypotion.setThrowableHeading(lookVec.xCoord * 0.5, -1, lookVec.zCoord * 0.5, 0.75F, 1.0F);
    worldObj.spawnEntityInWorld(entitypotion);
    setCurrentItemOrArmor(0, null);
    healTimer = 80;
  }

  public void catDied(EntityWitherCat cat) {
    cats.remove(cat);
  }

  private void spawnCats() {
    if(!Config.witherCatEnabled) {
      return;
    }
    int numCats = rand.nextInt(Config.witherWitchMaxCats + 1);
    numCats = Math.max(numCats, Config.witherWitchMinCats);
    for (int i = 0; i < numCats; i++) {
      Point3i startPoint = EntityUtil.getEntityPositionI(this);
      startPoint.x += 4 - rand.nextInt(9);
      startPoint.z += 4 - rand.nextInt(9);
      Point3i spawnLoc = new Point3i();
      if(SpawnUtil.findClearGround(worldObj, startPoint, spawnLoc, 2, 10, true)) {
        spawnCat(spawnLoc);
      } else {
        return;
      }
    }
  }

  private void spawnCat(Point3i spawnLoc) {
    EntityWitherCat cat = new EntityWitherCat(worldObj);
    cat.onSpawnWithEgg(null);
    cat.setOwner(this);
    cat.setPositionAndRotation(spawnLoc.x + 0.5, spawnLoc.y + 0.5, spawnLoc.z + 0.5, rotationYaw, 0);
    if (MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.CheckSpawn(cat, worldObj, (float)cat.posX, (float)cat.posY, (float)cat.posZ))) {
      return;
    }
    if(!cat.getCanSpawnHere()) {
      return;
    }
    cats.add(cat);
    worldObj.spawnEntityInWorld(cat);
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound root) {
    super.writeEntityToNBT(root);
    if(cats.isEmpty()) {
      return;
    }
    NBTTagList catsList = new NBTTagList();
    for (EntityWitherCat cat : cats) {
      if(!cat.isDead) {
        NBTTagCompound catRoot = new NBTTagCompound();
        cat.writeToNBT(catRoot);
        catsList.appendTag(catRoot);
      }
    }
    if(catsList.tagCount() > 0) {
      root.setTag("cats", catsList);
    }
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound root) {
    super.readEntityFromNBT(root);
    if(!root.hasKey("cats")) {
      return;
    }
    NBTTagList catsList = (NBTTagList) root.getTag("cats");
    loadedCats = new ArrayList<NBTTagCompound>(catsList.tagCount());
    for (int i = 0; i < catsList.tagCount(); i++) {
      NBTTagCompound catRoot = catsList.getCompoundTagAt(i);
      if(catRoot != null) {
        loadedCats.add(catRoot);
      }
    }
  }

  private void loadCats() {
    if(loadedCats == null) {
      return;
    }
    for (NBTTagCompound catRoot : loadedCats) {
      if(catRoot != null) {
        EntityWitherCat cat = new EntityWitherCat(worldObj);
        cat.readFromNBT(catRoot);
        cat.setOwner(this);
        cats.add(cat);
        worldObj.spawnEntityInWorld(cat);
      }
    }
  }

  protected void manageCats() {
    if(cats.isEmpty()) {
      return;
    }
    if(noActiveTargetTime > 40) {
      pacifyCats();
      return;
    }
    EntityLivingBase currentTarget = getActiveTarget();
    EntityLivingBase hitBy = getAITarget();
    if(hitBy == null) {
      //agro the cats if we have been hit or we have actually thrown a potion
      hitBy = attackedWithPotion;
    }
    angerCats(currentTarget, hitBy);
  }

  private void angerCats(EntityLivingBase targ, EntityLivingBase hitBy) {
    for (EntityWitherCat cat : cats) {
      if(cat.isAngry()) {
        if(cat.getAttackTarget() != targ) {
          cat.setAttackTarget(targ);
        }
      } else if(cat.getGrowthMode() != GrowthMode.GROW && hitBy != null) {
        cat.setGrowthMode(GrowthMode.GROW);
      }
    }
  }

  private void pacifyCats() {
    for (EntityWitherCat cat : cats) {
      if(cat.isAngry()) {
        cat.setGrowthMode(GrowthMode.SHRINK);
        if(cat.getAttackTarget() != null) {
          cat.setAttackTarget(null);
        }
      }
    }
  }

}
