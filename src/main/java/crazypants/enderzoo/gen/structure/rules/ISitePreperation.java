package crazypants.enderzoo.gen.structure.rules;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;

public interface ISitePreperation {

  boolean prepareLocation(Structure structure, WorldStructures structures, World world, Random random, int chunkX, int chunkZ);

}
