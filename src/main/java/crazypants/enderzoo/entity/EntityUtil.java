package crazypants.enderzoo.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import crazypants.enderzoo.vec.Point3i;

public class EntityUtil {

  public static boolean isHardDifficulty(World worldObj) {
    return worldObj.difficultySetting == EnumDifficulty.HARD;
  }

  public static float getDifficultyMultiplierForLocation(World world, double x, double y, double z) {
    //Value between 0 and 1 (normal) - 1.5 based on how long a chunk has been occupied
    float occupiedDiffcultyMultiplier = world.func_147462_b(x, y, z);
    occupiedDiffcultyMultiplier /= 1.5f; // normalize
    return occupiedDiffcultyMultiplier;
  }

  public static String getDisplayNameForEntity(String mobName) {
    return StatCollector.translateToLocal("entity." + mobName + ".name");
  }

  public static Vec3 getEntityPosition(Entity entity) {
    return Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
  }

  public static AxisAlignedBB getBoundsAround(Entity entity, double range) {
    return getBoundsAround(entity.posX, entity.posY, entity.posZ, range);
  }

  public static AxisAlignedBB getBoundsAround(Vec3 pos, double range) {
    return getBoundsAround(pos.xCoord, pos.yCoord, pos.zCoord, range);
  }

  public static AxisAlignedBB getBoundsAround(double x, double y, double z, double range) {
    return AxisAlignedBB.getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range);
  }

  public static Point3i getEntityPositionI(Entity entity) {
    return new Point3i((int) entity.posX, (int) entity.posY, (int) entity.posZ);
  }

  public static void cancelCurrentTasks(EntityLiving ent) {
    Iterator<?> iterator = ent.tasks.taskEntries.iterator();

    List<EntityAITasks.EntityAITaskEntry> currentTasks = new ArrayList<EntityAITasks.EntityAITaskEntry>();
    while (iterator.hasNext()) {
      EntityAITaskEntry entityaitaskentry = (EntityAITasks.EntityAITaskEntry) iterator.next();
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

  @SuppressWarnings("rawtypes")
  public static boolean isPlayerWithinRange(Entity entity, double range) {
    List res = entity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBoundsAround(entity, range));
    return res != null && !res.isEmpty();
  }

}
