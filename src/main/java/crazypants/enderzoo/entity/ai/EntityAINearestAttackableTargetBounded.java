package crazypants.enderzoo.entity.ai;

import com.google.common.base.Predicate;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class EntityAINearestAttackableTargetBounded extends EntityAINearestAttackableTarget {

  private double distanceOverride = -1;  

  public EntityAINearestAttackableTargetBounded(EntityCreature p_i45879_1_, Class p_i45879_2_, boolean p_i45879_3_, boolean p_i45879_4_) {
    super(p_i45879_1_, p_i45879_2_, p_i45879_3_, p_i45879_4_);
  }

  public EntityAINearestAttackableTargetBounded(EntityCreature p_i45878_1_, Class p_i45878_2_, boolean p_i45878_3_) {
    super(p_i45878_1_, p_i45878_2_, p_i45878_3_);
  }

  public EntityAINearestAttackableTargetBounded(EntityCreature p_i45880_1_, Class p_i45880_2_, int p_i45880_3_, boolean p_i45880_4_, boolean p_i45880_5_,
      Predicate p_i45880_6_) {
    super(p_i45880_1_, p_i45880_2_, p_i45880_3_, p_i45880_4_, p_i45880_5_, p_i45880_6_);
  }

  public double getMaxDistanceToTarget() {
    return distanceOverride;
  }

  public void setMaxDistanceToTarget(double distanceOverride) {
    this.distanceOverride = distanceOverride;
  }
  
  @Override
  protected double getTargetDistance() {
    if(distanceOverride > 0) {
      return distanceOverride;
    }
    return super.getTargetDistance();
  }

}
