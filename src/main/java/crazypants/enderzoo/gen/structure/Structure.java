package crazypants.enderzoo.gen.structure;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.ChunkBounds;
import crazypants.enderzoo.gen.TemplateRegister;
import crazypants.enderzoo.vec.Point3i;

public class Structure {

  private final StructureTemplate template;
  private final Point3i origin;

  public Structure(StructureTemplate template, Point3i origin) {
    this.template = template;
    this.origin = origin;
  }

  public Structure(NBTTagCompound root) {
    template = TemplateRegister.instance.getTemplate(root.getString("structure"));
    origin = new Point3i(root.getInteger("x"), root.getInteger("y"), root.getInteger("z"));
  }

  public AxisAlignedBB getBounds() {
    return template.getBounds().getOffsetBoundingBox(origin.x, origin.y, origin.z);
  }

  public StructureTemplate getTemplate() {
    return template;
  }

  public Point3i getOrigin() {
    return origin;
  }

  public ChunkCoordIntPair getChunkCoord() {
    return new ChunkCoordIntPair(origin.x >> 4, origin.z >> 4);
  }

  public ChunkBounds getChunkBounds() {
    Point3i size = template.getSize();
    return new ChunkBounds(origin.x >> 4, origin.z >> 4, (origin.x + size.x) >> 4, (origin.z + size.z) >> 4);
  }

  public void writeToNBT(NBTTagCompound root) {
    root.setInteger("x", origin.x);
    root.setInteger("y", origin.y);
    root.setInteger("z", origin.z);
    root.setString("structure", template.getUid());
  }

  public boolean isValid() {
    return template != null && origin != null;
  }

  @Override
  public String toString() {
    return "Structure [template=" + template + ", origin=" + origin + "]";
  }

  public boolean isChunkBoundaryCrossed() {
    return getChunkBounds().getNumChunks() > 1;
  }

  public StructureData getData() {
    return template.getData();
  }

  public void build(World world) {
    build(world, null);
  }

  public void build(World world, int chunkX, int chunkZ) {
    ChunkBounds genBounds = new ChunkBounds(chunkX, chunkZ, chunkX, chunkZ);
    build(world, genBounds);
  }

  public void build(World world, ChunkBounds genBounds) {
    Map<StructureBlock, List<Point3i>> blks = getData().getBlocks();
    for (Entry<StructureBlock, List<Point3i>> entry : blks.entrySet()) {

      StructureBlock sb = entry.getKey();
      List<Point3i> coords = entry.getValue();

      Block block = GameRegistry.findBlock(sb.getModId(), sb.getBlockName());

      if(block == null) {
        Log.error("Could not find block " + sb.getModId() + ":" + sb.getBlockName() + " when generating structure: " + getTemplate().getUid());
      } else {
        for (Point3i coord : coords) {
          Point3i bc = new Point3i(getOrigin().x + coord.x, getOrigin().y + coord.y, getOrigin().z + coord.z);
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

}
