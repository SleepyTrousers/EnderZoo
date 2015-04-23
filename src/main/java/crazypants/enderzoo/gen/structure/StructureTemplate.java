package crazypants.enderzoo.gen.structure;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.vec.Point3i;

public class StructureTemplate {

  private final StructureData data;
  private final String uid;

  private boolean canSpanChunks = true;

  public StructureTemplate(StructureData data) {
    this.data = data;
    uid = data.getName();
  }

  public AxisAlignedBB getBounds() {
    return data.getBounds();
  }

  public Collection<Structure> generate(WorldStructures structures, Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
      IChunkProvider chunkProvider) {

    if(canSpanChunks) { //Generate any bits that where started in a different
      generateExisting(structures, chunkX, chunkZ, world);
    }

    if(chunkX % 4 != 0 || chunkZ % 4 != 0) {
      return Collections.emptyList();
    }

    Point3i origin = StructureUtil.getRandomSurfaceBlock(world, chunkX, chunkZ);
    if(origin == null) {
      return Collections.emptyList();
    }
    Structure s = new Structure(this, origin);

    if(s.isChunkBoundaryCrossed()) {
      //Only build in the chunk
      //      System.out.println("StructureTemplate.generateExisting: Added new multichunk structure");
      StructureUtil.buildStructure(data, world, origin.x, origin.y, origin.z, chunkX, chunkZ);
      //and already created ones      
      Collection<ChunkCoordIntPair> chunks = s.getChunkBounds().getChunks();
      for (ChunkCoordIntPair c : chunks) {
        if(!(c.chunkXPos == chunkX && c.chunkZPos == chunkZ) && chunkGenerator.chunkExists(c.chunkXPos, c.chunkZPos)) {
          StructureUtil.buildStructure(data, world, origin.x, origin.y, origin.z, c.chunkXPos, c.chunkZPos);
          //          System.out.println("StructureTemplate.generateExisting: build structure onto existng chunk");
        }
      }

    } else {
      //      System.out.println("StructureTemplate.generateExisting: Added new structure");
      StructureUtil.buildStructure(data, world, origin.x, origin.y, origin.z);
    }
    return Collections.singletonList(s);
  }

  protected boolean generateExisting(WorldStructures structures, int chunkX, int chunkZ, World world) {
    AxisAlignedBB bnds = data.getBounds();
    int xSize = (int) Math.abs(bnds.maxX - bnds.minX);
    int zSize = (int) Math.abs(bnds.maxZ - bnds.minZ);
    //convert to num chunks
    xSize /= 16;
    zSize /= 16;
    xSize = Math.max(1, xSize);
    zSize = Math.max(1, zSize);

    Collection<Structure> existing = structures.getStructures(chunkX - xSize, chunkZ - zSize, chunkX + xSize, chunkZ + zSize, uid);

    for (Structure s : existing) {
      //      System.out.println("StructureTemplate.generateExisting: added part of existing structure");
      Point3i origin = s.getOrigin();
      StructureUtil.buildStructure(data, world, origin.x, origin.y, origin.z, chunkX, chunkZ);
    }
    return !existing.isEmpty();

  }

  private int getLongestXzEdge(AxisAlignedBB bnds) {
    return (int) Math.max(Math.abs(bnds.maxX - bnds.minX), Math.abs(bnds.maxZ - bnds.minZ));
  }

  public String getUid() {
    return uid;
  }

  public boolean canSpanChunks() {
    return canSpanChunks;
  }

  @Override
  public String toString() {
    return "StructureTemplate [uid=" + uid + "]";
  }



}
