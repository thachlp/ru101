package org.example.util;

public class KeyHelper {
  private KeyHelper() {
    throw new IllegalStateException("Utility class");
  }
  public static String createKey(String ... value) {
    return String.join(":", value);
  }
}
