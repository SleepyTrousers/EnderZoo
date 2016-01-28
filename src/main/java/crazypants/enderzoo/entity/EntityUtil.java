package crazypants.enderzoo.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import crazypants.enderzoo.vec.Point3i;
import crazypants.enderzoo.vec.VecUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fluids.FluidRegistry;

public class EntityUtil {

  public static boolean isHardDifficulty(World worldObj) {
    return worldObj.getDifficulty() == EnumDifficulty.HARD;
  }

  public static float getDifficultyMultiplierForLocation(World world, double x, double y, double z) {
    //Value between 0 and 1 (normal) - 1.5 based on how long a chunk has been occupied
    float occupiedDiffcultyMultiplier = world.getDifficultyForLocation(VecUtil.bpos(x, y, z)).getClampedAdditionalDifficulty();
    occupiedDiffcultyMultiplier /= 1.5f; // normalize
    return occupiedDiffcultyMultiplier;
  }

  public static String getDisplayNameForEntity(String mobName) {
    return StatCollector.translateToLocal("entity." + mobName + ".name");
  }

  public static Vec3 getEntityPosition(Entity entity) {
	  
    return new Vec3(entity.posX, entity.posY, entity.posZ);
  }

  public static AxisAlignedBB getBoundsAround(Entity entity, double range) {
    return getBoundsAround(entity.posX, entity.posY, entity.posZ, range);
  }

  public static AxisAlignedBB getBoundsAround(Vec3 pos, double range) {
    return getBoundsAround(pos.xCoord, pos.yCoord, pos.zCoord, range);
  }

  public static AxisAlignedBB getBoundsAround(double x, double y, double z, double range) {
    return new AxisAlignedBB(
        x - range, y - range, z - range,
        x + range, y + range, z + range);
  }

  public static Point3i getEntityPositionI(Entity entity) {
    return new Point3i((int) entity.posX, (int) entity.posY, (int) entity.posZ);
  }

  public static void cancelCurrentTasks(EntityLiving ent) {
    Iterator<EntityAITaskEntry> iterator = ent.tasks.taskEntries.iterator();
    List<EntityAITasks.EntityAITaskEntry> currentTasks = new ArrayList<EntityAITasks.EntityAITaskEntry>();
    while (iterator.hasNext()) {
      EntityAITaskEntry entityaitaskentry = iterator.next();
      if(entityaitaskentry != null) {
        currentTasks.add(entityaitaskentry);
      }
    }
    //Only available way to stop current execution is to remove all current tasks, then re-add them 
    for (EntityAITaskEntry task : currentTasks) {
      ent.tasks.removeTask(task.action);
      ent.tasks.addTask(task.priority, task.action);
    }
    ent.getNavigator().clearPathEntity();
  }

  public static IAttributeInstance removeModifier(EntityLivingBase ent, IAttribute p, UUID u) {
    IAttributeInstance att = ent.getEntityAttribute(p);
    AttributeModifier curmod = att.getModifier(u);
    if(curmod != null) {
      att.removeModifier(curmod);
    }
    return att;
  }

  public static double getDistanceSqToNearestPlayer(Entity entity, double maxRange) {
    AxisAlignedBB bounds = getBoundsAround(entity, maxRange);
    EntityPlayer nearest = (EntityPlayer) entity.worldObj.findNearestEntityWithinAABB(EntityPlayer.class, bounds, entity);
    if(nearest == null) {
      return 1;
    }
    return nearest.getDistanceSqToEntity(entity);
  }

  public static boolean isPlayerWithinRange(Entity entity, double range) {
    List<EntityPlayer> res = entity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBoundsAround(entity, range));
    return res != null && !res.isEmpty();
  }
  
  public static final Random RND = new Random();

  public static boolean isPlant(Block block, World world, int x, int y, int z) {
    return block instanceof IShearable || block instanceof IPlantable || block.isLeaves(world, new BlockPos(x, y, z))
        || block.isWood(world, new BlockPos(x, y, z));
  }

  /**
   * If true, this block should be ignored (treated as 'air') when determining
   * the surface height.
   *
   * @param world
   * @param x
   * @param z
   * @param y
   * @param blk
   * @param ignorePlants
   * @param ignoreFluids
   * @return
   */
  public static boolean isIgnoredAsSurface(World world, int x, int z, int y, IBlockState bs, boolean ignorePlants, boolean ignoreFluids) {
    Block blk = bs.getBlock();
    //the first one will get a lot of hits, so it gets its own check
    return blk == Blocks.air || blk == Blocks.snow_layer || blk == Blocks.web || blk.isAir(world, new BlockPos(x, y, z)) ||
        (ignorePlants && isPlant(blk, world, x, y, z) ||
            (ignoreFluids && FluidRegistry.lookupFluidForBlock(blk) != null));
  }

  public static IBlockState getSurfaceBlock(World world, int x, int z, Point3i blockLocationResult, boolean ignorePlants, boolean ignoreFluids) {
    return getSurfaceBlock(world, x, z, 0, 256, blockLocationResult, ignorePlants, ignoreFluids);
  }

  public static IBlockState getSurfaceBlock(World world, int x, int z, int minY, int maxY, Point3i blockLocationResult, boolean ignorePlants, boolean ignoreFluids) {

    //Find the surface y
    IBlockState blk;

    int y = maxY;
    blk = world.getBlockState(new BlockPos(x, y, z));
    while (isIgnoredAsSurface(world, x, z, y, blk, ignorePlants, ignoreFluids)) {
      --y;
      if(y < minY) {
        return null;
      }
      blk = world.getBlockState(new BlockPos(x, y, z));
    }

    if(blk == null) {
      return null;
    }

    if(y == maxY && !isIgnoredAsSurface(world, x, z, y + 1, blk, ignorePlants, ignoreFluids)) {
      //found a solid block in the first sample, so need to check if it has 'air/ignored' block above it
      return null;
    }

    if(blockLocationResult != null) {
      blockLocationResult.set(x, y, z);
    }
    return blk;
  }

  public static boolean isOnGround(EntityCreature entity) {
    List<AxisAlignedBB> collides = entity.worldObj.getCollidingBoundingBoxes(entity, entity.getEntityBoundingBox().offset(0, -0.05, 0));
    return collides != null && !collides.isEmpty();     
  }

}
