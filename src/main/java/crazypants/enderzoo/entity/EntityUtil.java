package crazypants.enderzoo.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityUtil {

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityCreature entity, boolean checkEntityCollisions) {
    return isSpaceAvailableForSpawn(worldObj, entity, checkEntityCollisions, false);
  }
  
  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityCreature entity, boolean checkEntityCollisions, boolean canSpawnInLiquid) {
    int i = MathHelper.floor_double(entity.posX);
    int j = MathHelper.floor_double(entity.boundingBox.minY);
    int k = MathHelper.floor_double(entity.posZ);
    
    if(entity.getBlockPathWeight(i, j, k) < 0) {
      return false;
    }
    if(checkEntityCollisions && !worldObj.checkNoEntityCollision(entity.boundingBox)) {
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

  public static Vec3 getEntityPosition(Entity entity) {    
    return Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
  }
  
  public static float getDifficultyMultiplierForLocation(World world, double x, double y, double z) {
    //Value between 0 and 1 (normal) - 1.5 based on how long a chunk has been occupied
    float occupiedDiffcultyMultiplier = world.func_147462_b(x,y,z);
    occupiedDiffcultyMultiplier /= 1.5f; // normalize
    return occupiedDiffcultyMultiplier;
  }
  
  public static String getDisplayNameForEntity(String mobName) {
    return StatCollector.translateToLocal("entity." + mobName + ".name");
  }
  
}
