package crazypants.enderzoo.entity.genes;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IMutationProvider;
import info.loenwind.owlgen.IWeightedGene;
import info.loenwind.owlgen.impl.MutationProvider;

import java.util.List;

public enum OwlBeakColor implements IGene {
  MEDIUM(10),
  DARK(10),
  BROWN(5),
  GRAY(10),
  BLACK(5);

  private static final IMutationProvider mut = new MutationProvider();

  static {
    for (OwlBeakColor value : values()) {
      mut.registerMutation(value, null, value, value.weight);
    }
  }

  private final int weight;

  private OwlBeakColor(int weight) {
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
