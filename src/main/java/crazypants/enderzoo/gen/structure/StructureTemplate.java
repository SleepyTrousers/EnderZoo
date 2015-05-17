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

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.ChunkBounds;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.structure.Structure.Rotation;
import crazypants.enderzoo.vec.Point3i;

public class StructureTemplate {

  private final AxisAlignedBB bb;

  private final Point3i size;

  private final Map<StructureBlock, List<Point3i>> blocks = new HashMap<StructureBlock, List<Point3i>>();

  private final String uid;
  private final int surfaceOffset;

  private final StructureBlock fillerBlock;
  private final StructureBlock topBlock;

  public StructureTemplate(String uid, IBlockAccess world, AxisAlignedBB worldBnds, int surfaceOffset) {

    this.uid = uid;

    bb = worldBnds.getOffsetBoundingBox(-worldBnds.minX, -worldBnds.minY, -worldBnds.minZ);

    size = new Point3i((int) Math.abs(worldBnds.maxX - worldBnds.minX), (int) Math.abs(worldBnds.maxY - worldBnds.minY), (int) Math.abs(worldBnds.maxZ
        - worldBnds.minZ));

    this.surfaceOffset = surfaceOffset;

    boolean markBiomeFillerForMerge = true;
    StructureBlock fb = null;
    StructureBlock sufb = null;
    if(markBiomeFillerForMerge) {
      BiomeGenBase biome = world.getBiomeGenForCoords((int) worldBnds.minX, (int) worldBnds.minZ);
      if(biome != null) {
        if(biome.topBlock != null) {
          sufb = new StructureBlock(biome.fillerBlock);
        }
        if(biome.fillerBlock != null) {
          fb = new StructureBlock(biome.fillerBlock);
          if(sufb == null) {
            sufb = fb;
          }
        }
      }
    }
    fillerBlock = fb;
    topBlock = sufb;

    int x;
    int y;
    int z;
    for (short xIndex = 0; xIndex < size.x; xIndex++) {
      for (short yIndex = 0; yIndex < size.y; yIndex++) {
        for (short zIndex = 0; zIndex < size.z; zIndex++) {

          x = (int) worldBnds.minX + xIndex;
          y = (int) worldBnds.minY + yIndex;
          z = (int) worldBnds.minZ + zIndex;
          Block blk = world.getBlock(x, y, z);
          StructureBlock sb = new StructureBlock(blk, world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z));
          if(!sb.isAir()) {
            addBlock(sb, xIndex, yIndex, zIndex);
          }
        }
      }
    }

  }

  public StructureTemplate(InputStream is) throws IOException {
    this(CompressedStreamTools.read(new DataInputStream(is)));
  }

  public StructureTemplate(NBTTagCompound root) throws IOException {
    uid = root.getString("uid");

    NBTTagList dataList = (NBTTagList) root.getTag("data");
    for (int i = 0; i < dataList.tagCount(); i++) {
      NBTTagCompound entryTag = dataList.getCompoundTagAt(i);
      NBTTagCompound blockTag = entryTag.getCompoundTag("block");
      StructureBlock sb = new StructureBlock(blockTag);

      List<Point3i> coords = new ArrayList<Point3i>();
      if(entryTag.getTag("coords") instanceof NBTTagIntArray) { //TODO
        int[] coordArr = entryTag.getIntArray("coords");
        for (int j = 0; j < coordArr.length; j += 3) {
          coords.add(new Point3i(coordArr[j], coordArr[j + 1], coordArr[j + 2]));
        }
      } else {
        //TODO: old
        NBTTagList coordList = (NBTTagList) entryTag.getTag("coords");
        for (int j = 0; j < coordList.tagCount(); j++) {
          NBTTagCompound coordTag = coordList.getCompoundTagAt(j);
          coords.add(new Point3i(coordTag.getShort("x"), coordTag.getShort("y"), coordTag.getShort("z")));
        }
      }

      blocks.put(sb, coords);

    }

    bb = AxisAlignedBB.getBoundingBox(root.getInteger("minX"), root.getInteger("minY"), root.getInteger("minZ"),
        root.getInteger("maxX"), root.getInteger("maxY"), root.getInteger("maxZ"));

    size = new Point3i((int) Math.abs(bb.maxX - bb.minX), (int) Math.abs(bb.maxY - bb.minY), (int) Math.abs(bb.maxZ
        - bb.minZ));

    surfaceOffset = root.getInteger("surfaceOffset");
    if(root.hasKey("fillerBlock")) {
      fillerBlock = new StructureBlock(root.getCompoundTag("fillerBlock"));
      //enable == comparison when building
      if(blocks.containsKey(fillerBlock)) {
        blocks.put(fillerBlock, blocks.get(fillerBlock));
      }
    } else {
      fillerBlock = null;
    }
    if(root.hasKey("topBlock")) {
      topBlock = new StructureBlock(root.getCompoundTag("topBlock"));
      //enable == comparison when building
      if(blocks.containsKey(topBlock)) {
        blocks.put(topBlock, blocks.get(topBlock));
      }
    } else {
      topBlock = null;
    }

    if(uid == null || bb == null || blocks.isEmpty()) {
      throw new IOException("Invalid NBT");
    }
  }

  public void writeToNBT(NBTTagCompound root) {

    root.setString("uid", uid);

    root.setInteger("minX", (int) bb.minX);
    root.setInteger("minY", (int) bb.minY);
    root.setInteger("minZ", (int) bb.minZ);
    root.setInteger("maxX", (int) bb.maxX);
    root.setInteger("maxY", (int) bb.maxY);
    root.setInteger("maxZ", (int) bb.maxZ);
    root.setInteger("surfaceOffset", surfaceOffset);

    if(fillerBlock != null) {
      root.setTag("fillerBlock", fillerBlock.asNbt());
    }
    if(topBlock != null) {
      root.setTag("topBlock", topBlock.asNbt());
    }

    NBTTagList entryList = new NBTTagList();
    root.setTag("data", entryList);

    for (Entry<StructureBlock, List<Point3i>> entry : blocks.entrySet()) {

      NBTTagCompound entryTag = new NBTTagCompound();
      entryTag.setTag("block", entry.getKey().asNbt());

      int i = 0;
      int[] coords = new int[entry.getValue().size() * 3];
      for (Point3i coord : entry.getValue()) {
        coords[i++] = coord.x;
        coords[i++] = coord.y;
        coords[i++] = coord.z;
      }
      entryTag.setIntArray("coords", coords);

      //      NBTTagList coordList = new NBTTagList();
      //      for (Point3i coord : entry.getValue()) {
      //        NBTTagCompound coordTag = new NBTTagCompound();
      //        coordTag.setShort("x", (short) coord.x);
      //        coordTag.setShort("y", (short) coord.y);
      //        coordTag.setShort("z", (short) coord.z);
      //        coordList.appendTag(coordTag);
      //      }
      //entryTag.setTag("coords", coordList);

      entryList.appendTag(entryTag);
    }

  }

  public void write(OutputStream os) throws IOException {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    CompressedStreamTools.write(root, new DataOutputStream(os));
  }

  public String getUid() {
    return uid;
  }

  public int getSurfaceOffset() {
    return surfaceOffset;
  }

  public void build(World world, int x, int y, int z, Rotation rot, ChunkBounds genBounds) {

    if(rot == null) {
      rot = Rotation.DEG_0;
    }

    Block fillBlk = null;
    Block surfBlk = null;
    BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
    if(biome != null) {
      fillBlk = biome.fillerBlock;
      surfBlk = biome.topBlock;
    }

    Map<StructureBlock, List<Point3i>> blks = getBlocks();
    for (Entry<StructureBlock, List<Point3i>> entry : blks.entrySet()) {

      StructureBlock sb = entry.getKey();
      List<Point3i> coords = entry.getValue();
      if(fillBlk != null && sb == fillerBlock) {
        fillBlocks(world, x, y, z, rot, genBounds, coords, fillBlk);
      } else if(surfBlk != null && sb == topBlock) {
        fillBlocks(world, x, y, z, rot, genBounds, coords, surfBlk);
      } else {
        placeBlocks(world, x, y, z, rot, genBounds, sb, coords);
      }
    }
  }

 

  private void fillBlocks(World world, int x, int y, int z, Rotation rot, ChunkBounds genBounds, List<Point3i> coords, Block filler) {
    for (Point3i coord : coords) {
      Point3i bc = transformToWorld(x, y, z, rot, coord);
      //if( (genBounds == null || genBounds.isBlockInBounds(bc.x, bc.z)) && world.isAirBlock(bc.x, bc.y, bc.z)) {
      if((genBounds == null || genBounds.isBlockInBounds(bc.x, bc.z)) && StructureUtil.isIgnoredAsSurface(world, x, z, y, world.getBlock(x, y, z), true, false)) {
        world.setBlock(bc.x, bc.y, bc.z, filler, 0, 2);
      }
    }
    return;
  }

  private void placeBlocks(World world, int x, int y, int z, Rotation rot, ChunkBounds genBounds, StructureBlock sb, List<Point3i> coords) {
    Block block = GameRegistry.findBlock(sb.getModId(), sb.getBlockName());
    if(block == null) {
      Log.error("Could not find block " + sb.getModId() + ":" + sb.getBlockName() + " when generating structure: " + uid);
    } else {
      for (Point3i coord : coords) {
        Point3i bc = transformToWorld(x, y, z, rot, coord);
        if(genBounds == null || genBounds.isBlockInBounds(bc.x, bc.z)) {

          world.setBlock(bc.x, bc.y, bc.z, block, sb.getMetaData(), 2);

          if(sb.getTileEntity() != null) {
            TileEntity te = TileEntity.createAndLoadEntity(sb.getTileEntity());
            if(te != null) {
              te.xCoord = bc.x;
              te.yCoord = bc.y;
              te.zCoord = bc.z;
              world.setTileEntity(bc.x, bc.y, bc.z, te);
            }
          }
          //Chest will change the meta on block placed, so need to set it back
          if(world.getBlockMetadata(bc.x, bc.y, bc.z) != sb.getMetaData()) {
            world.setBlockMetadataWithNotify(bc.x, bc.y, bc.z, sb.getMetaData(), 3);
          }
          for (int i = 0; i < rot.ordinal(); i++) {
            block.rotateBlock(world, bc.x, bc.y, bc.z, ForgeDirection.UP);
          }
        }
      }
    }
  }

  public Point3i transformToWorld(int x, int y, int z, Rotation rot, Point3i coord) {
    Point3i bc = new Point3i(coord);
    rot.rotate(bc, size.x - 1, size.z - 1);
    bc.add(x, y, z);
    return bc;
  }

  public AxisAlignedBB getBounds() {
    return bb;
  }

  public Point3i getSize() {
    return size;
  }

  private Map<StructureBlock, List<Point3i>> getBlocks() {
    return blocks;
  }

  private void addBlock(StructureBlock block, short x, short y, short z) {
    if(!blocks.containsKey(block)) {
      blocks.put(block, new ArrayList<Point3i>());
    }
    blocks.get(block).add(new Point3i(x, y, z));
  }

  @Override
  public String toString() {
    return "StructureTemplate [uid=" + uid + "]";
  }

}
