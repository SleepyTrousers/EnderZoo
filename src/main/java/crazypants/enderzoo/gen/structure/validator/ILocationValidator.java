package crazypants.enderzoo.gen.structure.validator;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureGenerator;

public interface ILocationValidator {

  boolean isValidChunk(StructureGenerator template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ);

  boolean isValidLocation(Structure structure, WorldStructures existingStructures, World world, Random random, int chunkX, int chunkZ);

}
