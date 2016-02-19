package crazypants.enderzoo.entity.genes;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IMutationProvider;
import info.loenwind.owlgen.IWeightedGene;
import info.loenwind.owlgen.impl.MutationProvider;

import java.util.List;

public enum OwlShade implements IGene {
  VOID(1, 0.5f),
  END(2, 0.65f),
  NIGHT(4, 0.75f),
  NETHER(4, 0.85f),
  DAWN(8, 0.9f),
  DAY(8, 0.95f),
  NORMAL(16, 1f);

  private static final IMutationProvider mut = new MutationProvider();

  static {
    for (OwlShade value : values()) {
      mut.registerMutation(value, null, value, value.weight);
    }

    for (OwlShade owl1 : values()) {
      for (OwlShade owl2 : values()) {
        if (owl2.ordinal() > owl1.ordinal()) {
          for (OwlShade owl3 : values()) {
            if (owl3.ordinal() > owl1.ordinal() && owl3.ordinal() < owl2.ordinal()) {
              mut.registerMutation(owl1, owl2, owl3, owl1.weight + owl2.weight + owl3.weight);
            }
          }
        }
      }
    }
  }

  private final int weight;
  private final float shade;

  private OwlShade(int weight, float shade) {
    this.weight = weight;
    this.shade = shade;
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

  public float getShade() {
    return shade;
  }
}
