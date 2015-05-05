package crazypants.enderzoo.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fluids.FluidRegistry;
import crazypants.enderzoo.gen.io.StructureResourceManager;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class StructureUtil {

  public static final Random RND = new Random();

  public static boolean isPlant(Block block, World world, int x, int y, int z) {
    return block instanceof IShearable || block instanceof IPlantable || block.isLeaves(world, x, y, z)
        || block.isWood(world, x, y, z);
  }

  /**
   * If true, this block should be ignored (treated as 'air') when determining
   * the surface height.
   */
  public static boolean isIgnoredAsSurface(World world, int x, int z, int y, Block blk, boolean ignorePlants, boolean ignoreFluids) {
    //the first one will get a lot of hits, so it gets its own check
    return blk == Blocks.air || blk == Blocks.snow_layer || blk == Blocks.web || blk.isAir(world, x, y, z) ||
        (ignorePlants && StructureUtil.isPlant(blk, world, x, y, z) ||
        (ignoreFluids && FluidRegistry.lookupFluidForBlock(blk) != null));
  }

  public static Block getSurfaceBlock(World world, int x, int z, Point3i blockLocationResult, boolean ignorePlants, boolean ignoreFluids) {
    return getSurfaceBlock(world, x, z, 0, 256, blockLocationResult, ignorePlants, ignoreFluids);
  }

  public static Block getSurfaceBlock(World world, int x, int z, int minY, int maxY, Point3i blockLocationResult, boolean ignorePlants, boolean ignoreFluids) {

    //Find the surface y
    Block blk;

    boolean foundAir = false;
    int y = maxY;
    blk = world.getBlock(x, y, z);
    while (StructureUtil.isIgnoredAsSurface(world, x, z, y, blk, ignorePlants, ignoreFluids)) {
      --y;
      if(y < minY) {
        return null;
      }
      blk = world.getBlock(x, y, z);
    }

    if(blk == null) {
      return null;
    }
    
    if(y == maxY && !StructureUtil.isIgnoredAsSurface(world, x, z, y + 1, blk, ignorePlants, ignoreFluids)) {
      //found a solid block in the first sample, so need to check if it has 'air/ignored' block above it
      return null;
    }

    if(blockLocationResult != null) {
      blockLocationResult.set(x, y, z);
    }
    return blk;
  }

}
