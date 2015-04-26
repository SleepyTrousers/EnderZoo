package crazypants.enderzoo.gen.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import crazypants.enderzoo.gen.BoundingCircle;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;
import crazypants.enderzoo.vec.Vector2d;

public class SpacingValidator implements ILocationValidator {

  private static final double CHUNK_RADIUS = new Vector2d().distance(new Vector2d(8, 8));
  
  private final int minSpacing;
  private final StructureTemplate templateType;
  private final boolean checkChunkDistance;
  private final boolean checkPointDistance;

  public SpacingValidator(int minSpacing, StructureTemplate templateType) {
    this(minSpacing, templateType, minSpacing >= 32, minSpacing < 32);
  }

  public SpacingValidator(int minSpacing, StructureTemplate templateType, boolean checkChunkDistance, boolean checkPointDistance) {
    this.minSpacing = minSpacing;
    this.templateType = templateType;
    this.checkChunkDistance = checkChunkDistance;
    this.checkPointDistance = checkPointDistance;

  }

  @Override
  public boolean isValidChunk(StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {

    if(!checkChunkDistance) {
      return true;
    }

    ChunkCoordIntPair cc = new ChunkCoordIntPair(chunkX, chunkZ);
    BoundingCircle bc = new BoundingCircle(cc.getCenterXPos(), cc.getCenterZPosition(), (int) (CHUNK_RADIUS + minSpacing));
    List<Structure> res = new ArrayList<Structure>();

    for (ChunkCoordIntPair chunk : bc.getChunks()) {
      structures.getStructuresIntersectingChunk(chunk, templateType, res);
      if(!res.isEmpty()) {
        return false;
      }
    }
    
    return true;
  }

  @Override
  public boolean isValidLocation(Point3i loc, StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {

    if(!checkPointDistance) {
      return true;
    }

    BoundingCircle bc = new BoundingCircle(loc.x, loc.z, (int) (template.getBoundingRadius() + minSpacing));
    List<Structure> res = new ArrayList<Structure>();
    Collection<ChunkCoordIntPair> chunks = bc.getChunks();
    for (ChunkCoordIntPair chunk : chunks) {
      structures.getStructuresIntersectingChunk(chunk, template, res);
      if(!res.isEmpty()) {
        for (Structure s : res) {
          if(s.getOrigin().distance(loc) - s.getTemplate().getBoundingRadius() - template.getBoundingRadius() < minSpacing) {
            return false;
          }
        }
      }
    }
    return true;
  }

}
