public class HostPort {
  private static final String defaultHost = "localhost";
  private static final Integer defaultPort = 6379;
  private static final String defaultPassword = "";

  private HostPort() {
  }

  public static String getRedisHost() {
    return defaultHost;
  }

  public static Integer getRedisPort() {
    return defaultPort;
  }

  public static String getRedisPassword() {
    return defaultPassword;
  }

}
