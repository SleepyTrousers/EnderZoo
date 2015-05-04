package crazypants.enderzoo.gen;

import java.util.Collection;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.vec.Point3i;
import crazypants.enderzoo.vec.Vector2d;

public class BoundingCircle {

  private final Vector2d origin = new Vector2d();
  private final double radius;
  private final double radius2;

  public BoundingCircle(AxisAlignedBB bb) {
    Point3i size = Structure.size(bb);
    origin.x = (int)bb.minX + (size.x/2);
    origin.y = (int)bb.minZ + (size.z/2);
    radius = origin.distance(new Vector2d(bb.minX, bb.minZ));
    radius2 = radius * radius;
  }

  public BoundingCircle(double x, double y, double radius) {
    origin.set(x, y);
    this.radius = radius;
    radius2 = radius * radius;
  }

  public double distanceToPoint(double x, double z) {
    double res = origin.distance(new Vector2d(x, z)) - radius;
    return Math.max(res, 0);
  }

  public double distance(BoundingCircle other) {
    double res = distanceToPoint(other.origin.x, other.origin.y) - other.radius;
    return Math.max(res, 0);
  }

  public double getRadius() {
    return radius;
  }

  public Vector2d getOrigin() {
    return origin;
  }

  public double getRadiusSquared() {
    return radius2;
  }

  public Collection<ChunkCoordIntPair> getChunks() {
    //This will get some chunks on the 'corners' that arn't in range with larger radius's, but good enough for now
    int minChunkX = (int) (origin.x - radius) >> 4;
    int minChunkZ = (int) (origin.y - radius) >> 4;
    int maxChunkX = (int) (origin.x + radius) >> 4;
    int maxChunkZ = (int) (origin.y + radius) >> 4;
    ChunkBounds cb = new ChunkBounds(minChunkX, minChunkZ, maxChunkX, maxChunkZ);
    return cb.getChunks();
  }

  public boolean containsPoint(int x, int z) {    
    return origin.distanceSquared(x, z) <= radius2;
  }
  
  public boolean intersects(BoundingCircle other) {
    return origin.distanceSquared(other.origin.x, other.origin.y) <=  (radius + other.radius) * (radius + other.radius);
  }

  @Override
  public String toString() {
    return "BoundingCircle [origin=" + origin + ", radius=" + radius + "]";
  }

}
