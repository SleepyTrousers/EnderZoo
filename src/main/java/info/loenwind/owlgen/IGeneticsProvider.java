package info.loenwind.owlgen;

import java.util.List;

/**
 * Methods to work with gene sets.
 * <p>
 * <strong>Operations:</strong>
 * <p>
 * Create a new individual (i0) without parents:
 * <ul>
 * <li>i0.parent0 = makeGenes()
 * <li>i0.parent1 = makeGenes()
 * <li>i0.expressed = expressGenes(i0.parent0, i0.parent1)
 * </ul>
 * <p>
 * Create a new individual (i1) from two parents (p0, p1):
 * <ul>
 * <li>i1.parent0 = makeChildGenes(p0.parent0, p0.parent1, p0.expressed)
 * <li>i1.parent1 = makeChildGenes(p1.parent0, p1.parent1, p1.expressed)
 * <li>i1.expressed = expressGenes(i1.parent0, i1.parent1)
 * </ul>
 * 
 */
public interface IGeneticsProvider {

  /**
   * Creates a single random gene of the given class.
   */
  IGene makeGene(Class<? extends IGene> clazz);

  /**
   * Creates a random gene set for an unknown parent.
   */
  List<IGene> makeGenes(List<Class<? extends IGene>> clazzes);

  /**
   * Combines two parent gene sets into an expressed set.
   */
  List<IGene> expressGenes(List<IGene> parent0, List<IGene> parent1);

  /**
   * Extracts a gene set from a parent to give to a child.
   */
  List<IGene> makeChildGenes(List<IGene> parent0, List<IGene> parent1, List<IGene> expressed);

}
