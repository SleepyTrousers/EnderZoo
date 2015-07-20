package crazypants.enderzoo.entity.ai;

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

  int attackPause;

  double speedTowardsTarget;
  double speedTowardsTargetMounted;

  /**
   * When true, the mob will continue chasing its target, even if it can't find
   * a path to them right now.
   */
  boolean longMemory;

  PathEntity entityPathEntity;
  Class<?> classTarget;
  private int pathUpdateTimer;
  private double targetPosX;
  private double targetPosY;
  private double targetPosZ;

  private int failedPathFindingPenalty;

  public EntityAIMountedAttackOnCollide(EntityCreature attacker, Class<?> targetClass, double speedTowardsTarget, double speedTowardsTargetMounted,
      boolean longMemory) {
    this(attacker, speedTowardsTarget, speedTowardsTargetMounted, longMemory);
    this.classTarget = targetClass;
  }

  public EntityAIMountedAttackOnCollide(EntityCreature attacker, double speedTowardsTarget, double speedTowardsTargetMounted, boolean longMemory) {
    this.attacker = attacker;
    this.worldObj = attacker.worldObj;
    this.speedTowardsTarget = speedTowardsTarget;
    this.speedTowardsTargetMounted = speedTowardsTargetMounted;
    this.longMemory = longMemory;
    this.setMutexBits(3);
  }

  /**
   * Returns whether the EntityAIBase should begin execution.
   */
  @Override
  public boolean shouldExecute() {

    EntityLivingBase entitylivingbase = attacker.getAttackTarget();
    if (entitylivingbase == null) {
      return false;
    } else if (!entitylivingbase.isEntityAlive()) {
      return false;
    } else if (this.classTarget != null && !classTarget.isAssignableFrom(entitylivingbase.getClass())) {
      return false;
    } else {
      if (--pathUpdateTimer <= 0) {
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
  @Override
  public boolean continueExecuting() {
    EntityLivingBase entitylivingbase = attacker.getAttackTarget();
    return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (!longMemory ? !getNavigator().noPath() : attacker
        .isWithinHomeDistance(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY),
            MathHelper.floor_double(entitylivingbase.posZ))));
  }

  /**
   * Execute a one shot task or start executing a continuous task
   */
  @Override
  public void startExecuting() {
    getNavigator().setPath(entityPathEntity, speedTowardsTarget);
    pathUpdateTimer = 0;
  }

  /**
   * Resets the task
   */
  @Override
  public void resetTask() {
    getNavigator().clearPathEntity();
  }

  /**
   * Updates the task
   */
  @Override
  public void updateTask() {

    EntityLivingBase target = attacker.getAttackTarget();
    attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
    --pathUpdateTimer;

    double distanceFromAttackerSq = attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
    if ((longMemory || attacker.getEntitySenses().canSee(target))
        && pathUpdateTimer <= 0
        && (targetPosX == 0.0D && targetPosY == 0.0D && targetPosZ == 0.0D || target.getDistanceSq(targetPosX, targetPosY, targetPosZ) >= 1.0D || attacker
            .getRNG().nextFloat() < 0.05F)) {

      targetPosX = target.posX;
      targetPosY = target.boundingBox.minY;
      targetPosZ = target.posZ;
      pathUpdateTimer = failedPathFindingPenalty + 4 + attacker.getRNG().nextInt(7);

      if (getNavigator().getPath() != null) {
        PathPoint finalPathPoint = getNavigator().getPath().getFinalPathPoint();
        if (finalPathPoint != null && target.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1) {
          failedPathFindingPenalty = 0;
        } else {
          failedPathFindingPenalty += 10;
        }
      } else {
        failedPathFindingPenalty += 10;
      }

      if (distanceFromAttackerSq > 1024.0D) {
        pathUpdateTimer += 10;
      } else if (distanceFromAttackerSq > 256.0D) {
        pathUpdateTimer += 5;
      }

      if (!getNavigator().tryMoveToEntityLiving(target, getAttackSpeed())) {
        pathUpdateTimer += 15;
      }
    }

    attackPause = Math.max(attackPause - 1, 0);
    double d1 = getAttackReach(target);
    if (distanceFromAttackerSq <= d1 && attackPause <= 20) {
      attackPause = 20;
      if (attacker.getHeldItem() != null) {
        attacker.swingItem();
      }
      attacker.attackEntityAsMob(target);
    }
  }

  private double getAttackSpeed() {
    if (attacker.isRiding()) {
      return speedTowardsTargetMounted;
    }
    return speedTowardsTarget;
  }

  protected PathNavigate getNavigator() {
    if (attacker.isRiding()) {
      Entity ent = attacker.ridingEntity;
      if (ent instanceof EntityLiving) {
        return ((EntityLiving) ent).getNavigator();
      }
    }
    return attacker.getNavigator();
  }

  protected double getAttackReach(EntityLivingBase target) {
    double res = attacker.width * 2.0 * attacker.width * 2.0 + target.width;
    if (attacker.isRiding()) {
      res += 1;
    }
    return res;
  }
}
