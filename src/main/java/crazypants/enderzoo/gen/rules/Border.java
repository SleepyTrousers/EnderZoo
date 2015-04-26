package crazypants.enderzoo.gen.rules;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

public class Border {

  private final Map<ForgeDirection, Integer> border = new HashMap<ForgeDirection, Integer>();

  public void setBorderXZ(int size) {
    setBorder(size, size, size, size);
  }

  public void setVerticalBorder(int down, int up) {
    border.put(ForgeDirection.DOWN, down);
    border.put(ForgeDirection.UP, up);
  }

  public void setBorder(int north, int south, int east, int west) {
    border.put(ForgeDirection.EAST, east);
    border.put(ForgeDirection.WEST, west);
    border.put(ForgeDirection.NORTH, north);
    border.put(ForgeDirection.SOUTH, south);
  }

  public void setBorder(int north, int south, int east, int west, int up, int down) {
    border.put(ForgeDirection.DOWN, down);
    border.put(ForgeDirection.UP, up);
    border.put(ForgeDirection.EAST, east);
    border.put(ForgeDirection.WEST, west);
    border.put(ForgeDirection.NORTH, north);
    border.put(ForgeDirection.SOUTH, south);
  }

  public int get(ForgeDirection dir) {
    Integer res = border.get(dir);
    if(res == null) {
      return 0;
    }
    return res;
  }

}
