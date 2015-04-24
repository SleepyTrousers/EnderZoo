package crazypants.enderzoo.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import crazypants.enderzoo.gen.ChunkBounds;
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
    return template.canSpanChunks() && getChunkBounds().getNumChunks() > 1;
  }

  public StructureData getData() {
    return template.getData();
  }

}
