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

  public void add(Point3i other) {
    x += other.x;
    y += other.y;
    z += other.z;
  }

  @Override
  public String toString() {
    return "Point3i [x=" + x + ", y=" + y + ", z=" + z + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    result = prime * result + z;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(getClass() != obj.getClass()) {
      return false;
    }
    Point3i other = (Point3i) obj;
    if(x != other.x) {
      return false;
    }
    if(y != other.y) {
      return false;
    }
    if(z != other.z) {
      return false;
    }
    return true;
  }

}
