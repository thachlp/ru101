package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  public FacetedSearch(Jedis jedis, ObjectMapper objectMapper) {
    this.jedis = jedis;
    this.objectMapper = objectMapper;
  }

  public void createEvent(Event[] events) throws JsonProcessingException {
    for (Event event : events) {
      String eventKey = KeyHelper.createKey("event", event.getSku());
      jedis.set(eventKey, objectMapper.writeValueAsString(event));
    }
  }

  public List<String> matchByInspection(Map<String, String> searchFactors)
      throws JsonProcessingException {
    String matchKeys = KeyHelper.createKey("event", "*");
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
}
