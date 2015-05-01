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
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.BoundingCircle;
import crazypants.enderzoo.gen.ChunkBounds;
import crazypants.enderzoo.gen.StructureRegister;
import crazypants.enderzoo.vec.Point3i;

public class Structure {

  public static enum Rotation {
    DEG_0,
    DEG_90,
    DEG_180,
    DEG_270;   
    
    //Keeps all point +'ve
    public void rotate(Point3i bc, int maxX, int maxZ) {
      if(this == Rotation.DEG_0) {
        return;
      }
      if(this == Rotation.DEG_90) {
        bc.set(maxZ-bc.z, bc.y, bc.x);
      } else if(this == Rotation.DEG_180) {
        bc.set(maxX-bc.x, bc.y, maxZ-bc.z);
      } else if(this == Rotation.DEG_270) {
        bc.set(bc.z, bc.y, maxX-bc.x);
      }
    }
  }
  
  private final StructureGenerator generator;
  private final Point3i origin;
  private final StructureTemplate template;

  private BoundingCircle bc;
  private Rotation rotation;
  

  public Structure(StructureGenerator generator, StructureTemplate template, Point3i origin, Rotation rotation) {
    this.generator = generator;    
    this.template = template;
    this.origin = origin;
    if(rotation == null) {
      this.rotation = Rotation.DEG_0; 
    } else {
      this.rotation = rotation;
    }
  }

  public Structure(NBTTagCompound root) {
    generator = StructureRegister.instance.getConfig(root.getString("structure"));
    template = StructureRegister.instance.getStructureData(root.getString("data"));
    origin = new Point3i(root.getInteger("x"), root.getInteger("y"), root.getInteger("z"));
    rotation = Rotation.values()[root.getShort("rotation")];
  }

  public AxisAlignedBB getBounds() {
    return template.getBounds().getOffsetBoundingBox(origin.x, origin.y, origin.z);
  }

  public StructureGenerator getGenerator() {
    return generator;
  }

  public Point3i getOrigin() {
    return origin;
  }

  public void setOrigin(Point3i origin) {
    this.origin.set(origin.x, origin.y, origin.z);
  }

  public Point3i getSize() {
    return template.getSize();
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
    root.setString("generator", generator.getUid());
    root.setString("template", template.getUid());    
    root.setShort("rotation", (short)rotation.ordinal());
  }

  public boolean isValid() {
    return generator != null && origin != null && template != null;
  }

  @Override
  public String toString() {
    if(isValid()) {
      return "Structure [generator=" + generator.getUid() + ", template=" + template.getUid() + ", origin=" + origin + "]";
    } else {
      return "Structure [generator=" + generator + ", template=" + template + ", origin=" + origin + "]";
    }
  }

  public boolean isChunkBoundaryCrossed() {
    return getChunkBounds().getNumChunks() > 1;
  }

  public StructureTemplate getTemplate() {
    return template;
  }

  public void build(World world) {
    build(world, null);
  }

  public void build(World world, int chunkX, int chunkZ) {
    ChunkBounds genBounds = new ChunkBounds(chunkX, chunkZ, chunkX, chunkZ);
    build(world, genBounds);
  }

  public void build(World world, ChunkBounds genBounds) {
    template.build(world, origin.x, origin.y, origin.z, rotation, genBounds);
  }

  public double getBoundingRadius() {
    // TODO Auto-generated method stub
    return 0;
  }

}
