public class HostPort {
  private final static String defaultHost = "localhost";
  private final static Integer defaultPort = 6379;
  private final static String defaultPassword = "";

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
