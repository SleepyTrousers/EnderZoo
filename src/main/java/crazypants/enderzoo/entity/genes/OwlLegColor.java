package crazypants.enderzoo.entity.genes;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IMutationProvider;
import info.loenwind.owlgen.IWeightedGene;
import info.loenwind.owlgen.impl.MutationProvider;

import java.util.List;

public enum OwlLegColor implements IGene {
  NORMAL(10),
  BROWN(5),
  RED(5),
  GRAY(1),
  BLACK(3);

  private static final IMutationProvider mut = new MutationProvider();

  static {
    for (OwlLegColor value : values()) {
      mut.registerMutation(value, null, value, value.weight);
    }
    mut.registerMutation(NORMAL, NORMAL, BROWN, 5);
    mut.registerMutation(NORMAL, NORMAL, RED, 5);
    mut.registerMutation(NORMAL, NORMAL, GRAY, 5);

    mut.registerMutation(BROWN, BROWN, BLACK, 5);

    mut.registerMutation(RED, GRAY, BROWN, 5);
    mut.registerMutation(RED, GRAY, NORMAL, 5);
  }

  private final int weight;

  private OwlLegColor(int weight) {
    this.weight = weight;
  }

  @Override
  public List<IWeightedGene> getChildrenGenes() {
    return mut.getChildrenGenes(this, null);
  }

  @Override
  public List<IWeightedGene> getChildrenGenes(IGene partner) {
    return mut.getChildrenGenes(this, partner);
  }

  public static int count() {
    return values().length + 1;
  }
}
