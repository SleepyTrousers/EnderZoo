package info.loenwind.owlgen.impl;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IMutation;
import info.loenwind.owlgen.IMutationProvider;
import info.loenwind.owlgen.IWeightedGene;

import java.util.ArrayList;
import java.util.List;

public class MutationProvider implements IMutationProvider {

  private final List<IMutation> mutations = new ArrayList<IMutation>();

  @Override
  public void registerMutation(IGene partner0, IGene partner1, IWeightedGene child) {
    getChildrenGenes(partner0, partner1).add(child);
  }

  @Override
  public void registerMutation(IGene partner0, IGene partner1, IGene child, int weight) {
    getChildrenGenes(partner0, partner1).add(new WeightedGene(child, weight));
  }

  @Override
  public List<IWeightedGene> getChildrenGenes(IGene partner0, IGene partner1) {
    for (IMutation mutation : mutations) {
      if ((eq(partner0, mutation.getParner0()) && eq(partner1, mutation.getParner1()))
          || (eq(partner0, mutation.getParner1()) && eq(partner1, mutation.getParner0()))) {
        return mutation.getMutations();
      }
    }
    List<IWeightedGene> list = new ArrayList<IWeightedGene>();
    mutations.add(new Mutation(partner0, partner1, list));
    return list;
  }

  private static boolean eq(IGene a, IGene b) {
    if (a == b) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return a.equals(b);
  }

}
