import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

    jedis.del("planets");
    jedis.del("earth");
  }

  @Test
  void hello() {
    String result = jedis.set("hello", "world");
    Assertions.assertEquals("OK", result);
    String value = jedis.get("hello");
    Assertions.assertEquals("world", value);

  }

  @AfterAll
  public static void tearDown() {
    jedis.close();
  }
}
