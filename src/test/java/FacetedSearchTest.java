import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.entity.Event;
import java.io.File;
import java.io.IOException;
import org.example.FacetedSearch;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class FacetedSearchTest {
  private static FacetedSearch facetedSearch;
  private static Event[] events;

  private static Jedis jedis;
  @BeforeAll
  public static void setUp() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    events = mapper.readValue(new File("src/test/resources/events.json"), Event[].class);
    jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

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

    matches = new HashMap<>();
    matches.put("disabled_access", "false");
    matches.put("medal_event", "false");
    matches.put("venue", "Nippon Budokan");
    skus = facetedSearch.matchByInspection(matches);
    Assertions.assertEquals("320-GHI-921", skus.get(0));
  }

  /**
   * fs:disabled_access:true   123-ABC-723  737-DEF-911
   * fs:disabled_access:false   320-GHI-921
   * fs:medal_event:true   123-ABC-723
   * fs:medal_event:false   737-DEF-911  320-GHI-921
   * fs:venue:Olympic Stadium   123-ABC-723  737-DEF-911
   * fs:venue:Nippon Budokan   320-GHI-921
   */
  @Test
  void matchByFaceting() throws JsonProcessingException {
    facetedSearch.createEventWithLookups(events);
    Map<String, String> matches = new HashMap<>();
    matches.put("disabled_access", "true");
    List<String> skus = facetedSearch.matchByFaceTing(matches);
    Assertions.assertEquals(2, skus.size());

    matches = new HashMap<>();
    matches.put("disabled_access", "true");
    matches.put("medal_event", "false");
    skus = facetedSearch.matchByFaceTing(matches);
    Assertions.assertEquals("737-DEF-911", skus.get(0));

    matches = new HashMap<>();
    matches.put("disabled_access", "false");
    matches.put("medal_event", "false");
    matches.put("venue", "Nippon Budokan");
    skus = facetedSearch.matchByFaceTing(matches);
    Assertions.assertEquals("320-GHI-921", skus.get(0));
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
