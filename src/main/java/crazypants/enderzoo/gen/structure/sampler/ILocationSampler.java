package crazypants.enderzoo.gen.structure.sampler;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureGenerator;
import crazypants.enderzoo.vec.Point3i;

public interface ILocationSampler {

  Point3i generateCandidateLocation(Structure structure, WorldStructures structures, World world,
      Random random, int chunkX, int chunkZ);
}
