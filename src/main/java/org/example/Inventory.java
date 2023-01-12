package org.example;

import java.util.Map.Entry;
import org.example.entity.Customer;
import org.example.entity.EventInventory;
import org.example.entity.Purchase;
import org.example.util.KeyHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.resps.ScanResult;

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
   * Create events from the array of passed event details. Provides overrides for number of
   * available tickets, price and ticket tier.
   */
  public void createEvent(EventInventory[] events, int available, double price, String tier) {
    String skuKeys = KeyHelper.createKey("events");
    for (EventInventory eventInventory : events) {
      if (available > 0) {
        eventInventory.setAvailable(available);
      }
      if (price > 0.0) {
        eventInventory.setPrice(price);
      }
      String eventKey = KeyHelper.createKey("event", eventInventory.getSku());
      jedis.sadd(skuKeys, eventInventory.getSku());
      jedis.hmset(eventKey, eventInventory.toMap(tier));
    }
  }

  /**
   * Check if there is sufficient inventory before making the purchase
   */
  public void checkAvailabilityAndPurchase(Customer customer, String sku, int quantity,
      String tier, String orderId) {
    Pipeline pipeline = jedis.pipelined();
    try {
      String eventKey = KeyHelper.createKey("event", sku);
      jedis.watch(eventKey);
      int available = Integer.parseInt(
          jedis.hget(eventKey, String.format("available:%s", tier)));
      double price = Double.parseDouble(
          jedis.hget(eventKey, String.format("price:%s", tier)));
      if (available > quantity) {
        pipeline.hincrBy(eventKey, String.format("available:%s", tier), -quantity);
        Purchase purchase = new Purchase();
        purchase.setOrderId(orderId);
        purchase.setCustomer(customer);
        purchase.setTier(tier);
        purchase.setQuantity(quantity);
        purchase.setCost(price * quantity);
        purchase.setSku(sku);
        purchase.setCreatedAt(System.currentTimeMillis());
        String orderKey = KeyHelper.createKey("sales_order", orderId);
        pipeline.hmset(orderKey, purchase.toMap());
        pipeline.sync();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Need to call the auth service to check, but here we always return true
   */
  private boolean creditCardAuth(Customer customer, Double orderTotal) {
    if (!customer.getCustomerName().isEmpty() && orderTotal < 100_000_000.0) {
      return true;
    }
    return false;
  }

  /**
   * Reserve stock & Credit Card auth
   * First reserve the inventory and perform a credit authorization. If successful
   * then confirm the inventory deduction or back the deduction out.
   */
  public void reserve(Customer customer, String sku, int quantity, String tier, String orderId) {
    Pipeline pipeline = new Pipeline(jedis);
    try {
      String eventKey = KeyHelper.createKey("event", sku);
      jedis.watch(eventKey);
      int available = Integer.parseInt(
          jedis.hget(eventKey, String.format("available:%s", tier)));
      double price = Double.parseDouble(
          jedis.hget(eventKey, String.format("price:%s", tier)));
      if (available > quantity && creditCardAuth(customer, price * quantity)) {
        long currentTime = System.currentTimeMillis();
        pipeline.hincrBy(eventKey, "available:" + tier, -quantity);
        pipeline.hincrBy(eventKey, "held:" + tier, quantity);
        String holdKey = KeyHelper.createKey("ticket_hold", sku);
        pipeline.hsetnx(holdKey, "qty:" + orderId, String.valueOf(quantity));
        pipeline.hsetnx(holdKey, "tier:" + orderId, tier);
        pipeline.hsetnx(holdKey, "ts:" + orderId, String.valueOf(currentTime));
        pipeline.sync();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Remove the ticket reservation
   */
  private void backoutHold(String sku, String orderId) {
    Pipeline pipeline = jedis.pipelined();
    try {
      String holdKey = KeyHelper.createKey("ticket_hold", sku);
      String eventKey = KeyHelper.createKey("event", sku);
      jedis.watch(eventKey);

      int quantity = Integer.parseInt(jedis.hget(holdKey, "qty:" + orderId));
      String tier = jedis.hget(holdKey, "tier:" + orderId);
      pipeline.hincrBy(eventKey, "available:" + tier, quantity);
      pipeline.hincrBy(eventKey, "held:" + tier, -quantity);
      pipeline.hdel(holdKey, "qty:" + orderId);
      pipeline.hdel(holdKey,  "tier:" + orderId);
      pipeline.hdel(holdKey, "ts:" + orderId);
      pipeline.sync();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Check if any reservation has exceeded the cutoff time. If any have, then
   * backout the reservation and return the inventory back to the pool.
   */
  public void expireReservation(String sku, long cutoffTime) {
    long cutoffTimeStamp = System.currentTimeMillis() - cutoffTime;
    String holdKey = KeyHelper.createKey("ticket_hold", sku);
    ScanResult<Entry<String, String>> hScan = jedis.hscan(holdKey, "ts:*");
    for (Entry<String, String> entry : hScan.getResult()) {
      if (Long.parseLong(entry.getKey()) < cutoffTimeStamp) {
        String orderId = entry.getValue().split(":")[0];
        backoutHold(sku, orderId);
      }
    }
  }
}
