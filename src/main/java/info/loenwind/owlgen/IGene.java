package info.loenwind.owlgen;

import java.util.List;

public interface IGene {

  /**
   * Get the probabilities for this gene to breed true, that is to be expressed
   * regardless of the partner gene.
   * <p>
   * This is also used to populate random new gene sets.
   * <p>
   * Note: This should return a list with a single item which is for this
   * element. If this element is not in the list, that means this gene can not
   * exist in a random new gene set. If there are other elements included, that
   * means this gene can randomly mutate into that other gene.
   */
  List<IWeightedGene> getChildrenGenes();

  /**
   * Get the resultant genes and their probabilities when breeding this gene
   * with the given other gene.
   * <p>
   * The result is combined with the reverse call on the partner and the results
   * of getChildrenGenes() on both this gene and the partner.
   */
  List<IWeightedGene> getChildrenGenes(IGene partner);

  int ordinal();
}
