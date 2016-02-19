package info.loenwind.owlgen.impl;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IMutation;
import info.loenwind.owlgen.IWeightedGene;

import java.util.List;

/**
 * Just a storage element.
 */
public class Mutation implements IMutation {

  private final IGene partner0;
  private final IGene partner1;
  private final List<IWeightedGene> mutations;

  public Mutation(IGene partner0, IGene partner1, List<IWeightedGene> mutations) {
    this.partner0 = partner0;
    this.partner1 = partner1;
    this.mutations = mutations;
  }

  @Override
  public IGene getParner0() {
    return partner0;
  }

  @Override
  public IGene getParner1() {
    return partner1;
  }

  @Override
  public List<IWeightedGene> getMutations() {
    return mutations;
  }

}
