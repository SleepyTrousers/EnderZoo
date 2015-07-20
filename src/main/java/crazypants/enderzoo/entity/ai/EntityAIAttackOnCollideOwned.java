package crazypants.enderzoo.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import crazypants.enderzoo.entity.IOwnable;

public class EntityAIAttackOnCollideOwned extends EntityAIAttackOnCollide {

  private IOwnable<? extends EntityCreature, ? extends EntityLivingBase> ownable;

  private boolean retreating;
  private EntityAIFollowOwner followTask;

  public EntityAIAttackOnCollideOwned(IOwnable<? extends EntityCreature, ? extends EntityLivingBase> ownable, Class<?> p_i1635_2_, double p_i1635_3_,
      boolean p_i1635_5_, EntityAIFollowOwner followTask) {
    super(ownable.asEntity(), p_i1635_2_, p_i1635_3_, p_i1635_5_);
    this.ownable = ownable;
    this.followTask = followTask;
  }

  public EntityAIAttackOnCollideOwned(IOwnable<? extends EntityCreature, ? extends EntityLivingBase> ownable, double p_i1636_2_, boolean p_i1636_4_,
      EntityAIFollowOwner followTask) {
    super(ownable.asEntity(), p_i1636_2_, p_i1636_4_);
    this.ownable = ownable;
    this.followTask = followTask;
  }

  @Override
  public boolean continueExecuting() {
    return super.continueExecuting() || retreating;
  }

  @Override
  public void resetTask() {
    super.resetTask();
    retreating = false;
    followTask.resetTask();
  }

  @Override
  public void updateTask() {
    if (retreating) {
      followTask.updateTask();
      if (followTask.isWithinTargetDistanceFromOwner()) {
        retreating = false;
        followTask.resetTask();
        super.startExecuting();
      }
      return;
    }
    if (isTooFarFromOwner()) {
      retreating = true;
      followTask.startExecuting();
      return;
    }
    super.updateTask();
  }

  private boolean isTooFarFromOwner() {
    if (ownable.getOwner() == null) {
      return false;
    }
    double distance = getDistanceSqFromOwner();
    double maxRange = ownable.asEntity().getNavigator().getPathSearchRange();
    maxRange = maxRange * maxRange;
    return distance > maxRange;

  }

  private double getDistanceSqFromOwner() {
    return ownable.asEntity().getDistanceSqToEntity(ownable.getOwner());
  }

}
