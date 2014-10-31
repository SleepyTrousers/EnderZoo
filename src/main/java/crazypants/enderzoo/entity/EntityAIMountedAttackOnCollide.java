package crazypants.enderzoo.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIMountedAttackOnCollide extends EntityAIBase {

  World worldObj;
  EntityCreature attacker;
  /**
   * An amount of decrementing ticks that allows the entity to attack once the
   * tick reaches 0.
   */
  int attackTick;
  /** The speed with which the mob will approach the target */
  double speedTowardsTarget;
  /**
   * When true, the mob will continue chasing its target, even if it can't find
   * a path to them right now.
   */
  boolean longMemory;
  /** The PathEntity of our entity. */
  PathEntity entityPathEntity;
  Class classTarget;
  private int pathUpdateTimer;
  private double targetPosX;
  private double targetPosY;
  private double targetPosZ;

  private int failedPathFindingPenalty;

  public EntityAIMountedAttackOnCollide(EntityCreature attacker, Class targetClass, double speedTowardsTarget, boolean longMemory) {
    this(attacker, speedTowardsTarget, longMemory);
    this.classTarget = targetClass;
  }

  public EntityAIMountedAttackOnCollide(EntityCreature attacker, double speedTowardsTarget, boolean longMemory) {
    this.attacker = attacker;
    this.worldObj = attacker.worldObj;
    this.speedTowardsTarget = speedTowardsTarget;
    this.longMemory = longMemory;
    this.setMutexBits(3);
  }

  /**
   * Returns whether the EntityAIBase should begin execution.
   */
  public boolean shouldExecute() {
    
    EntityLivingBase entitylivingbase = attacker.getAttackTarget();
    if(entitylivingbase == null) {
      return false;
    } else if(!entitylivingbase.isEntityAlive()) {
      return false;
    } else if(this.classTarget != null && !classTarget.isAssignableFrom(entitylivingbase.getClass())) {
      return false;
    } else {
      if(--pathUpdateTimer <= 0) {
        entityPathEntity = getNavigator().getPathToEntityLiving(entitylivingbase);
        pathUpdateTimer = 4 + attacker.getRNG().nextInt(7);
        return entityPathEntity != null;
      } else {
        return true;
      }
    }
  }

  /**
   * Returns whether an in-progress EntityAIBase should continue executing
   */
  public boolean continueExecuting() {
    EntityLivingBase entitylivingbase = attacker.getAttackTarget();
    return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (!longMemory ? !getNavigator().noPath()
        : attacker.isWithinHomeDistance(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY),
            MathHelper.floor_double(entitylivingbase.posZ))));
  }

  /**
   * Execute a one shot task or start executing a continuous task
   */
  public void startExecuting() {
    getNavigator().setPath(entityPathEntity, speedTowardsTarget);
    pathUpdateTimer = 0;
  }

  /**
   * Resets the task
   */
  public void resetTask() {
    getNavigator().clearPathEntity();
  }

  /**
   * Updates the task
   */
  public void updateTask() {
    
    EntityLivingBase target = attacker.getAttackTarget();
    attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);        
    --this.pathUpdateTimer;

    double distanceFromAttackerSq = this.attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
    if((longMemory || attacker.getEntitySenses().canSee(target))
        && pathUpdateTimer <= 0
        && (targetPosX == 0.0D && targetPosY == 0.0D && targetPosZ == 0.0D
            || target.getDistanceSq(targetPosX, targetPosY, targetPosZ) >= 1.0D || attacker.getRNG().nextFloat() < 0.05F)) {
      
      targetPosX = target.posX;
      targetPosY = target.boundingBox.minY;
      targetPosZ = target.posZ;
      pathUpdateTimer = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

      if(getNavigator().getPath() != null) {
        PathPoint finalPathPoint = getNavigator().getPath().getFinalPathPoint();
        if(finalPathPoint != null && target.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1) {
          failedPathFindingPenalty = 0;
        } else {
          failedPathFindingPenalty += 10;
        }
      } else {
        failedPathFindingPenalty += 10;
      }
      
      if(distanceFromAttackerSq > 1024.0D) {
        pathUpdateTimer += 10;
      } else if(distanceFromAttackerSq > 256.0D) {
        pathUpdateTimer += 5;
      }

      if(!getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget)) {
        this.pathUpdateTimer += 15;
      }
    }

    this.attackTick = Math.max(this.attackTick - 1, 0);
    double d1 = getAttackReach(target);
    if(distanceFromAttackerSq <= d1 && this.attackTick <= 20) {
      attackTick = 20;
      if(attacker.getHeldItem() != null) {
        attacker.swingItem();
      }
      attacker.attackEntityAsMob(target);
    }
  }

  protected PathNavigate getNavigator() {
    if(attacker.isRiding()) {
      Entity ent = attacker.ridingEntity;
      if(ent instanceof EntityLiving) {
        return ((EntityLiving)ent).getNavigator();
      }
    }
    return attacker.getNavigator();
  }
  
  protected double getAttackReach(EntityLivingBase target) {
    double res = attacker.width * 2.0 * attacker.width * 2.0 + target.width;
    if(attacker.isRiding()) {
      res += 1;
    }
    return res;
  }
}
