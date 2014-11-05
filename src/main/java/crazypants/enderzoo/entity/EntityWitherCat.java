package crazypants.enderzoo.entity;

import java.util.UUID;

import org.omg.PortableInterceptor.NON_EXISTENT;

import crazypants.enderzoo.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityWitherCat extends EntityMob implements IEnderZooMob{

  public enum GrowthMode {
    NONE,
    GROW,
    SHRINK    
  };
  
  public static final String NAME = "enderzoo.WitherCat";
  public static final int EGG_BG_COL = 0x303030;
  public static final int EGG_FG_COL = 0xFFFFFF;

  private static final float DEF_HEIGHT = 0.8F;
  private static final float DEF_WIDTH = 0.6F;
  
  private static final int SCALE_INDEX = 12;
  private static final int GROWTH_MODE_INDEX = 13;
  
  private static final float ANGRY_SCALE = 2;
  private static final float SCALE_INC = 0.05f;

  private static final UUID ATTACK_BOOST_MOD_UID = UUID.fromString("B9662B59-9566-4402-BC1F-2ED2B276D846");
  
  private AttributeModifier attackBoostMod = new AttributeModifier(ATTACK_BOOST_MOD_UID, "Transformed Attack Modifier", 0.0, 1);
  private float lastScale = 1f;
  private boolean grow;
  private boolean shrink;
  
  public EntityWitherCat(World world) {
    super(world);
    //    tasks.addTask(1, new EntityAISwimming(this));
    //    tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.5D, false));
    //    tasks.addTask(4, new EntityAIWander(this, 1.0D));
    //    tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    //    tasks.addTask(6, new EntityAILookIdle(this));
    //    targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    //    targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

    //new EntityAIFollowParent(, p_i1626_2_)

    setSize(DEF_WIDTH, DEF_HEIGHT);
  }

  protected void entityInit() {
    super.entityInit();
    getDataWatcher().addObject(SCALE_INDEX, 1f);
    getDataWatcher().addObject(GROWTH_MODE_INDEX, (byte)GrowthMode.NONE.ordinal());
  }

  public void setScale(float scale) {
    getDataWatcher().updateObject(12, scale);
  }
    
  public float getScale() {
    return getDataWatcher().getWatchableObjectFloat(12);
  }
  
  public void setGrowthMode(GrowthMode mode) {
    setGrowthMode(mode.ordinal());
  }
  
  private void setGrowthMode(int ordinal) {
    getDataWatcher().updateObject(GROWTH_MODE_INDEX, (byte)ordinal);    
  }
  
  public GrowthMode getGrowthMode() {
    return GrowthMode.values()[getDataWatcher().getWatchableObjectByte(GROWTH_MODE_INDEX)];
  }
  

  public float getAngryScale() {
    return ANGRY_SCALE;
  }
  
  public float getScaleInc() {
    return SCALE_INC;
  }

  @Override
  protected boolean isAIEnabled() {
    return true;
  }

  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Config.witherCatHealth);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(Config.witherCatAttackDamage);
  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    if(worldObj.isRemote) {
      float scale = getScale();
      if(lastScale != scale) {
        spawnParticles();
        lastScale = scale;
      }
      return;
    }

    updateScale();
    
    float scale = getScale();
    if(lastScale != scale) {
      lastScale = scale;
      setSize(DEF_WIDTH * scale, DEF_HEIGHT * scale);
      getEntityAttribute(SharedMonsterAttributes.attackDamage).removeAllModifiers();
      attackBoostMod = createAttackModifierForScale(lastScale);
      getEntityAttribute(SharedMonsterAttributes.attackDamage).applyModifier(attackBoostMod);
    }    
        
  }

  public void updateScale() {
    GrowthMode curMode = getGrowthMode();
    if(curMode == GrowthMode.NONE) {
      return;
    }
    
    float scale = getScale();
    if(curMode == GrowthMode.GROW) {
      if(scale < ANGRY_SCALE) {
        setScale(scale + SCALE_INC);
      } else {
        setScale(ANGRY_SCALE);        
        setGrowthMode(GrowthMode.NONE);
      }
    } else {
      if(scale > 1) {
        setScale(scale - SCALE_INC);
      } else {
        setScale(1);      
        setGrowthMode(GrowthMode.NONE);        
      }
    }       
  }

  private AttributeModifier createAttackModifierForScale(float scale) {    
    return new AttributeModifier(ATTACK_BOOST_MOD_UID, "Transformed Attack Modifier", scale - 1, 1);
  }

  private void spawnParticles() {
    double startX = posX;
    double startY = posY;
    double startZ = posZ;
    double offsetScale = 0.8 * getScale();
    for (int i = 0; i < 2; i++) {
      double xOffset = offsetScale - rand.nextFloat() * offsetScale * 2;
      double yOffset = offsetScale / 3 + rand.nextFloat() * offsetScale / 3 * 2F;
      double zOffset = offsetScale - rand.nextFloat() * offsetScale * 2;
      EntityFX fx = Minecraft.getMinecraft().renderGlobal.doSpawnParticle("spell", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
      if(fx != null) {
        fx.setRBGColorF(0.8f, 0.2f, 0.2f);
        fx.motionY *= 0.025f;
      }
    }
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound root) {
    super.writeEntityToNBT(root);
    root.setFloat("scale", getScale());
    root.setByte("growthMode", (byte)getGrowthMode().ordinal());
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound root) {
    super.readEntityFromNBT(root);
    if(root.hasKey("scale")) {
      setScale(root.getFloat("scale"));
    }
    if(root.hasKey("growthMode")) {
      setGrowthMode(root.getByte("growthMode"));      
    }
  }
  
  

}
