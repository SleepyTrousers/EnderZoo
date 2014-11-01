package crazypants.enderzoo.entity;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityUtil {

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityCreature entity) {
    return isSpaceAvailableForSpawn(worldObj, entity, false);
  }
  
  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityCreature entity, boolean canSpawnInLiquid) {
    int i = MathHelper.floor_double(entity.posX);
    int j = MathHelper.floor_double(entity.boundingBox.minY);
    int k = MathHelper.floor_double(entity.posZ);
    
    if(entity.getBlockPathWeight(i, j, k) < 0) {
      return false;
    }
    if(!worldObj.checkNoEntityCollision(entity.boundingBox)) {
      return false;
    }
    if(!worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty()) {
      return false;
    }    
    if(!canSpawnInLiquid && worldObj.isAnyLiquid(entity.boundingBox)) {
      return false;
    }    
    return true;
  }

  public static Vector3d getEntityPosition(Entity entity) {    
    return new Vector3d(entity.posX, entity.posY, entity.posZ);
  }
  
}
