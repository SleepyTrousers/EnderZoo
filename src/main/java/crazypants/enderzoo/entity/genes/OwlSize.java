package crazypants.enderzoo.entity.genes;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IMutationProvider;
import info.loenwind.owlgen.IWeightedGene;
import info.loenwind.owlgen.impl.MutationProvider;

import java.util.List;

public enum OwlSize implements IGene {
  TINY(1, 0.7f),
  SMALL(2, 0.8f),
  SMALLISH(4, 0.95f),
  NORMAL(8, 1.0f),
  LARGISH(4, 1.05f),
  LARGE(2, 1.2f),
  HUGE(1, 1.3f);

  private static final IMutationProvider mut = new MutationProvider();

  static {
    for (OwlSize value : values()) {
      mut.registerMutation(value, null, value, value.weight);
    }

    for (OwlSize owl1 : values()) {
      for (OwlSize owl2 : values()) {
        if (owl2.ordinal() > owl1.ordinal()) {
          for (OwlSize owl3 : values()) {
            if (owl3.ordinal() > owl1.ordinal() && owl3.ordinal() < owl2.ordinal()) {
              mut.registerMutation(owl1, owl2, owl3, owl1.weight + owl2.weight);
            }
          }
        }
      }
    }
  }

  private final int weight;
  private final float size;

  private OwlSize(int weight, float size) {
    this.weight = weight;
    this.size = size;
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

  public float getSize() {
    return size;
  }
}
