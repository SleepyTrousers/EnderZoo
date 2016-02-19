package info.loenwind.owlgen.impl;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IGeneticsProvider;
import info.loenwind.owlgen.IWeightedGene;
import info.loenwind.owlgen.WeightedRandom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeneticsProvider implements IGeneticsProvider {

  public GeneticsProvider() {
  }

  @Override
  public IGene makeGene(Class<? extends IGene> clazz) {
    IGene[] enumConstants = clazz.getEnumConstants();
    List<IWeightedGene> list = new ArrayList<IWeightedGene>();
    for (IGene t : enumConstants) {
      list.addAll(t.getChildrenGenes());
    }
    IGene gene = WeightedRandom.getRandomItem(list).getGene();
    return gene;
  }

  @Override
  public List<IGene> makeGenes(List<Class<? extends IGene>> clazzes) {
    List<IGene> result = new ArrayList<IGene>();
    for (Class<? extends IGene> clazz : clazzes) {
      result.add(makeGene(clazz));
    }
    return result;
  }

  protected IGene expressGene(IGene parent0, IGene parent1) {
    List<IWeightedGene> list = new ArrayList<IWeightedGene>();
    list.addAll(parent0.getChildrenGenes());
    list.addAll(parent0.getChildrenGenes(parent1));
    list.addAll(parent1.getChildrenGenes());
    list.addAll(parent1.getChildrenGenes(parent0));
    return WeightedRandom.getRandomItem(list).getGene();
  }

  @Override
  public List<IGene> expressGenes(List<IGene> parent0, List<IGene> parent1) {
    List<IGene> result = new ArrayList<IGene>();
    Iterator<IGene> iterator0 = parent0.iterator();
    Iterator<IGene> iterator1 = parent1.iterator();
    while (iterator0.hasNext() || iterator1.hasNext()) {
      IGene gene0 = iterator0.hasNext() ? iterator0.next() : null;
      IGene gene1 = iterator1.hasNext() ? iterator1.next() : null;
      if (gene0 == null && gene1 == null) {
        result.add(null);
      } else {
        if (gene0 == null && gene1 != null) {
          gene0 = makeGene(gene1.getClass());
        } else if (gene0 != null && gene1 == null) {
          gene1 = makeGene(gene0.getClass());
        }
        result.add(expressGene(gene0, gene1));
      }
    }
    return result;
  }

  protected IGene makeChildGene(IGene parent0, IGene parent1, IGene expressed) {
    List<IWeightedGene> list = new ArrayList<IWeightedGene>();
    if (parent0 != null) {
      list.add(new WeightedGene(parent0, 2));
    }
    if (parent1 != null) {
      list.add(new WeightedGene(parent1, 2));
    }
    if (expressed != null && !expressed.equals(parent0) && !expressed.equals(parent1)) {
      list.add(new WeightedGene(expressed, 1));
    }
    return WeightedRandom.getRandomItem(list).getGene();
  }

  @Override
  public List<IGene> makeChildGenes(List<IGene> parent0, List<IGene> parent1, List<IGene> expressed) {
    List<IGene> result = new ArrayList<IGene>();
    Iterator<IGene> iterator0 = parent0.iterator();
    Iterator<IGene> iterator1 = parent1.iterator();
    Iterator<IGene> iteratorE = expressed.iterator();
    while (iterator0.hasNext() || iterator1.hasNext() || iteratorE.hasNext()) {
      IGene gene0 = iterator0.hasNext() ? iterator0.next() : null;
      IGene gene1 = iterator1.hasNext() ? iterator1.next() : null;
      IGene geneE = iteratorE.hasNext() ? iteratorE.next() : null;
      result.add(makeChildGene(gene0, gene1, geneE));
    }
    return result;
  }

}
