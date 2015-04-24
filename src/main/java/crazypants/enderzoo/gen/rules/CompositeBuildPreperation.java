package crazypants.enderzoo.gen.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.enderzoo.gen.WorldStructures;
import crazypants.enderzoo.gen.structure.Structure;

public class CompositeBuildPreperation implements IBuildPreperation {

  private final List<IBuildPreperation> preps = new ArrayList<IBuildPreperation>();

  public CompositeBuildPreperation() {
  }

  public void add(IBuildPreperation prep) {
    preps.add(prep);
  }

  @Override
  public boolean prepareLocation(Structure structure, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (IBuildPreperation rule : preps) {
      if(!rule.prepareLocation(structure, structures, world, random, chunkX, chunkZ)) {
        return false;
      }
    }
    return true;
  }

}
