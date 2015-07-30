package crazypants.enderzoo.vec;

public class Point3i {

  public int x;
  public int y;
  public int z;
  
  public Point3i() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Point3i(int x, int y, int z) {  
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point3i(Point3i other) {
    x = other.x;
    y = other.y;
    z = other.z;
  }

  
  
}
