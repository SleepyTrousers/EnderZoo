package crazypants.enderzoo.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
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
    
    EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
    if(entitylivingbase == null) {
      return false;
    } else if(!entitylivingbase.isEntityAlive()) {
      return false;
    } else if(this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass())) {
      return false;
    } else {
      if(--this.pathUpdateTimer <= 0) {
        this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
        this.pathUpdateTimer = 4 + this.attacker.getRNG().nextInt(7);
        return this.entityPathEntity != null;
      } else {
        return true;
      }
    }
  }

  /**
   * Returns whether an in-progress EntityAIBase should continue executing
   */
  public boolean continueExecuting() {
    EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
    return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (!this.longMemory ? !this.attacker.getNavigator().noPath()
        : this.attacker.isWithinHomeDistance(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY),
            MathHelper.floor_double(entitylivingbase.posZ))));
  }

  /**
   * Execute a one shot task or start executing a continuous task
   */
  public void startExecuting() {
    this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
    this.pathUpdateTimer = 0;
  }

  /**
   * Resets the task
   */
  public void resetTask() {
    this.attacker.getNavigator().clearPathEntity();
  }

  /**
   * Updates the task
   */
  public void updateTask() {
    
    EntityLivingBase target = this.attacker.getAttackTarget();
    this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);        
    --this.pathUpdateTimer;

    double distanceFromAttackerSq = this.attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
    if((this.longMemory || this.attacker.getEntitySenses().canSee(target))
        && this.pathUpdateTimer <= 0
        && (this.targetPosX == 0.0D && this.targetPosY == 0.0D && this.targetPosZ == 0.0D
            || target.getDistanceSq(this.targetPosX, this.targetPosY, this.targetPosZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F)) {
      
      this.targetPosX = target.posX;
      this.targetPosY = target.boundingBox.minY;
      this.targetPosZ = target.posZ;
      this.pathUpdateTimer = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

      if(this.attacker.getNavigator().getPath() != null) {
        PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
        if(finalPathPoint != null && target.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1) {
          failedPathFindingPenalty = 0;
        } else {
          failedPathFindingPenalty += 10;
        }
      } else {
        failedPathFindingPenalty += 10;
      }
      
      if(distanceFromAttackerSq > 1024.0D) {
        this.pathUpdateTimer += 10;
      } else if(distanceFromAttackerSq > 256.0D) {
        this.pathUpdateTimer += 5;
      }

      if(!this.attacker.getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget)) {
        this.pathUpdateTimer += 15;
      }
    }

    this.attackTick = Math.max(this.attackTick - 1, 0);
    double d1 = getAttackReach(target);
    if(distanceFromAttackerSq <= d1 && this.attackTick <= 20) {
      this.attackTick = 20;
      if(this.attacker.getHeldItem() != null) {
        this.attacker.swingItem();
      }
      this.attacker.attackEntityAsMob(target);
    }
  }

  private double getAttackReach(EntityLivingBase target) {
    double res = attacker.width * 2.0 * attacker.width * 2.0 + target.width;
    if(attacker.isRiding()) {
      res += 1;
    }
    return res;
  }
}
