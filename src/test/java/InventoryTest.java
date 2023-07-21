import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Inventory;
import org.example.entity.Customer;
import org.example.entity.EventInventory;
import org.example.util.KeyHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class InventoryTest {
  private static Inventory inventory;
  private static EventInventory[] eventInventories;

  private static Customer[] customers;

  private static Jedis jedis;

  @BeforeAll
  public static void setUp() throws IOException {
    final ObjectMapper mapper = new ObjectMapper();
    eventInventories = mapper.readValue(new File("src/test/resources/event_inventories.json"), EventInventory[].class);
    customers = mapper.readValue(new File("src/test/resources/customers.json"), Customer[].class);
    jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());
    if (!HostPort.getRedisPassword().isEmpty()) {
      jedis.auth(HostPort.getRedisPassword());
    }
    inventory = new Inventory(jedis);
  }

  @Test
  void checkAvailabilityAndPurchase() {
    final String orderId =  UUID.randomUUID().toString();
    inventory.checkAvailabilityAndPurchase(customers[0], "123-ABC-723", 5, "General", orderId);
    final String orderKey = KeyHelper.createKey("sales_order", orderId);
    final String cost = jedis.hget(orderKey, "cost");
    Assertions.assertEquals("125.0", cost);
  }

  @Test
  void reserve() {
    final String orderId =  UUID.randomUUID().toString();
    final String sku = "737-DEF-911";
    inventory.reserve(customers[0], sku, 5, "General", orderId);
    final String holdKey = KeyHelper.createKey("ticket_hold", sku);
    final int quantity = Integer.parseInt(jedis.hget(holdKey, "qty:" + orderId));
    Assertions.assertEquals(5, quantity);
  }

  @BeforeEach
  void init() {
    inventory.createEvent(eventInventories, 20000, 25.00, "General");
    inventory.createCustomers(customers);

    final String skuKeys = KeyHelper.createKey("events");
    final long numOfElements = jedis.scard(skuKeys);
    final String customerKey = KeyHelper.createKey("customer", "bill");
    final String customerName = jedis.hget(customerKey, "customer_name");

    Assertions.assertEquals(3, numOfElements);
    Assertions.assertEquals("bill smith", customerName);
  }

  @AfterEach
  void clear() {
    jedis.flushDB();
  }

  @AfterAll
  static void tearDown() {
    jedis.close();
  }
}
