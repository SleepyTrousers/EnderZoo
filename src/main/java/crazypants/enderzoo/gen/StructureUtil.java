package crazypants.enderzoo.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.vec.Point3i;

public class StructureUtil {

  public static void generateStructure(StructureTemplate st, World world, int x, int y, int z) {
    Map<StructureBlock, List<Point3i>> blks = st.getBlocks();
    for (Entry<StructureBlock, List<Point3i>> entry : blks.entrySet()) {

      StructureBlock sb = entry.getKey();
      List<Point3i> coords = entry.getValue();

      Block block = GameRegistry.findBlock(sb.getModId(), sb.getBlockName());
      if(block == null) {
        Log.error("Could not find block " + sb.getModId() + ":" + sb.getBlockName() + " when generating structure: " + st.getName());
      } else {
        for (Point3i coord : coords) {
          world.setBlock(x + coord.x, y + coord.y, z + coord.z, block, sb.getMetaData(), 2);
          if(sb.getTileEntity() != null) {
            TileEntity te = TileEntity.createAndLoadEntity(sb.getTileEntity());
            if(te != null) {
              world.setTileEntity(x + coord.x, y + coord.y, z + coord.z, te);
            }
          }
        }

      }

    }
  }

  public static void writeToFile(EntityPlayer entityPlayer, StructureTemplate st) {
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

  public static StructureTemplate readFromFile() {
    try {
      File f = new File("ezStructures", "test.nbt");
      return new StructureTemplate(new FileInputStream(f));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
