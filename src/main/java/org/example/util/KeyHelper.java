package org.example.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyHelper {
  private KeyHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static final String PREFIX_EVENT = "event";

  public static String createKey(String ... value) {
    return String.join(":", value);
  }

  public static String sha256(String key) throws NoSuchAlgorithmException {
    final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
    final byte[] hashBytes = digest.digest(
        key.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(hashBytes);
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte h : hash) {
      String hex = Integer.toHexString(0xff & h);
      if (hex.length() == 1)
        hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
