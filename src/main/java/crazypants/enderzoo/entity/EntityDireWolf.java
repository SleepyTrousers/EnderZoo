package crazypants.enderzoo.entity;

import java.util.List;

import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.ai.EntityAIAttackOnCollideAggressive;
import crazypants.enderzoo.entity.ai.EntityAINearestAttackableTargetBounded;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityDireWolf extends EntityMob implements IEnderZooMob {

  public static final String NAME = "enderzoo.DireWolf";
  public static final int EGG_BG_COL = 0x606060;
  public static final int EGG_FG_COL = 0xA0A0A0;

  private static final String SND_HURT = "enderzoo:direwolf.hurt";
  private static final String SND_HOWL = "enderzoo:direwolf.howl";
  private static final String SND_GROWL = "enderzoo:direwolf.growl";
  private static final String SND_DEATH = "enderzoo:direwolf.death";

  private static final int ANGRY_INDEX = 12;

  private EntityLivingBase previsousAttackTarget;

  private static int packHowl = 0;
  private static long lastHowl = 0;

  public EntityDireWolf(World world) {
    super(world);
    setSize(0.8F, 1.2F);
    //getNavigator().setAvoidsWater(true);
    ((PathNavigateGround) this.getNavigator()).setAvoidsWater(true);
    tasks.addTask(1, new EntityAISwimming(this));
    tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
    tasks.addTask(4, new EntityAIAttackOnCollideAggressive(this, 1.1D, true).setAttackFrequency(20));
    tasks.addTask(7, new EntityAIWander(this, 0.5D));
    tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    tasks.addTask(9, new EntityAILookIdle(this));
    targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));

    if(Config.direWolfAggresiveRange > 0) {      
      EntityAINearestAttackableTargetBounded<EntityPlayer> nearTarg = new EntityAINearestAttackableTargetBounded<EntityPlayer>(this, EntityPlayer.class, true);
      nearTarg.setMaxDistanceToTarget(Config.direWolfAggresiveRange);
      targetTasks.addTask(2, nearTarg);
    }
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    getDataWatcher().addObject(ANGRY_INDEX, (byte) 0);
    updateAngry();
  }

  public boolean isAngry() {
    return getDataWatcher().getWatchableObjectByte(ANGRY_INDEX) == 1;
  }

  @Override
  protected boolean isValidLightLevel() {
    return true;
  }

  @Override
  public int getMaxSpawnedInChunk() {
    return 6;
  }

  //  @Override
  //  public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
  //    System.out.println("EntityDireWolf.isCreatureType: " + type);
  //    return type.getCreatureClass().isAssignableFrom(this.getClass());
  //  }

  private void updateAngry() {
    getDataWatcher().updateObject(ANGRY_INDEX, getAttackTarget() != null ? (byte) 1 : (byte) 0);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
    getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    MobInfo.DIRE_WOLF.applyAttributes(this);
  }

  @Override
  protected void playStepSound(BlockPos bp, Block p_145780_4_) {
    playSound("mob.wolf.step", 0.15F, 1.0F);
  }

  @Override
  protected String getLivingSound() {
    if (isAngry()) {
      return SND_GROWL;
    }
    if (EntityUtil.isPlayerWithinRange(this, 12)) {
      return SND_GROWL;
    }
    boolean howl = (packHowl > 0 || rand.nextFloat() <= Config.direWolfHowlChance) && worldObj.getTotalWorldTime() > (lastHowl + 10);
    if (howl) {
      if (packHowl <= 0 && rand.nextFloat() <= 0.6) {
        packHowl = Config.direWolfPackHowlAmount;
      }
      lastHowl = worldObj.getTotalWorldTime();
      packHowl = Math.max(packHowl - 1, 0);
      return SND_HOWL;
    } else {
      return SND_GROWL;
    }
  }

  @Override
  public void playSound(String name, float volume, float pitch) {
    if (SND_HOWL.equals(name)) {
      volume *= (float) Config.direWolfHowlVolumeMult;
      pitch *= 0.8f;
    }
    worldObj.playSoundAtEntity(this, name, volume, pitch);
  }

  @Override
  protected String getHurtSound() {
    return SND_HURT;
  }

  @Override
  protected String getDeathSound() {
    return SND_DEATH;
  }

  @Override
  public float getEyeHeight() {
    return height * 0.8F;
  }

  @Override
  protected float getSoundVolume() {
    return 0.4F;
  }

  @Override
  protected Item getDropItem() {
    return Item.getItemById(-1);
  }

  public float getTailRotation() {
    if (isAngry()) {
      return (float) Math.PI / 2;
    }
    return (float) Math.PI / 4;
  }

  @Override
  public void setPosition(double x, double y, double z) {
    posX = x;
    posY = y;
    posZ = z;
    //Correct misalignment of bounding box    
    double hw = width / 2.0F;
    double hd = hw * 2.25;
    float f1 = height;

    setEntityBoundingBox(new AxisAlignedBB(
        x - hw, y, z - hd,
        x + hw, y + f1, z + hd));

  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    EntityLivingBase curTarget = getAttackTarget();
    if (curTarget != previsousAttackTarget) {
      if (curTarget != null) {
        doGroupArgo(curTarget);
      }
      previsousAttackTarget = getAttackTarget();
      updateAngry();
    }
  }

  private void doGroupArgo(EntityLivingBase curTarget) {
    if (!Config.direWolfPackAttackEnabled) {
      return;
    }
    int range = 16;
    AxisAlignedBB bb = new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
    List<EntityDireWolf> pack = worldObj.getEntitiesWithinAABB(EntityDireWolf.class, bb);
    if (pack != null && !pack.isEmpty()) {
      for (EntityDireWolf wolf : pack) {
        if (wolf.getAttackTarget() == null) {
          EntityUtil.cancelCurrentTasks(wolf);
          wolf.setAttackTarget(curTarget);
        }
      }
    }
  }

}
