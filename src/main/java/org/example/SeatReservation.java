package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.util.KeyHelper;
import org.example.util.TextIncrease;
import redis.clients.jedis.Jedis;

public class SeatReservation {
  private static final int MAX_SEATS_PER_BLOCK = 32;

  private final Jedis jedis;

  private final ObjectMapper objectMapper;

  public SeatReservation(Jedis jedis, ObjectMapper objectMapper) {
    this.jedis = jedis;
    this.objectMapper = objectMapper;
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

  /**
   * For the given Event, Tier and Block, return the seat map
   */
  public Long getEventSeatBlock(String sku, String tier, String blockName) {
    String key = KeyHelper.createKey("seatmap", sku, tier, blockName);
    String[] values = new String[]{"GET", "u32", "0"};
    return jedis.bitfield(key, values).get(0);
  }

  /**
   * Return the available contiguous seats that match the criteria
   */
  public List<String> getAvailable(int seatMap, int seatsRequired) throws JsonProcessingException {
    List<String> seats = new ArrayList<>();
    int endSeat = Integer.bitCount(seatMap) + 1;
    if (seatsRequired <= endSeat) {
      int requiredBlock = (int)Math.pow(2, seatsRequired) - 1;
      for (int i = 1; i <= endSeat + 1; i++) {
        if ((seatMap & requiredBlock) == requiredBlock) {
          Map<String, String> properties = new HashMap<>();
          properties.put("first_seat", String.valueOf(i));
          properties.put("last_seat", String.valueOf(i + seatsRequired - 1));
          seats.add(objectMapper.writeValueAsString(properties));
          requiredBlock = requiredBlock << 1;
        }
      }
    }
    return  seats;
  }

}
