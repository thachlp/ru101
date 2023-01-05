package org.example.util;

public class KeyHelper {
  private KeyHelper() {
    throw new IllegalStateException("Utility class");
  }
  public static String createKey(String type, String value) {
    return String.format("%s:%s", type, value);
  }
}
