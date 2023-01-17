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

}
