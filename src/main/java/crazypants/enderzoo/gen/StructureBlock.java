package crazypants.enderzoo.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import crazypants.enderzoo.Log;

public class StructureBlock {

  private final String modId;
  private final String blockName;
  private final short blockMeta;
  private final NBTTagCompound tileEntity;

  public StructureBlock(NBTTagCompound tag) {

    UniqueIdentifier uid = new UniqueIdentifier(tag.getString("uid"));
    modId = uid.modId;
    blockName = uid.name;
    blockMeta = tag.getShort("meta");
    if(tag.hasKey("te")) {
      tileEntity = tag.getCompoundTag("te");
    } else {
      tileEntity = null;
    }

  }

  public StructureBlock(IBlockAccess ba, int x, int y, int z) {

    Block b = ba.getBlock(x, y, z);
    if(b == null) {
      Log.warn("StructureBlock.StructureBlock: Null block");
      b = Blocks.air;
    }
    UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(b);
    if(uid == null) {
      modId = "minecraft";
      blockName = "air";
      Log.warn("StructureBlock.StructureBlock: Null UID for " + b);
    } else {
      modId = uid.modId;
      blockName = uid.name;
    }
    blockMeta = (short) ba.getBlockMetadata(x, y, z);

    TileEntity tile = ba.getTileEntity(x, y, z);
    if(tile != null) {
      tileEntity = new NBTTagCompound();
      tile.writeToNBT(tileEntity);
    } else {
      tileEntity = null;
    }
  }

  public boolean isAir() {
    return "minecraft".equals(getModId()) && "air".equals(getBlockName());
  }

  public NBTTagCompound asNbt() {
    NBTTagCompound res = new NBTTagCompound();
    writeToNBT(res);
    return res;
  }

  public void writeToNBT(NBTTagCompound res) {
    res.setString("uid", getModId() + ":" + getBlockName());
    res.setShort("meta", blockMeta);
    if(tileEntity != null) {
      res.setTag("te", tileEntity);
    }

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + blockMeta;
    result = prime * result + ((getBlockName() == null) ? 0 : getBlockName().hashCode());
    result = prime * result + ((getModId() == null) ? 0 : getModId().hashCode());
    result = prime * result + ((tileEntity == null) ? 0 : tileEntity.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(getClass() != obj.getClass()) {
      return false;
    }
    StructureBlock other = (StructureBlock) obj;
    if(blockMeta != other.blockMeta) {
      return false;
    }
    if(getBlockName() == null) {
      if(other.getBlockName() != null) {
        return false;
      }
    } else if(!getBlockName().equals(other.getBlockName())) {
      return false;
    }
    if(getModId() == null) {
      if(other.getModId() != null) {
        return false;
      }
    } else if(!getModId().equals(other.getModId())) {
      return false;
    }
    if(tileEntity == null) {
      if(other.tileEntity != null) {
        return false;
      }
    } else if(!tileEntity.equals(other.tileEntity)) {
      return false;
    }
    return true;
  }

  public String getModId() {
    return modId;
  }

  public String getBlockName() {
    return blockName;
  }

  public int getMetaData() {
    return blockMeta;
  }

  public NBTTagCompound getTileEntity() {
    return tileEntity;
  }

}