package org.example;

import org.example.util.KeyHelper;
import org.example.util.TextIncrease;
import redis.clients.jedis.Jedis;

public class SeatReservation {
  private static final int MAX_SEATS_PER_BLOCK = 32;

  private final Jedis jedis;

  public SeatReservation(Jedis jedis) {
    this.jedis = jedis;
  }

  /**
   * Create the seats blocks for the given event. 32 bits are available for
   * seats. This could be extended to accommodate more bits, by storing multiple
   * u32 fields
   */
  public void createEvent(String sku, int blocks, int seatsPerBlock, String tier) {
    String blockName = "A";
    for (int i = 0; i < blocks; i++) {
      int filledSeatMap = (int)Math.pow(2, Math.min(seatsPerBlock, MAX_SEATS_PER_BLOCK)) - 1;
      String[] values = new String[] {"SET", "u32", "0", String.valueOf(filledSeatMap)};
      String key = KeyHelper.createKey("seatmap", sku, tier, blockName);
      jedis.bitfield(key, values);
      blockName = TextIncrease.increaseString(blockName);
    }
  }
}
