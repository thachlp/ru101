package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Customer;
import org.example.entity.EventInventory;
import org.example.util.KeyHelper;
import redis.clients.jedis.Jedis;

public class Inventory {
  private final Jedis jedis;

  public Inventory(Jedis jedis) {
    this.jedis = jedis;
  }

  /**
   * Create customer keys from the array of passed customer details
   */
  public void createCustomers(Customer[] customers) {
    for (Customer customer : customers) {
      String customerKey = KeyHelper.createKey("customer", customer.getId());
      jedis.hset(customerKey, customer.toMap());
    }
  }

  /**
   * Create events from the array of passed event details. Provides overrides
   * for number of available tickets, price and ticket tier.
   */
  public void createEvent(EventInventory[] events, int available, int price, String tier)
      throws JsonProcessingException {
    String keyListSku = KeyHelper.createKey("events");
    for (EventInventory eventInventory : events) {
      if (available > 0) {
        eventInventory.setAvailable(available);
      }
      if (price > 0.0) {
        eventInventory.setPrice(price);
      }
      String keyDetail = KeyHelper.createKey("event", eventInventory.getSku());
      jedis.sadd(keyListSku, eventInventory.getSku());
      jedis.hmset(keyDetail, eventInventory.toMap(tier));
    }
  }

}
