package crazypants.enderzoo.gen.structure.rules;

import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderzoo.gen.ChunkBounds;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;

public class ClearPreperation implements ISitePreperation {

  private final Border border = new Border();

  private boolean clearPlants = true;

  public ClearPreperation() {
    setBorder(1);
  }

  public void setBorder(int size) {
    border.setBorder(size, size, size, size, 3, -1);
  }

  @Override
  public boolean prepareLocation(Structure structure, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {

    ChunkBounds clip = new ChunkBounds(chunkX, chunkZ);

    AxisAlignedBB bb = structure.getBounds();
    int minX = (int) bb.minX - border.get(ForgeDirection.WEST);
    int maxX = (int) bb.maxX + border.get(ForgeDirection.EAST);
    int minY = (int) bb.minY - border.get(ForgeDirection.DOWN);
    int maxY = (int) bb.maxY + border.get(ForgeDirection.UP);
    int minZ = (int) bb.minZ - border.get(ForgeDirection.NORTH);
    int maxZ = (int) bb.maxZ + border.get(ForgeDirection.SOUTH);

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
