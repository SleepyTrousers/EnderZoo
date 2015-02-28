package crazypants.enderzoo.vec;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class VecUtil {

	public static BlockPos bpos(double x, double y, double z) {
		return bpos((int)x, (int)y, (int)z);
	}
	
  public static BlockPos bpos(int x, int y, int z) {
	 return new BlockPos(x, y, z); 
  }
	
  public static Vec3 scale(Vec3 vec, double scale) {
	  return new Vec3(vec.xCoord * scale, vec.yCoord * scale, vec.zCoord * scale);
  }

  public static Vec3 copy(Vec3 vec) {
    return new Vec3(vec.xCoord, vec.yCoord, vec.zCoord);
  }

  public static Vec3 subtract(Vec3 a, Vec3 b) {    
    return new Vec3(a.xCoord - b.xCoord, a.yCoord - b.yCoord, a.zCoord - b.zCoord);
  }

  public static Vec3 add(Vec3 a, Vec3 b) {    
    return new Vec3(a.xCoord + b.xCoord, a.yCoord+ b.yCoord, a.zCoord+ b.zCoord);
  }

}
