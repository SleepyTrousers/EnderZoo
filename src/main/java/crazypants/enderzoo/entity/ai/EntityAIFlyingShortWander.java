package crazypants.enderzoo.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class EntityAIFlyingShortWander extends EntityAIBase {

  private EntityCreature entity;
  protected double speed;
  private double randPosX;
  private double randPosY;
  private double randPosZ;
  private int executionChance;

  public EntityAIFlyingShortWander(EntityCreature creature, double speedIn, int executionChanceIn) {
    entity = creature;
    speed = speedIn;
    executionChance = executionChanceIn;
    setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {
    int chance = executionChance;
    if (isOnLeaves()) {
      chance *= 2;
    }
    if (entity.getRNG().nextInt(chance) != 0) {
      return false;
    }
    
    Vec3d vec3 = RandomPositionGenerator.findRandomTarget(entity, 4, 2);    
    if (vec3 == null || entity.posY - vec3.yCoord < -2) {
      return false;
    }    
    randPosX = vec3.xCoord;
    randPosY = vec3.yCoord;
    randPosZ = vec3.zCoord;
    return true;
  }

  @Override
  public void startExecuting() {
    entity.getNavigator().tryMoveToXYZ(randPosX, randPosY, randPosZ, speed);
  }

  @Override
  public boolean continueExecuting() {
    return !entity.getNavigator().noPath();
  }

  private boolean isOnLeaves() {
    IBlockState bs = entity.worldObj.getBlockState(entity.getPosition().down());
    Block block = bs.getBlock();
    return block.getMaterial(bs) == Material.LEAVES;
  }
}
