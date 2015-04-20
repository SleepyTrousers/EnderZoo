package crazypants.enderzoo.gen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class TestGen implements IWorldGenerator {

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

    //    BiomeGenBase b = world.getBiomeGenForCoords(chunkX, chunkZ);
    //    if(b.biomeName.equals("Plains")) {
    //            // Then we have plains!
    //    }

    //    switch (world.provider.dimensionId) {
    //    case -1:
    //      generateNether(world, random, chunkX * 16, chunkZ * 16);
    //    case 0:
    //      generateSurface(world, random, chunkX * 16, chunkZ * 16);
    //    case 1:
    //      generateEnd(world, random, chunkX * 16, chunkZ * 16);
    //    }

    int x = chunkX * 16 + random.nextInt(16);
    int z = chunkZ * 16 + random.nextInt(16);

    int y = 200;
    while (world.isAirBlock(x, y, z) && y > 2) {
      --y;
    }

    for (int i = 0; i < 10; i++) {
      world.setBlock(x, y + i, z, Blocks.diamond_block);
    }

  }

}
