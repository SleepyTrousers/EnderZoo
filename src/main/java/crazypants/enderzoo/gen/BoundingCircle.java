package crazypants.enderzoo.gen;

import java.util.Collection;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import crazypants.enderzoo.vec.Vector2d;

public class BoundingCircle {

  private final Vector2d origin = new Vector2d();
  private final double radius;

  public BoundingCircle(AxisAlignedBB bb) {
    origin.x = bb.maxX - bb.minX;
    origin.y = bb.maxZ - bb.minZ;
    radius = origin.distance(new Vector2d(bb.minX, bb.maxX));
  }

  public BoundingCircle(int x, int y, int radius) {
    origin.set(x, y);
    this.radius = radius;
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

  public Collection<ChunkCoordIntPair> getChunks() {
    //This will get some chunks on the 'corners' that arn't in range with larger radius's, but good enough for now
    int minChunkX = (int) (origin.x - radius) >> 4;
    int minChunkZ = (int) (origin.y - radius) >> 4;
    int maxChunkX = (int) (origin.x + radius) >> 4;
    int maxChunkZ = (int) (origin.y + radius) >> 4;
    ChunkBounds cb = new ChunkBounds(minChunkX, minChunkZ, maxChunkX, maxChunkZ);
    return cb.getChunks();
  }

}
