package crazypants.enderzoo.gen;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class StructureTemplate {

  private int xSize;
  private int ySize;
  private int zSize;

  private BlockInfo[][][] data;

  public StructureTemplate(IBlockAccess ba, int x, int y, int z, int xSize, int ySize, int zSize) {
    this.xSize = xSize;
    this.ySize = ySize;
    this.zSize = zSize;

    data = new BlockInfo[xSize][ySize][zSize];

    for (int xCoord = 0; xCoord < xSize; xCoord++) {
      for (int yCoord = 0; yCoord < ySize; yCoord++) {
        for (int zCoord = 0; zCoord < zSize; zCoord++) {
          data[xCoord][yCoord][zCoord] = new BlockInfo(ba, xCoord + x, yCoord + y, zCoord + z);
        }
      }
    }

  }

  public StructureTemplate(InputStream is) throws IOException {
    NBTTagCompound root = CompressedStreamTools.readCompressed(is);

    xSize = root.getInteger("xSize");
    ySize = root.getInteger("ySize");
    zSize = root.getInteger("zSize");

    data = new BlockInfo[xSize][ySize][zSize];

    NBTTagList dataList = (NBTTagList) root.getTag("data");
    int index = 0;
    for (int xCoord = 0; xCoord < xSize; xCoord++) {
      for (int yCoord = 0; yCoord < ySize; yCoord++) {
        for (int zCoord = 0; zCoord < zSize; zCoord++) {
          NBTTagCompound dataTag = dataList.getCompoundTagAt(index);
          data[xCoord][yCoord][zCoord] = new BlockInfo(dataTag);
          index++;
        }
      }
    }

  }

  public void write(OutputStream os) throws IOException {
    NBTTagCompound root = new NBTTagCompound();
    root.setInteger("xSize", xSize);
    root.setInteger("ySize", ySize);
    root.setInteger("zSize", zSize);

    NBTTagList dataList = new NBTTagList();
    root.setTag("data", dataList);

    for (int xCoord = 0; xCoord < xSize; xCoord++) {
      for (int yCoord = 0; yCoord < ySize; yCoord++) {
        for (int zCoord = 0; zCoord < zSize; zCoord++) {
          NBTTagCompound dataTag = data[xCoord][yCoord][zCoord].asNbt();
          dataList.appendTag(dataTag);
        }
      }
    }
    CompressedStreamTools.writeCompressed(root, os);
  }

  private static class BlockInfo {

    String modId;
    String blockName;
    short blockMeta;

    private BlockInfo(NBTTagCompound tag) {
      if(tag == null) {
        modId = "minecraft";
        blockName = "air";
        blockMeta = 0;
      } else {
        UniqueIdentifier uid = new UniqueIdentifier(tag.getString("uid"));
        modId = uid.modId;
        blockName = uid.name;
        blockMeta = tag.getShort("meta");
      }
    }

    private BlockInfo(IBlockAccess ba, int x, int y, int z) {
      Block b = ba.getBlock(x, y, z);
      if(b == null) {
        b = Blocks.air;
      }
      UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(b);
      if(uid == null) {
        modId = "minecraft";
        blockName = "air";
      } else {
        modId = uid.modId;
        blockName = uid.name;
      }
      blockMeta = (short) ba.getBlockMetadata(x, y, z);
    }

    private BlockInfo(String modId, String blockName, short blockMeta) {
      this.modId = modId;
      this.blockName = blockName;
      this.blockMeta = blockMeta;
    }

    NBTTagCompound asNbt() {
      NBTTagCompound res = new NBTTagCompound();
      res.setString("uid", modId + ":" + blockName);
      res.setShort("meta", blockMeta);
      return res;
    }

  }

}
