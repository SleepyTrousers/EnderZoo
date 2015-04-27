package crazypants.enderzoo.gen.structure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import crazypants.enderzoo.vec.Point3i;

public class StructureData {

  private final AxisAlignedBB bb;

  private final Point3i size;

  private final Map<StructureBlock, List<Point3i>> data = new HashMap<StructureBlock, List<Point3i>>();

  private final String name;

  public StructureData(String name, IBlockAccess world, AxisAlignedBB worldBnds) {

    this.name = name;

    bb = worldBnds.getOffsetBoundingBox(-worldBnds.minX, -worldBnds.minY, -worldBnds.minZ);

    size = new Point3i((int) Math.abs(worldBnds.maxX - worldBnds.minX), (int) Math.abs(worldBnds.maxY - worldBnds.minY), (int) Math.abs(worldBnds.maxZ
        - worldBnds.minZ));

    for (short xIndex = 0; xIndex < size.x; xIndex++) {
      for (short yIndex = 0; yIndex < size.y; yIndex++) {
        for (short zIndex = 0; zIndex < size.z; zIndex++) {
          addBlock(new StructureBlock(world, (int) worldBnds.minX + xIndex, (int) worldBnds.minY + yIndex, (int) worldBnds.minZ + zIndex), xIndex, yIndex,
              zIndex);
        }
      }
    }

  }

  public StructureData(InputStream is) throws IOException {
    this(CompressedStreamTools.read(new DataInputStream(is)));
  }

  public StructureData(NBTTagCompound root) throws IOException {
    name = root.getString("name");

    NBTTagList dataList = (NBTTagList) root.getTag("data");
    for (int i = 0; i < dataList.tagCount(); i++) {
      NBTTagCompound entryTag = dataList.getCompoundTagAt(i);
      NBTTagCompound blockTag = entryTag.getCompoundTag("block");
      StructureBlock sb = new StructureBlock(blockTag);

      List<Point3i> coords = new ArrayList<Point3i>();
      NBTTagList coordList = (NBTTagList) entryTag.getTag("coords");
      for (int j = 0; j < coordList.tagCount(); j++) {
        NBTTagCompound coordTag = coordList.getCompoundTagAt(j);
        coords.add(new Point3i(coordTag.getShort("x"), coordTag.getShort("y"), coordTag.getShort("z")));
      }

      data.put(sb, coords);

    }

    bb = AxisAlignedBB.getBoundingBox(root.getInteger("minX"), root.getInteger("minY"), root.getInteger("minZ"),
        root.getInteger("maxX"), root.getInteger("maxY"), root.getInteger("maxZ"));

    size = new Point3i((int) Math.abs(bb.maxX - bb.minX), (int) Math.abs(bb.maxY - bb.minY), (int) Math.abs(bb.maxZ
        - bb.minZ));
    
    if(name == null || bb == null || data.isEmpty()) {
      throw new IOException("Invalid NBT");
    }
  }

  public void writeToNBT(NBTTagCompound root) {

    root.setString("name", name);

    root.setInteger("minX", (int) bb.minX);
    root.setInteger("minY", (int) bb.minY);
    root.setInteger("minZ", (int) bb.minZ);
    root.setInteger("maxX", (int) bb.maxX);
    root.setInteger("maxY", (int) bb.maxY);
    root.setInteger("maxZ", (int) bb.maxZ);

    NBTTagList entryList = new NBTTagList();
    root.setTag("data", entryList);

    for (Entry<StructureBlock, List<Point3i>> entry : data.entrySet()) {

      NBTTagList coordList = new NBTTagList();
      for (Point3i coord : entry.getValue()) {
        NBTTagCompound coordTag = new NBTTagCompound();
        coordTag.setShort("x", (short) coord.x);
        coordTag.setShort("y", (short) coord.y);
        coordTag.setShort("z", (short) coord.z);
        coordList.appendTag(coordTag);
      }

      NBTTagCompound entryTag = new NBTTagCompound();
      entryTag.setTag("block", entry.getKey().asNbt());
      entryTag.setTag("coords", coordList);

      entryList.appendTag(entryTag);
    }

  }

  public void write(OutputStream os) throws IOException {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    CompressedStreamTools.write(root, new DataOutputStream(os));
  }

  public String getName() {
    return name;
  }

  public AxisAlignedBB getBounds() {
    return bb;
  }

  public Map<StructureBlock, List<Point3i>> getBlocks() {
    return data;
  }

  private void addBlock(StructureBlock block, short x, short y, short z) {
    if(block.isAir()) {
      return;
    }
    if(!data.containsKey(block)) {
      data.put(block, new ArrayList<Point3i>());
    }
    data.get(block).add(new Point3i(x, y, z));
  }

  public Point3i getSize() {
    return size;
  }

}
