package crazypants.enderzoo.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureBlock;
import crazypants.enderzoo.gen.structure.StructureData;
import crazypants.enderzoo.vec.Point3i;

public class StructureUtil {

  public static final Random random = new Random();

  public static boolean isPlant(Block block, World world, int x, int y, int z) {
    return block instanceof IShearable || block instanceof IPlantable || block.isLeaves(world, x, y, z)
        || block.isWood(world, x, y, z);
  }

  public static void buildStructure(Structure s, World world) {
    buildStructure(s, world, null);
  }

  public static void buildStructure(Structure s, World world, int chunkX, int chunkZ) {
    ChunkBounds genBounds = new ChunkBounds(chunkX, chunkZ, chunkX, chunkZ);
    buildStructure(s, world, genBounds);
  }

  public static void buildStructure(Structure s, World world, ChunkBounds genBounds) {
    Map<StructureBlock, List<Point3i>> blks = s.getData().getBlocks();
    for (Entry<StructureBlock, List<Point3i>> entry : blks.entrySet()) {

      StructureBlock sb = entry.getKey();
      List<Point3i> coords = entry.getValue();

      Block block = GameRegistry.findBlock(sb.getModId(), sb.getBlockName());

      if(block == null) {
        Log.error("Could not find block " + sb.getModId() + ":" + sb.getBlockName() + " when generating structure: " + s.getTemplate().getUid());
      } else {
        for (Point3i coord : coords) {
          Point3i bc = new Point3i(s.getOrigin().x + coord.x, s.getOrigin().y + coord.y, s.getOrigin().z + coord.z);
          if(genBounds == null || genBounds.isBlockInBounds(bc.x, bc.z)) {
            world.setBlock(bc.x, bc.y, bc.z, block, sb.getMetaData(), 2);

            if(sb.getTileEntity() != null) {
              TileEntity te = TileEntity.createAndLoadEntity(sb.getTileEntity());
              if(te != null) {
                world.setTileEntity(bc.x, bc.y, bc.z, te);
              }
            }
            //Chest will change the meta on block placed, so need to set it back
            if(world.getBlockMetadata(bc.x, bc.y, bc.z) != sb.getMetaData()) {
              world.setBlockMetadataWithNotify(bc.x, bc.y, bc.z, sb.getMetaData(), 3);
            }
          }
        }

      }

    }
  }

  public static void writeToFile(EntityPlayer entityPlayer, StructureData st) {
    File f = new File("ezStructures");
    f.mkdir();
    if(!f.exists()) {
      entityPlayer.addChatComponentMessage(new ChatComponentText("Could not make folder " + f.getAbsolutePath()));
      return;
    }
    f = new File(f, st.getName() + ".nbt");
    try {
      st.write(new FileOutputStream(f, false));
      entityPlayer.addChatComponentMessage(new ChatComponentText("Saved to " + f.getAbsolutePath()));
    } catch (Exception e) {
      e.printStackTrace();
      entityPlayer.addChatComponentMessage(new ChatComponentText("Could not save to " + f.getAbsolutePath()));
    }

  }

  public static StructureData readFromFile() {
    try {
      File f = new File("ezStructures", "test.nbt");
      return new StructureData(new FileInputStream(f));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
