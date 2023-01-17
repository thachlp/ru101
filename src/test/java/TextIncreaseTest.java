import org.example.util.TextIncrease;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TextIncreaseTest {
  @Test
  void increaseChar() {
    Assertions.assertEquals('B', TextIncrease.increaseChar('A'));
    Assertions.assertEquals('A', TextIncrease.increaseChar('Z'));
  }

}
