package crazypants.enderzoo.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.BoundingCircle;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure.Rotation;
import crazypants.enderzoo.gen.structure.preperation.ClearPreperation;
import crazypants.enderzoo.gen.structure.preperation.CompositePreperation;
import crazypants.enderzoo.gen.structure.preperation.FillPreperation;
import crazypants.enderzoo.gen.structure.preperation.ISitePreperation;
import crazypants.enderzoo.gen.structure.sampler.ILocationSampler;
import crazypants.enderzoo.gen.structure.sampler.SurfaceLocationSampler;
import crazypants.enderzoo.gen.structure.validator.CompositeValidator;
import crazypants.enderzoo.gen.structure.validator.ILocationValidator;
import crazypants.enderzoo.gen.structure.validator.LevelGroundValidator;
import crazypants.enderzoo.gen.structure.validator.RandomValidator;
import crazypants.enderzoo.gen.structure.validator.SpacingValidator;
import crazypants.enderzoo.vec.Point3i;

public class StructureGenerator {

  private static final Random rnd = new Random();

  private final StructureTemplate data;
  private final String uid;

  private final CompositeValidator validators = new CompositeValidator();
  private final CompositePreperation sitePreps = new CompositePreperation();

  private ILocationSampler locSampler;
  private boolean canSpanChunks = false;
  private int attemptsPerChunk = 5;
  //Max number of structures of this type that be generated in a single chunk
  private int maxInChunk = 1;
  private int yOffset = 0;

  public StructureGenerator(String uid, StructureTemplate data) {
    this.uid = uid;
    this.data = data;
    locSampler = new SurfaceLocationSampler();
  }

  public Structure createStructure() {
    return new Structure(this, data, new Point3i(), Rotation.DEG_0);
  }

  public Collection<Structure> generate(WorldStructures structures, Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
      IChunkProvider chunkProvider) {

    if(canSpanChunks) { //Generate any bits that where started in a different
      generateExisting(structures, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }

    //TODO: If we can span chunks, we need to check the validators against the other chunks it crosses
    //as well. If those chunks havent been created yet then maybe consider defering the checks until they are
    if(!validators.isValidChunk(this, structures, world, random, chunkX, chunkZ)) {
      return Collections.emptyList();
    }

    Structure struct = createStructure();
    List<Structure> res = new ArrayList<Structure>();
    for (int i = 0; i < attemptsPerChunk && res.size() < maxInChunk; i++) {
      Point3i origin = locSampler.generateCandidateLocation(struct, structures, world, random, chunkX, chunkZ);      
      if(origin != null) {
        struct.setOrigin(origin);
        if(validators.isValidLocation(struct, structures, world, random, chunkX, chunkZ)) {
          if(yOffset != 0) {
            Point3i o = new Point3i(struct.getOrigin());
            o.y += yOffset;
            struct.setOrigin(origin);
          }                    
          if(buildStructure(struct, structures, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider)) {
            res.add(struct);
            Log.debug("StructureTemplate.generate: Added " + struct);
          }
        }
      }

    }
    return res;
  }

  public boolean buildStructure(Structure s, WorldStructures structures, Random random, int chunkX, int chunkZ, World world,
      IChunkProvider chunkGenerator,
      IChunkProvider chunkProvider) {

    boolean res = false;
    if(s.isChunkBoundaryCrossed()) {
      //Only build in the chunk
      if(sitePreps.prepareLocation(s, structures, world, random, chunkX, chunkZ)) {
        res = true;
        s.build(world, chunkX, chunkZ);
        //and already created ones      
        Collection<ChunkCoordIntPair> chunks = s.getChunkBounds().getChunks();
        for (ChunkCoordIntPair c : chunks) {
          if(!(c.chunkXPos == chunkX && c.chunkZPos == chunkZ) && chunkGenerator.chunkExists(c.chunkXPos, c.chunkZPos)) {
            sitePreps.prepareLocation(s, structures, world, random, c.chunkXPos, c.chunkZPos);
            s.build(world, c.chunkXPos, c.chunkZPos);
          }
        }
      }

    } else {
      if(sitePreps.prepareLocation(s, structures, world, random, chunkX, chunkZ)) {
        s.build(world);
        res = true;
      }
    }
    return res;
  }

  protected boolean generateExisting(WorldStructures structures, Random random, int chunkX, int chunkZ, World world,
      IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

    Collection<Structure> existing = new ArrayList<Structure>();
    structures.getStructuresIntersectingChunk(new ChunkCoordIntPair(chunkX, chunkZ), uid, existing);

    for (Structure s : existing) {
      if(sitePreps.prepareLocation(s, structures, world, random, chunkX, chunkZ)) {
        s.build(world, chunkX, chunkZ);
      }
    }
    return !existing.isEmpty();

  }

  public boolean isValid() {
    return uid != null && !uid.trim().isEmpty() && data != null && locSampler != null;
  }

  public void addLocationValidator(ILocationValidator val) {
    if(val != null) {
      validators.add(val);
    }
  }

  public void addSitePreperation(ISitePreperation val) {
    if(val != null) {
      sitePreps.add(val);
    }
  }

  public String getUid() {
    return uid;
  }

  public boolean canSpanChunks() {
    return canSpanChunks;
  }

  public int getMaxAttemptsPerChunk() {
    return attemptsPerChunk;
  }

  public ILocationSampler getLocationSampler() {
    return locSampler;
  }

  public void setLocationSampler(ILocationSampler locSampler) {
    this.locSampler = locSampler;
  }

  public boolean isCanSpanChunks() {
    return canSpanChunks;
  }

  public void setCanSpanChunks(boolean canSpanChunks) {
    this.canSpanChunks = canSpanChunks;
  }

  public int getAttemptsPerChunk() {
    return attemptsPerChunk;
  }

  public void setAttemptsPerChunk(int attemptsPerChunk) {
    this.attemptsPerChunk = attemptsPerChunk;
  }

  public int getMaxInChunk() {
    return maxInChunk;
  }

  public void setMaxInChunk(int maxInChunk) {
    this.maxInChunk = maxInChunk;
  }

  public int getyOffset() {
    return yOffset;
  }

  public void setyOffset(int yOffset) {
    this.yOffset = yOffset;
  }

  @Override
  public String toString() {
    return "StructureTemplate [uid=" + uid + "]";
  }

}
