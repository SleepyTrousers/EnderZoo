package crazypants.enderzoo.gen.rules;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;

public interface IBuildPreperation {

  boolean prepareLocation(Structure structure, WorldStructures structures, World world, Random random, int chunkX, int chunkZ);

}
