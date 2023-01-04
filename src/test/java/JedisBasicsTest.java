import static org.awaitility.Awaitility.await;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

class JedisBasicsTest {

  private static Jedis jedis;
  @BeforeAll
  public static void setUp() {
    jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

    if (HostPort.getRedisPassword().length() > 0) {
      jedis.auth(HostPort.getRedisPassword());
    }
    jedis.del("hello");
    jedis.del("customers:001");
    jedis.del("products:yoyo:001");
    jedis.del("collections:001");
  }

  @Test
  void hello() {
    String key = "hello";
    String result = jedis.set(key, "world");
    Assertions.assertEquals("OK", result);
    String value = jedis.get(key);
    Assertions.assertEquals("world", value);

  }

  @Test
  void redisString() {
    String key = "customers:001";
    jedis.set(key, "1");
    jedis.incr(key);
    Assertions.assertEquals("2", jedis.get(key));

    jedis.expire(key, 5);
    Assertions.assertTrue(jedis.exists(key));
    await().atLeast(5, TimeUnit.SECONDS);
    Assertions.assertTrue(jedis.exists(key));
  }

  @Test
  void redisHash() {
    String key = "products:yoyo:001";
    Map<String, String> productProperties = new HashMap<>();
    productProperties.put("name", "Yoyo");
    productProperties.put("price", "148.000");
    productProperties.put("YY1000", "20");
    productProperties.put("YY1001", "20");
    productProperties.put("YY1002", "20");
    for (var property : productProperties.entrySet()) {
      jedis.hset(key, property.getKey(), property.getValue());
    }

    Map<String, String> storedProperties = jedis.hgetAll(key);
    Assertions.assertEquals(productProperties, storedProperties);
    jedis.del(key);

    jedis.hset(key, productProperties);
    storedProperties = jedis.hgetAll(key);
    Assertions.assertEquals(productProperties, storedProperties);

    jedis.hincrBy(key, "YY1000", -1L);
    String availableStock = jedis.hget(key, "YY1000");
    Assertions.assertEquals("19", availableStock);
  }

  @Test
  void redisList() {
    String key = "collections:001";
    String[] productNames = {"Hat", "T-Shirt", "Shirt", "Jean"};
    jedis.rpush(key, productNames);
    long total = jedis.llen(key);
    Assertions.assertEquals(4, total);
    String firstElement = jedis.lpop(key);
    Assertions.assertEquals("Hat", firstElement);
    String lastElement = jedis.rpop(key);
    Assertions.assertEquals("Jean", lastElement);
  }

  @AfterAll
  public static void tearDown() {
    jedis.close();
  }
}
