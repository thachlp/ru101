import static org.awaitility.Awaitility.await;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

class JedisBasicsTest {

  private static Jedis jedis;
  @BeforeEach
  public void setUp() {
    jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

    if (!HostPort.getRedisPassword().isEmpty()) {
      jedis.auth(HostPort.getRedisPassword());
    }
    jedis.del("hello");
    jedis.del("customers:001");
    jedis.del("products:yoyo:001");
    jedis.del("collections:001");
    jedis.del("leaderboards");

  }

  @Test
  void hello() {
    final String key = "hello";
    final String result = jedis.set(key, "world");
    Assertions.assertEquals("OK", result);
    final String value = jedis.get(key);
    Assertions.assertEquals("world", value);

  }

  @Test
  void redisString() {
    final String key = "customers:001";
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
    final String key = "products:yoyo:001";
    final Map<String, String> productProperties = new HashMap<>();
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
    final String availableStock = jedis.hget(key, "YY1000");
    Assertions.assertEquals("19", availableStock);
  }

  @Test
  void redisList() {
    final String key = "collections:001";
    final String[] productNames = {"Hat", "T-Shirt", "Shirt", "Jean"};
    jedis.rpush(key, productNames);
    final long total = jedis.llen(key);
    Assertions.assertEquals(4, total);
    final String firstElement = jedis.lpop(key);
    Assertions.assertEquals("Hat", firstElement);
    final String lastElement = jedis.rpop(key);
    Assertions.assertEquals("Jean", lastElement);
  }

  @Test
  void redisSet() {
    final String key = "collections:001";
    final String[] productNames = {"Hat", "T-Shirt", "Shirt", "Jean", "Jean"};
    jedis.sadd(key, productNames);
    final long count = jedis.scard(key);
    Assertions.assertEquals(4, count);
    final boolean hat = jedis.sismember(key, "Hat");
    Assertions.assertTrue(hat);
  }

  @Test
  void redisSortedSet() {
    final String key = "leaderboards";
    jedis.zadd(key, 3, "Alan");
    jedis.zadd(key, 9, "Den");
    jedis.zadd(key, 4, "Del");
    jedis.zadd(key, 1, "Phuong");
    jedis.zadd(key, 8, "Thanh");
    jedis.zadd(key, 5, "Brand");
    final List<Tuple> tuples = jedis.zrevrangeByScoreWithScores(key, 10, 5);
    Assertions.assertEquals(3, tuples.size());
    Assertions.assertEquals(9, tuples.get(0).getScore());
  }

  @AfterAll
  public static void tearDown() {
    jedis.close();
  }
}
