import static org.awaitility.Awaitility.await;

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
  }

  @Test
  void hello() {
    String result = jedis.set("hello", "world");
    Assertions.assertEquals("OK", result);
    String value = jedis.get("hello");
    Assertions.assertEquals("world", value);

  }

  @Test
  void redisString() {
    String key = "customer:001";
    jedis.set(key, "1");
    jedis.incr(key);
    Assertions.assertEquals("2", jedis.get(key));

    jedis.expire(key, 5);
    Assertions.assertTrue(jedis.exists(key));
    await().atLeast(5, TimeUnit.SECONDS);
    Assertions.assertTrue(jedis.exists(key));
  }

  @AfterAll
  public static void tearDown() {
    jedis.close();
  }
}
