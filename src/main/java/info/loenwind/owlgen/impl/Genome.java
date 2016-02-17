package info.loenwind.owlgen.impl;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IGeneticsProvider;
import info.loenwind.owlgen.IGenome;

import java.util.List;

import net.minecraft.entity.DataWatcher;

/**
 * A Genome is a set of genes that can be used in an entity. It will use the
 * entity's datawatcher to automatically sync itself to the client.
 * <p>
 * This class can also used without datawatcher, in which case nothing is
 * synced.
 * <p>
 * Note: This class can <em>not</em> handle null genes.
 */
public class Genome implements IGenome {

  private static final IGeneticsProvider geneticsProvider = new GeneticsProvider();

  private final DataWatcher dataWatcher;
  private final int id;
  private boolean syncing = false;

  private final int genomesize;
  private final List<IGene> genes0;
  private final List<IGene> genes1;
  private final List<IGene> genesE;

  private String data;

  private Genome(DataWatcher datawatcher, int id, int genomesize, List<IGene> genes0, List<IGene> genes1) {
    this.dataWatcher = datawatcher;
    this.id = id;
    this.genomesize = genomesize;
    this.genes0 = genes0;
    this.genes1 = genes1;
    this.genesE = geneticsProvider.expressGenes(genes0, genes1);

    data = makeData();
  }

  /**
   * Creates a new genome with random genes from the given gene template.
   * 
   * @param datawatcher
   *          The datawatcher to use for syncing. Can be null to disable
   *          syncing.
   * @param id
   *          The id to use with the datawatcher. Ignored if there is no
   *          datawatcher.
   * @param genetemplate
   *          The gene template to use.
   */
  public Genome(DataWatcher datawatcher, int id, List<Class<? extends IGene>> genetemplate) {
    this(datawatcher, id, genetemplate.size(), geneticsProvider.makeGenes(genetemplate), geneticsProvider.makeGenes(genetemplate));
  }

  /**
   * Creates a new genome with with genes derived from one parent and a random
   * set of genes from the given gene template.
   * 
   * @param datawatcher
   *          The datawatcher to use for syncing. Can be null to disable
   *          syncing.
   * @param id
   *          The id to use with the datawatcher. Ignored if there is no
   *          datawatcher.
   * @param genetemplate
   *          The gene template to use.
   * @param parent0
   *          The parent.
   */
  public Genome(DataWatcher datawatcher, int id, List<Class<? extends IGene>> genetemplate, IGenome parent0) {
    this(datawatcher, id, genetemplate.size(), geneticsProvider.makeChildGenes(parent0.getGenes0(), parent0.getGenes1(), parent0.getGenesE()), geneticsProvider
        .makeGenes(genetemplate));
  }

  /**
   * Creates a new genome with with genes derived from the given two parents.
   * 
   * @param datawatcher
   *          The datawatcher to use for syncing. Can be null to disable
   *          syncing.
   * @param id
   *          The id to use with the datawatcher. Ignored if there is no
   *          datawatcher.
   * @param genetemplate
   *          The gene template to use.
   * @param parent0
   *          The first parent.
   * @param parent1
   *          The second parent.
   */
  public Genome(DataWatcher datawatcher, int id, IGenome parent0, IGenome parent1) {
    this(datawatcher, id, parent0.getGenes0().size(), geneticsProvider.makeChildGenes(parent0.getGenes0(), parent0.getGenes1(), parent0.getGenesE()),
        geneticsProvider.makeChildGenes(parent1.getGenes0(), parent1.getGenes1(), parent1.getGenesE()));
  }

  /**
   * Converts the genes into an UTF-8-friendly String.
   */
  private String makeData() {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < genomesize; i++) {
      int g0 = genes0.get(i).ordinal();
      int g1 = genes1.get(i).ordinal();
      int gE = genesE.get(i).ordinal();
      int bm = 1, v = 0;
      while (g0 != 0 || g1 != 0 || gE != 0) {
        if ((g0 & 1) != 0) {
          v |= bm;
        }
        bm = bm << 1;
        g0 = g0 >> 1;
        if ((g1 & 1) != 0) {
          v |= bm;
        }
        bm = bm << 1;
        g1 = g1 >> 1;
        if ((gE & 1) != 0) {
          v |= bm;
        }
        bm = bm << 1;
        gE = gE >> 1;
      }
      b.appendCodePoint(v);
    }
    return b.toString();
  }

  /**
   * Sets the genes from the given String.
   */
  private void readData(String data) {
    if (data != null && this.data != data && data.length() <= genomesize) {
      for (int i = 0; i < data.length(); i++) {
        int v = data.codePointAt(i);
        int g0 = 0, g1 = 0, gE = 0, bm = 1;
        while (v != 0) {
          if ((v & 1) != 0) {
            g0 |= bm;
          }
          v = v >> 1;
          if ((v & 1) != 0) {
            g1 |= bm;
          }
          v = v >> 1;
          if ((v & 1) != 0) {
            gE |= bm;
          }
          v = v >> 1;

          bm = bm << 1;
        }
        genes0.set(i, genes0.get(i).getClass().getEnumConstants()[g0]);
        genes1.set(i, genes1.get(i).getClass().getEnumConstants()[g1]);
        genesE.set(i, genesE.get(i).getClass().getEnumConstants()[gE]);
      }
      this.data = data;
    }
  }

  /**
   * Updates the genes from the datawatcher if it has changed.
   */
  private void updateData() {
    if (syncing) {
      readData(dataWatcher.getWatchableObjectString(id));
    }
  }

  /* (non-Javadoc)
   * @see info.loenwind.owlgen.impl.IGenome#setData(java.lang.String)
   */
  @Override
  public void setData(String data) {
    try {
      readData(data);
      if (syncing) {
        dataWatcher.updateObject(id, data);
      }
    } catch (Throwable t) {
    }
  }

  /* (non-Javadoc)
   * @see info.loenwind.owlgen.impl.IGenome#getData()
   */
  @Override
  public String getData() {
    return data;
  }

  /* (non-Javadoc)
   * @see info.loenwind.owlgen.impl.IGenome#getGenes0()
   */
  @Override
  public List<IGene> getGenes0() {
    updateData();
    return genes0;
  }

  /* (non-Javadoc)
   * @see info.loenwind.owlgen.impl.IGenome#getGenes1()
   */
  @Override
  public List<IGene> getGenes1() {
    updateData();
    return genes1;
  }

  /* (non-Javadoc)
   * @see info.loenwind.owlgen.impl.IGenome#getGenesE()
   */
  @Override
  public List<IGene> getGenesE() {
    updateData();
    return genesE;
  }

  /* (non-Javadoc)
   * @see info.loenwind.owlgen.impl.IGenome#startSync()
   */
  @Override
  public void startSync() {
    data = makeData();

    if (dataWatcher != null) {
      dataWatcher.addObject(id, data);
      syncing = true;
    }
  }

}
