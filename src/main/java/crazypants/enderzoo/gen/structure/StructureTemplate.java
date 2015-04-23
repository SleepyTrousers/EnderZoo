package crazypants.enderzoo.gen.structure;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.vec.Point3i;

public class StructureTemplate {

  private final StructureData data;
  private final String uid;

  public StructureTemplate(StructureData data) {
    this.data = data;
    uid = data.getName();
  }

  public boolean canSpawnHere(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
    return chunkX % 4 == 0 && chunkZ % 4 == 0;
  }

  public Structure generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
    Point3i origin = StructureUtil.getRandomSurfaceBlock(world, chunkX, chunkZ);
    if(origin == null) {
      return null;
    }
    Structure s = new Structure(this, origin);
    StructureUtil.generateStructure(data, world, origin.x, origin.y, origin.z);
    return s;
  }

  public String getUid() {
    return uid;
  }

  @Override
  public String toString() {
    return "StructureTemplate [uid=" + uid + "]";
  }

}
