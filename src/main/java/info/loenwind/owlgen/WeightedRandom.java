package info.loenwind.owlgen;

import java.util.Collection;
import java.util.Random;

/**
 * Same as Minecraft's WeightedRandom, just with an interface instead of a base
 * class for the item.
 * <p>
 * And much less code.
 *
 */
public class WeightedRandom {

  private static final Random random = new Random();

  public static <T extends IWeightedItem> T getRandomItem(Collection<T> collection) {
    int totalWeight = 0;
    for (T item : collection) {
      totalWeight += item.getWeight();
    }
    int i = random.nextInt(totalWeight);
    for (T item : collection) {
      i -= item.getWeight();
      if (i < 0) {
        return item;
      }
    }
    return (T) null;
  }
}