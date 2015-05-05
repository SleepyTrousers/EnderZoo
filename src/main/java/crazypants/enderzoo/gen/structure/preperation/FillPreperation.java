package crazypants.enderzoo.gen.structure.preperation;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderzoo.gen.ChunkBounds;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Border;
import crazypants.enderzoo.gen.structure.Structure;

public class FillPreperation implements ISitePreperation {

  private Block fillBlock;
  private int fillMeta = 0;

  private Block surfaceBlock;
  private int surfaceMeta = 0;

  private int yOffset = -1;
  private boolean useBiomeFillerBlock = true;
  private boolean clearPlants = true;

  private Border border = new Border();

  public FillPreperation() {
    border.setBorderXZ(1);
  }

  @Override
  public boolean prepareLocation(Structure structure, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    Block fill = fillBlock;
    Block surf = surfaceBlock;
    if(useBiomeFillerBlock) {
      fill = world.getBiomeGenForCoords(structure.getOrigin().x, structure.getOrigin().z).fillerBlock;
      surf = world.getBiomeGenForCoords(structure.getOrigin().x, structure.getOrigin().z).topBlock;
    }
    if(fill == null) {
      fill = Blocks.cobblestone;
    }
    if(surf == null) {
      surf = fill;
    }

//        fill = Blocks.glass;
//        surf = Blocks.glass;
    //    surfaceMeta = 4;

    ChunkBounds clip = new ChunkBounds(chunkX, chunkZ);

    AxisAlignedBB bb = structure.getBounds();

    int minX = (int) bb.minX;
    int maxX = (int) bb.maxX;
    int minZ = (int) bb.minZ;
    int maxZ = (int) bb.maxZ;

    
    int minY = 0;
//    int maxY = (int) bb.minY + structure.getTemplate().getSurfaceOffset();
    int maxY = (int) bb.minY;

    Block curBlk;
    int curMeta;
    for (int x = minX - border.get(ForgeDirection.WEST); x < maxX + border.get(ForgeDirection.EAST); x++) {
      for (int z = minZ - border.get(ForgeDirection.NORTH); z < maxZ + border.get(ForgeDirection.SOUTH); z++) {

        int startY = maxY;
        if(x < minX || x >= maxX || z < minZ || z >= maxZ) {
          //border, so need to make it back to ground level 
          startY = maxY + structure.getTemplate().getSurfaceOffset();
        }
        for (int y = startY; y > minY; y--) {
          if(clip.isBlockInBounds(x, z)) {
            if(StructureUtil.isIgnoredAsSurface(world, x, z, y, world.getBlock(x, y, z), true, true)) {
              if(y >= maxY && world.isAirBlock(x, y + 1, z)) {
                curBlk = surf;
                curMeta = surfaceMeta;
              } else {
                curBlk = fill;
                curMeta = fillMeta;
              }
              world.setBlock(x, y, z, curBlk, curMeta, 2);
            } else {
              y = 0; //done for the x,z
            }
          }
        }
      }
    }
    return true;
  }

  public boolean isClearPlants() {
    return clearPlants;
  }

  public void setClearPlants(boolean clearPlants) {
    this.clearPlants = clearPlants;
  }

  public Border getBorder() {
    return border;
  }

  public void setBorder(Border border) {
    this.border = border;
  }

}
