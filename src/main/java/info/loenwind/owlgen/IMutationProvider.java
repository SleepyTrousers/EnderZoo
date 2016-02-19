package info.loenwind.owlgen;

import java.util.List;

/**
 * This is an optional helper for IGenes to store their mutation data.
 */
public interface IMutationProvider {

  /**
   * Registers a mutation of two parent genes.
   * <p>
   * This may be a true mutation (e.g. RED+BROWN=BLUE), or a probability shift
   * (e.g. RED+BROWN=BROWN w=100).
   * <p>
   * Null parameters are valid.
   */
  void registerMutation(IGene partner0, IGene partner1, IWeightedGene child);

  /**
   * Registers a mutation of two parent genes.
   * <p>
   * This may be a true mutation (e.g. RED+BROWN=BLUE), or a probability shift
   * (e.g. RED+BROWN=BROWN w=100).
   * <p>
   * Null parameters are valid.
   */
  void registerMutation(IGene partner0, IGene partner1, IGene child, int weight);

  /**
   * Get the registered mutations for partner0 and partner1. The order of the
   * parameters doesn't matter.
   * <p>
   * Null parameters are valid.
   */
  List<IWeightedGene> getChildrenGenes(IGene partner0, IGene partner1);

}
