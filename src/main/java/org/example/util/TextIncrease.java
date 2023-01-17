package org.example.util;

public class TextIncrease {

  /**
   * Increment a character, from 'Z' to 'A'
   */
  public static char increaseChar(char c) {
    if (c == 'Z') {
      return 'A';
    }
    return (char) (c + 1);
  }

  /**
   * Increment a string, including wrapping from 'Z' to 'AA' etc.
   */
  public static String increaseString(String s) {
    int n = s.length();
    int zEnd = 0;
    for (int i = n - 1; i >= 0; i--) {
      if (s.charAt(i) == 'Z') {
        zEnd++;
      }
    }
    s = s.substring(0, n - zEnd);
    int numReplacements = n - zEnd;
    char first = s.charAt(0);
    s = s.substring(1);
    s = increaseChar(first) + s;
    while (numReplacements > 0) {
      s = s + "AA";
      numReplacements--;
    }
    return s;
  }

}
