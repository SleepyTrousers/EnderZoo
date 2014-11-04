package crazypants.enderzoo.vec;

import net.minecraft.util.Vec3;

public class VecUtil {

  public static Vec3 scale(Vec3 vec, double scale) {
    Vec3 result = copy(vec);
    result.xCoord = vec.xCoord * scale;
    result.yCoord = vec.yCoord * scale;
    result.zCoord = vec.zCoord * scale;
    return result;
  }

  public static Vec3 copy(Vec3 vec) {
    return Vec3.createVectorHelper(vec.xCoord, vec.yCoord, vec.zCoord);
  }

  public static Vec3 subtract(Vec3 a, Vec3 b) {    
    return Vec3.createVectorHelper(a.xCoord - b.xCoord, a.yCoord - b.yCoord, a.zCoord - b.zCoord);
  }

  public static Vec3 add(Vec3 a, Vec3 b) {    
    return Vec3.createVectorHelper(a.xCoord + b.xCoord, a.yCoord+ b.yCoord, a.zCoord+ b.zCoord);
  }

  public static void set(Vec3 pos, double posX, double posY, double posZ) {
    pos.xCoord = posX;
    pos.yCoord = posY;
    pos.zCoord = posZ;    
  }
  
}
