package crazypants.enderzoo.gen.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderzoo.gen.ChunkBounds;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;

public class ClearPreperation implements IBuildPreperation {

  private final Map<ForgeDirection, Integer> border = new HashMap<ForgeDirection, Integer>();

  private boolean clearPlants = true;

  public ClearPreperation() {
    setBorder(1);
  }

  public int getBorder(ForgeDirection dir) {
    Integer res = border.get(dir);
    if(res == null) {
      return 0;
    }
    return res;
  }

  public void setBorder(int size) {
    setBorder(size, size, size, size, 0, -1);
  }

  public void setBorder(int north, int south, int east, int west, int up, int down) {
    border.put(ForgeDirection.DOWN, down);
    border.put(ForgeDirection.UP, up);
    border.put(ForgeDirection.EAST, east);
    border.put(ForgeDirection.WEST, west);
    border.put(ForgeDirection.NORTH, north);
    border.put(ForgeDirection.SOUTH, south);
  }

  @Override
  public boolean prepareLocation(Structure structure, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {

    ChunkBounds clip = new ChunkBounds(chunkX, chunkZ);

    AxisAlignedBB bb = structure.getBounds();
    int minX = (int) bb.minX - getBorder(ForgeDirection.WEST);
    int maxX = (int) bb.maxX + getBorder(ForgeDirection.EAST);
    int minY = (int) bb.minY - getBorder(ForgeDirection.DOWN);
    int maxY = (int) bb.maxY + getBorder(ForgeDirection.UP);
    int minZ = (int) bb.minZ - getBorder(ForgeDirection.NORTH);
    int maxZ = (int) bb.maxZ + getBorder(ForgeDirection.SOUTH);

    for (int x = minX; x < maxX; x++) {
      for (int y = minY; y < maxY; y++) {
        for (int z = minZ; z < maxZ; z++) {
          if(clip.isBlockInBounds(x, z) && (clearPlants || !StructureUtil.isPlant(world.getBlock(x, y, z), world, x, y, z))) {
            if(!world.isAirBlock(x, y, z)) {
              world.setBlockToAir(x, y, z);
            }
          }
        }
      }
    }

    return true;
  }

}
