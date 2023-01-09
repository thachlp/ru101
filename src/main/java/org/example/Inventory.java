package org.example;

import org.example.entity.Customer;
import org.example.util.KeyHelper;
import redis.clients.jedis.Jedis;

public class Inventory {
  private final Jedis jedis;

  public Inventory(Jedis jedis) {
    this.jedis = jedis;
  }

  public void createCustomers(Customer[] customers) {
    for (Customer customer : customers) {
      String customerKey = KeyHelper.createKey("customer", customer.getId());
      jedis.hset(customerKey, customer.toMap());
    }
  }

}
