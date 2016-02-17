package crazypants.enderzoo.entity.genes;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IMutationProvider;
import info.loenwind.owlgen.IWeightedGene;
import info.loenwind.owlgen.impl.MutationProvider;

import java.util.List;

public enum OwlCoatColor implements IGene {
  LIGHT(10),
  DARK(2),
  GOLD(3),
  WHITE(1);

  private static final IMutationProvider mut = new MutationProvider();

  static {
    for (OwlCoatColor value : values()) {
      mut.registerMutation(value, null, value, value.weight);
    }
    mut.registerMutation(LIGHT, DARK, DARK, 20);
    mut.registerMutation(LIGHT, DARK, LIGHT, 100);
    mut.registerMutation(LIGHT, DARK, GOLD, 1);

    mut.registerMutation(LIGHT, GOLD, DARK, 20);
    mut.registerMutation(LIGHT, GOLD, LIGHT, 100);
    mut.registerMutation(LIGHT, GOLD, GOLD, 50);

    mut.registerMutation(LIGHT, WHITE, LIGHT, 100);
    mut.registerMutation(LIGHT, WHITE, WHITE, 10);

    mut.registerMutation(DARK, GOLD, DARK, 50);
    mut.registerMutation(DARK, GOLD, GOLD, 100);

    mut.registerMutation(DARK, WHITE, LIGHT, 100);
    mut.registerMutation(DARK, WHITE, DARK, 50);
    mut.registerMutation(DARK, WHITE, WHITE, 50);

    mut.registerMutation(GOLD, WHITE, WHITE, 50);
    mut.registerMutation(GOLD, WHITE, GOLD, 50);
    mut.registerMutation(GOLD, WHITE, LIGHT, 10);
  }

  private final int weight;

  private OwlCoatColor(int weight) {
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
