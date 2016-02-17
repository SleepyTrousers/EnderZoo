package info.loenwind.owlgen.impl;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IWeightedGene;

public class WeightedGene implements IWeightedGene {

  private final IGene gene;
  private final int weight;

  public WeightedGene(IGene gene, int weight) {
    this.gene = gene;
    this.weight = weight;
  }

  @Override
  public int getWeight() {
    return weight;
  }

  @Override
  public IGene getGene() {
    return gene;
  }

}
