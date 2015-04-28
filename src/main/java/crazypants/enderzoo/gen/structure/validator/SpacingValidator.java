package crazypants.enderzoo.gen.structure.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
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
  
  private int minSpacing;
  private final List<String> templateFilter = new ArrayList<String>();
  private boolean validateChunk = false;
  private boolean validateLocation = true;

  public SpacingValidator() {    
    this(20, (String[])null);
  }
  
  public SpacingValidator(int minSpacing, String... templateType) {
    this(minSpacing, minSpacing >= 32, minSpacing < 32, templateType);
  }

  public SpacingValidator(int minSpacing, boolean checkChunkDistance, boolean checkPointDistance, String... matchTypes) {
    this.minSpacing = minSpacing;    
    this.validateChunk = checkChunkDistance;
    this.validateLocation = checkPointDistance;
    if(matchTypes != null) {
      for(String tmp : matchTypes) {
        if(tmp != null) {
          templateFilter.add(tmp);
        }
      }
    }
  }
  
  @Override
  public boolean isValidChunk(StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {

    if(!validateChunk) {
      return true;
    }

    ChunkCoordIntPair cc = new ChunkCoordIntPair(chunkX, chunkZ);
    BoundingCircle bc = new BoundingCircle(cc.getCenterXPos(), cc.getCenterZPosition(), (int) (CHUNK_RADIUS + minSpacing));
    
    List<Structure> res = new ArrayList<Structure>();
    for (ChunkCoordIntPair chunk : bc.getChunks()) {
      getStructuresIntersectingChunk(chunk, structures, res); 
      if(!res.isEmpty()) {
        return false;
      }
    }
    
    return true;
  }

  @Override
  public boolean isValidLocation(Point3i loc, StructureTemplate template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {

    if(!validateLocation) {
      return true;
    }

    BoundingCircle bc = new BoundingCircle(loc.x, loc.z, (int) (template.getBoundingRadius() + minSpacing));
    List<Structure> res = new ArrayList<Structure>();
    Collection<ChunkCoordIntPair> chunks = bc.getChunks();
    for (ChunkCoordIntPair chunk : chunks) {
      getStructuresIntersectingChunk(chunk, structures, res);
      if(!res.isEmpty()) {
        for (Structure s : res) {
          if(s.getOrigin().distance(loc) - s.getTemplate().getBoundingRadius() - template.getBoundingRadius() < minSpacing) {
            return false;
          }
        }
        res.clear();
      }
    }
    return true;
  }
  
  private void getStructuresIntersectingChunk(ChunkCoordIntPair chunk, WorldStructures structures, List<Structure> res) {
    structures.getStructuresIntersectingChunk(chunk, null, res);
    if(!templateFilter.isEmpty() && !res.isEmpty()) {
      ListIterator<Structure> iter = res.listIterator();
      while(iter.hasNext()) {
        Structure match = iter.next();
        if(!templateFilter.contains(match.getTemplate().getUid())) {
          iter.remove();
        }
      }
    }
  }
  
  public int getMinSpacing() {
    return minSpacing;
  }

  public void setMinSpacing(int minSpacing) {
    this.minSpacing = minSpacing;
  }

  public boolean isValidateChunk() {
    return validateChunk;
  }

  public void setValidateChunk(boolean validateChunk) {
    this.validateChunk = validateChunk;
  }

  public boolean isValidateLocation() {
    return validateLocation;
  }

  public void setValidateLocation(boolean validateLocation) {
    this.validateLocation = validateLocation;
  }

  public List<String> getTemplateFilter() {
    return templateFilter;
  }
  
  public void setTemplateFilter(Collection<String> filter) {
    templateFilter.clear();
    templateFilter.addAll(filter);
  }

}
