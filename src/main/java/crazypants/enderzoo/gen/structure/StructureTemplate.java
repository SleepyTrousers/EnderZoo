package crazypants.enderzoo.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import crazypants.enderzoo.gen.BoundingCircle;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.rules.ClearPreperation;
import crazypants.enderzoo.gen.rules.CompositeBuildPreperation;
import crazypants.enderzoo.gen.rules.CompositeBuildRule;
import crazypants.enderzoo.gen.rules.FillPreperation;
import crazypants.enderzoo.gen.rules.ILocationSampler;
import crazypants.enderzoo.gen.rules.LevelGroundRule;
import crazypants.enderzoo.gen.rules.SpacingRule;
import crazypants.enderzoo.gen.rules.SurfaceLocationSampler;
import crazypants.enderzoo.vec.Point3i;

public class StructureTemplate {

  private static final Random rnd = new Random();

  private final StructureData data;
  private final String uid;
  private final BoundingCircle bc;

  private final boolean canSpanChunks = false;


  private final ILocationSampler locSampler;
  private final CompositeBuildRule buildRules = new CompositeBuildRule();
  private final CompositeBuildPreperation buildPrep = new CompositeBuildPreperation();

  private final int attemptsPerChunk = 5;
  //Max number of structures of this type that be generated in a single chunk
  private final int maxInChunk = 1;
  private final int yOffset = 0;

  public StructureTemplate(StructureData data) {
    this.data = data;
    uid = data.getName();
    bc = new BoundingCircle(data.getBounds());

    //    buildRules.add(new ChanceRule(0.05f));
    //    buildRules.add(new SpacingRule(200, this));        

    buildRules.add(new SpacingRule(20, null));
    buildRules.add(new LevelGroundRule());

    locSampler = new SurfaceLocationSampler(0);

    buildPrep.add(new ClearPreperation());
    buildPrep.add(new FillPreperation());
  }

  public AxisAlignedBB getBounds() {
    return data.getBounds();
  }

  public double getBoundingRadius() {
    return bc.getRadius();
  }

  public BoundingCircle getBoundingCircle() {
    return bc;
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

  public Point3i getSize() {
    return data.getSize();
  }

  public StructureData getData() {
    return data;
  }

  public Collection<Structure> generate(WorldStructures structures, Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
      IChunkProvider chunkProvider) {

    if(canSpanChunks) { //Generate any bits that where started in a different
      generateExisting(structures, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }


    if(!buildRules.isValidChunk(this, structures, world, random, chunkX, chunkZ)) {
      return Collections.emptyList();
    }


    List<Structure> res = new ArrayList<Structure>();
    for (int i = 0; i < attemptsPerChunk && res.size() < maxInChunk; i++) {
      Point3i origin = locSampler.generateCandidateLocation(this, structures, world, random, chunkX, chunkZ);
      if(origin != null && buildRules.isValidLocation(origin, this, structures, world, random, chunkX, chunkZ)) {

        origin.y -= yOffset;
        Structure s = new Structure(this, origin);
        if(buildStructure(s, structures, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider)) {
          res.add(s);
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
      //      System.out.println("StructureTemplate.generateExisting: Added new multichunk structure");
      if(buildPrep.prepareLocation(s, structures, world, random, chunkX, chunkZ)) {
        res = true;
        s.build(world, chunkX, chunkZ);
        //and already created ones      
        Collection<ChunkCoordIntPair> chunks = s.getChunkBounds().getChunks();
        for (ChunkCoordIntPair c : chunks) {
          if(!(c.chunkXPos == chunkX && c.chunkZPos == chunkZ) && chunkGenerator.chunkExists(c.chunkXPos, c.chunkZPos)) {
            buildPrep.prepareLocation(s, structures, world, random, c.chunkXPos, c.chunkZPos);
            s.build(world, c.chunkXPos, c.chunkZPos);
            //          System.out.println("StructureTemplate.generateExisting: build structure onto existng chunk");
          }
        }
      }

    } else {
      //      System.out.println("StructureTemplate.generateExisting: Added new structure");
      if(buildPrep.prepareLocation(s, structures, world, random, chunkX, chunkZ)) {
        s.build(world);
        res = true;
      }
    }
    return res;
  }

  protected boolean generateExisting(WorldStructures structures, Random random, int chunkX, int chunkZ, World world,
      IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

    Collection<Structure> existing = new ArrayList<Structure>();
    structures.getStructuresIntersectingChunk(new ChunkCoordIntPair(chunkX, chunkZ), this, existing);

    for (Structure s : existing) {
      if(buildPrep.prepareLocation(s, structures, world, random, chunkX, chunkZ)) {
        s.build(world, chunkX, chunkZ);
      }
    }
    return !existing.isEmpty();

  }

  @Override
  public String toString() {
    return "StructureTemplate [uid=" + uid + "]";
  }

}
