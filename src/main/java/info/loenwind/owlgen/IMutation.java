package info.loenwind.owlgen;

import java.util.List;

public interface IMutation {

  IGene getParner0();

  IGene getParner1();

  List<IWeightedGene> getMutations();

}
