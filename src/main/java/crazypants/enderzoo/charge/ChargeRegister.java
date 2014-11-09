package crazypants.enderzoo.charge;

import java.util.HashMap;
import java.util.Map;

public class ChargeRegister {

  public static ChargeRegister instance = new ChargeRegister();

  private Map<Integer, ICharge> charges = new HashMap<Integer, ICharge>();

  private int nextId = 0;

  private ChargeRegister() {
  }

  public void registerCharge(ICharge charge) {
    charge.setID(nextId);
    charges.put(nextId, charge);
    ++nextId;

  }

  public ICharge getCharge(int id) {
    return charges.get(id);
  }

}
