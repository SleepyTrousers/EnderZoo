package crazypants.enderzoo.entity;

import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class SpawnUtil {

  public static boolean findClearGround(World world, BlockCoord startingLocation, BlockCoord clearLocation) {
    return findClearGround(world, startingLocation, clearLocation, 2, 10, false);
  }
  
  public static boolean findClearGround(World world, BlockCoord startingLocation, BlockCoord clearLocation, int horizRange, int vertRange,
      boolean checkForLivingEntities) {
    //first find some air in the y
    boolean foundTargetSpace = false;
    for (int xOff = -horizRange; xOff <= horizRange && !foundTargetSpace; xOff++) {
      clearLocation = clearLocation.withX(startingLocation.x + xOff);
      for (int zOff = -horizRange; zOff <= horizRange && !foundTargetSpace; zOff++) {
        clearLocation = clearLocation.withZ(startingLocation.z + zOff);
        foundTargetSpace = SpawnUtil.seachYForClearGround(clearLocation, world, vertRange, checkForLivingEntities);
        if(!foundTargetSpace) {
          clearLocation = clearLocation.withY(startingLocation.y);
        }
      }
    }
    return foundTargetSpace;
  }
  
  public static boolean seachYForClearGround(BlockCoord target, World world) {
    return seachYForClearGround(target, world, 10, false);
  }
  
  public static boolean seachYForClearGround(BlockCoord target, World world, int searchRange, boolean checkForLivingEntities) {
    boolean foundY = false;
    for (int i = 0; i < searchRange && !foundY; i++) {
      if(world.isAirBlock(target.x, target.y, target.z)) {
        foundY = true;
      } else {
        target = target.withY(target.y + 1);
      }
    }
    boolean onGround = false;
    if(foundY) {
      for (int i = 0; i < searchRange && !onGround; i++) {
        onGround = !world.isAirBlock(target.x, target.y - 1, target.z) && !isLiquid(world, target.x, target.y - 1, target.z);
        if(!onGround) {
          target = target.withY(target.y - 1);
        } else if(checkForLivingEntities && containsLiving(world, target)) {
          return false;
        }
      }
    }
    return foundY && onGround;
  }

  public static boolean containsLiving(World world, BlockCoord blockCoord) {
    AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(blockCoord.x, blockCoord.y, blockCoord.z, blockCoord.x + 1, blockCoord.y + 1, blockCoord.z + 1);
    List ents = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
    return ents != null && !ents.isEmpty();
  }

  public static boolean isLiquid(World world, int x, int y, int z) {
    Block block = world.getBlock(x, y, z);
    if(block.getMaterial().isLiquid()) {
      return true;
    }
    return false;
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

  public static boolean isSpaceAvailableForSpawn(World worldObj, EntityCreature entity, boolean checkEntityCollisions) {
    return isSpaceAvailableForSpawn(worldObj, entity, checkEntityCollisions, false);
  }

  
}
