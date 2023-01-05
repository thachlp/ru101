import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.entity.Event;
import java.io.File;
import java.io.IOException;
import org.example.FacetedSearch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class FacetedSearchTest {
  private static FacetedSearch facetedSearch;
  private static Event[] events;
  @BeforeAll
  public static void setUp() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    events = mapper.readValue(new File("src/test/resources/events.json"), Event[].class);
    Jedis jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

    if (HostPort.getRedisPassword().length() > 0) {
      jedis.auth(HostPort.getRedisPassword());
    }
    facetedSearch = new FacetedSearch(jedis, mapper);
  }

  @Test
  void matchByInspection() throws JsonProcessingException {
    facetedSearch.createEvent(events);
    Map<String, String> matches = new HashMap<>();
    matches.put("disabled_access", "true");
    List<String> skus = facetedSearch.matchByInspection(matches);
    Assertions.assertEquals(2, skus.size());

    matches = new HashMap<>();
    matches.put("disabled_access", "true");
    matches.put("medal_event", "false");
    skus = facetedSearch.matchByInspection(matches);
    Assertions.assertEquals("737-DEF-911", skus.get(0));
  }
}
