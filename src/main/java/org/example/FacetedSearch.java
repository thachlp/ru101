package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.example.entity.Event;
import org.example.util.KeyHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public class FacetedSearch {

  private final Jedis jedis;
  private final ObjectMapper objectMapper;
  private final String[] lookups = {"disabled_access", "medal_event", "venue"};
  public FacetedSearch(Jedis jedis, ObjectMapper objectMapper) {
    this.jedis = jedis;
    this.objectMapper = objectMapper;
  }

  public void createEvent(Event[] events) throws JsonProcessingException {
    for (Event event : events) {
      String eventKey = KeyHelper.createKey(KeyHelper.PREFIX_EVENT, event.getSku());
      jedis.set(eventKey, objectMapper.writeValueAsString(event));
    }
  }

  /**
   * Find all matching keys, retrieve value and then examine for all matching
   *   attributes.
   */
  public List<String> matchByInspection(Map<String, String> searchFactors)
      throws JsonProcessingException {
    String matchKeys = KeyHelper.createKey(KeyHelper.PREFIX_EVENT, "*");
    List<String> result = new ArrayList<>();
    ScanParams params = new ScanParams();
    params.match(matchKeys);
    params.count(1000);

    ScanResult<String> keys = jedis.scan("0", params);
    for (String key : keys.getResult()) {
      JsonNode node = objectMapper.readTree(jedis.get(key));
      boolean match = true;
      for (var factor : searchFactors.entrySet()) {
        if (!node.get(factor.getKey()).asText().equals(factor.getValue())) {
          match = false;
          break;
        }
      }
      if (match) {
        result.add(node.get("sku").asText());
      }
    }
    return result;
  }

  /**
   * For each attribute & value combination, add the event into a Set
   */
  public void createEventWithLookups(Event[] events) throws JsonProcessingException {
    for (Event event : events) {
      String eventKey = KeyHelper.createKey(KeyHelper.PREFIX_EVENT, event.getSku());
      jedis.set(eventKey, objectMapper.writeValueAsString(event));
      for (String lookup : lookups) {
        String facetedSearchKey = KeyHelper.createKey("fs", lookup, event.lookup(lookup));
        jedis.sadd(facetedSearchKey, event.getSku());
      }
    }
  }

  /**
   * Use SINTER to find the matching elements
   */
  public List<String> matchByFaceTing(Map<String, String> searchFactors) {
    List<String> facets = new ArrayList<>();
    for (var factor : searchFactors.entrySet()) {
      String facetedSearchKey = KeyHelper.createKey("fs", factor.getKey(), factor.getValue());
      facets.add(facetedSearchKey);
    }

    return new ArrayList<>(jedis.sinter(facets.toArray(new String[0])));
  }

  /**
   * Create hashed lookup for each event
   */
  public void createEventsHashedLookups(Event[] events)
      throws JsonProcessingException, NoSuchAlgorithmException {
    for (Event event : events) {
      String eventKey = KeyHelper.createKey(KeyHelper.PREFIX_EVENT, event.getSku());
      jedis.set(eventKey, objectMapper.writeValueAsString(event));
      List<String> hfs = new ArrayList<>();
      for (String lookup : lookups) {
        hfs.add(lookup);
        hfs.add(event.lookup(lookup));
        String hashed = KeyHelper.sha256(hfs.toString());
        String hfsKey = KeyHelper.createKey("hfs", hashed);
        jedis.sadd(hfsKey, event.getSku());
      }
    }
  }

  /**
   * Hashed Faceted Search
   */
  public List<String> matchByHashedFaceTing(Map<String, String> searchFactors)
      throws NoSuchAlgorithmException {
    String hsfKey = "";
    List<String> hfs = new ArrayList<>();
    for (String lookup : lookups) {
        if (searchFactors.containsKey(lookup)) {
          hfs.add(lookup);
          hfs.add(searchFactors.get(lookup));
      }
    }
    String hashed = KeyHelper.sha256(hfs.toString());
    hsfKey = KeyHelper.createKey("hfs", hashed);
    ScanResult<String> scanResult = jedis.sscan(hsfKey, "0");
    return new ArrayList<>(scanResult.getResult());
  }
}
