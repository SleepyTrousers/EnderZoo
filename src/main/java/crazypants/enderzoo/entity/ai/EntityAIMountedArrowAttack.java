package crazypants.enderzoo.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;

public class EntityAIMountedArrowAttack extends EntityAIBase {

  private final EntityLiving entityHost;

  private final IRangedAttackMob rangedAttackEntityHost;
  private EntityLivingBase attackTarget;
  private double entityMoveSpeed;
  private double mountedEntityMoveSpeed;

  private int timeUntilNextAttack;
  private int timeTargetHidden;

  private int minRangedAttackTime;
  private int maxRangedAttackTime;

  private float attackRange;
  private float attackRangeSq;

  public EntityAIMountedArrowAttack(IRangedAttackMob host, double moveSpeed, double mountedEntityMoveSpeed, int minAttackTime, int maxAttackTime, float attackRange) {
    this.timeUntilNextAttack = -1;
    this.rangedAttackEntityHost = host;
    this.entityHost = (EntityLiving) host;
    this.entityMoveSpeed = moveSpeed;
    this.mountedEntityMoveSpeed = mountedEntityMoveSpeed;
    this.minRangedAttackTime = minAttackTime;
    this.maxRangedAttackTime = maxAttackTime;
    this.attackRange = attackRange;
    this.attackRangeSq = attackRange * attackRange;
    this.setMutexBits(3);
  }

  @Override
  public boolean shouldExecute() {
    EntityLivingBase toAttack = entityHost.getAttackTarget();
    if(toAttack == null) {
      return false;
    } else {
      attackTarget = toAttack;
      return true;
    }
  }

  public boolean continueExecuting() {
    return shouldExecute() || !getNavigator().noPath();
  }

  public void resetTask() {
    this.attackTarget = null;
    this.timeTargetHidden = 0;
    this.timeUntilNextAttack = -1;
  }

  /**
   * Updates the task
   */
  public void updateTask() {
    double distToTargetSq = entityHost.getDistanceSq(attackTarget.posX, attackTarget.boundingBox.minY, attackTarget.posZ);
    boolean canSeeTarget = entityHost.getEntitySenses().canSee(attackTarget);

    if(canSeeTarget) {
      ++timeTargetHidden;
    } else {
      timeTargetHidden = 0;
    }

    if(distToTargetSq <= attackRangeSq && timeTargetHidden >= 20) {
      getNavigator().clearPathEntity();
    } else if (distToTargetSq > (attackRangeSq * 0.9)) {
      getNavigator().tryMoveToEntityLiving(attackTarget, getMoveSpeed());
    }

    entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);

    if(--timeUntilNextAttack == 0) {
      if(distToTargetSq > attackRangeSq || !canSeeTarget) {
        return;
      }
      float rangeRatio = MathHelper.sqrt_double(distToTargetSq) / attackRange;
      rangeRatio = MathHelper.clamp_float(rangeRatio, 0.1f, 1);
      rangedAttackEntityHost.attackEntityWithRangedAttack(attackTarget, rangeRatio);
      timeUntilNextAttack = MathHelper.floor_float(rangeRatio * (maxRangedAttackTime - minRangedAttackTime) + minRangedAttackTime);
    } else if(this.timeUntilNextAttack < 0) {
      float rangeRatio = MathHelper.sqrt_double(distToTargetSq) / attackRange;
      timeUntilNextAttack = MathHelper.floor_float(rangeRatio * (maxRangedAttackTime - minRangedAttackTime) + minRangedAttackTime);
    }
  }

  private double getMoveSpeed() {
    if(entityHost.isRiding()) {
      return mountedEntityMoveSpeed;
    }
    return entityMoveSpeed;
  }

  protected PathNavigate getNavigator() {
    if(entityHost.isRiding()) {
      Entity ent = entityHost.ridingEntity;
      if(ent instanceof EntityLiving) {
        return ((EntityLiving) ent).getNavigator();
      }
    }
    return entityHost.getNavigator();
  }
}