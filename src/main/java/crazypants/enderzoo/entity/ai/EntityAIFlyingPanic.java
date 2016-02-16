package crazypants.enderzoo.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIFlyingPanic extends EntityAIBase {

  private EntityCreature theEntityCreature;
  protected double speed;
  private double randPosX;
  private double randPosY;
  private double randPosZ;

  public EntityAIFlyingPanic(EntityCreature creature, double speedIn) {
    theEntityCreature = creature;
    speed = speedIn;
    setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {
    if (theEntityCreature.getAITarget() == null && !theEntityCreature.isBurning()) {
      return false;
    }
    Vec3 vec3 = RandomPositionGenerator.findRandomTarget(theEntityCreature, 5, 4);
    if (vec3 == null) {
      return false;
    }
    double yOffset = 1 + theEntityCreature.worldObj.rand.nextInt(3);
    //double yOffset = 0;
    randPosX = vec3.xCoord;
    randPosY = vec3.yCoord + yOffset;
    randPosZ = vec3.zCoord;
    return true;
  }

  @Override
  public void startExecuting() {
    theEntityCreature.getNavigator().tryMoveToXYZ(randPosX, randPosY, randPosZ, speed);
  }

  @Override
  public boolean continueExecuting() {
    return !theEntityCreature.getNavigator().noPath();
  }

}
