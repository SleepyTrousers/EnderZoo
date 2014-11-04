package crazypants.enderzoo.vec;

public class Point3i {

  public int x;
  public int y;
  public int z;
  
  public Point3i(int x, int y, int z) {  
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point3i(Point3i other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
  }
  
  
}
