import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.NoSuchAlgorithmException;
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

  /**
   * [disabled_access, true]
   * hfs:d0852c46b74c3e22c2918b864ce048b83ae79484605ccf8e734b7a6698729a20  123-ABC-723 737-DEF-911
   * ------------------------------------------------------------------
   * [disabled_access, true, medal_event, true]
   * hfs:23035b3964a4a5774bde503329f658efbad34d5706d61d4c25f0b73e95fadda4  123-ABC-723
   * ------------------------------------------------------------------
   * [disabled_access, true, medal_event, true, venue, Olympic Stadium]
   * hfs:275d0be4a79e9a059e6fcf10299ba1dd4905ff8221dfde6f64b07e19a920f914  123-ABC-723
   * ------------------------------------------------------------------
   * [disabled_access, true, medal_event, false]
   * hfs:e40811a71bf9c435bba64601471dec539391c184460568a13f9de79c6e7e94b4  737-DEF-911
   * ------------------------------------------------------------------
   * [disabled_access, true, medal_event, false, venue, Olympic Stadium]
   * hfs:1fd1c61bec241104861709eb3b15636c446658bcf085624cc968633292177bed  737-DEF-911
   * ------------------------------------------------------------------
   * [disabled_access, false]
   * hfs:67a99c3c63e823217f2a74a2d57c40ee6aa7aa0dfcb39ed8b2a9bbd5e4f96bd4  320-GHI-921
   * ------------------------------------------------------------------
   * [disabled_access, false, medal_event, false]
   * hfs:7d7a814bfa454595d91e753febb5cc793f019d82d156424b30a5ddd962a76f05  320-GHI-921
   * ------------------------------------------------------------------
   * [disabled_access, false, medal_event, false, venue, Nippon Budokan]
   * hfs:614c0162a1387c74bb4597c413c3be1d4fe8b6c32434dbe2a779d265715a8b97  320-GHI-921
   */
  @Test
  void matchByHashedFaceting() throws JsonProcessingException, NoSuchAlgorithmException {
    facetedSearch.createEventsHashedLookups(events);
    Map<String, String> matches = new HashMap<>();
    matches.put("disabled_access", "true");
    List<String> skus = facetedSearch.matchByHashedFaceTing(matches);
    Assertions.assertEquals(2, skus.size());

    matches = new HashMap<>();
    matches.put("disabled_access", "true");
    matches.put("medal_event", "false");
    skus = facetedSearch.matchByHashedFaceTing(matches);
    Assertions.assertEquals("737-DEF-911", skus.get(0));

    matches = new HashMap<>();
    matches.put("disabled_access", "false");
    matches.put("medal_event", "false");
    matches.put("venue", "Nippon Budokan");
    skus = facetedSearch.matchByHashedFaceTing(matches);
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
