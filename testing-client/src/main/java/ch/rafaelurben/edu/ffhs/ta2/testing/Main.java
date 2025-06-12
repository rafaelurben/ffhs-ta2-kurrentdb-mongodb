/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiClient;
import ch.rafaelurben.edu.ffhs.ta2.client.JSON;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import lombok.extern.slf4j.Slf4j;

/** Main entry point for testing the API implementations. */
@Slf4j
public class Main {
  /**
   * Sets up the Gson configuration for the API client. This method configures the date-time format
   * to handle OffsetDateTime correctly.
   */
  private static void setupGson() {
    JSON.setOffsetDateTimeFormat(
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .parseLenient()
            .parseDefaulting(java.time.temporal.ChronoField.OFFSET_SECONDS, 0)
            .toFormatter());
  }

  public static void main(String[] args) {
    setupGson();

    ApiClient client1 = new ApiClient();
    client1.setBasePath("http://localhost:8181/api/v1");

    ApiClient client2 = new ApiClient();
    client2.setBasePath("http://localhost:8182/api/v1");

    TestRunner.testFunctionality(client1, "Implementation 1 (MongoDB)");
    TestRunner.testFunctionality(client2, "Implementation 2 (KurrentDB)");

    TestRunner.testPerformance(client1, "Implementation 1 (MongoDB)");
    TestRunner.testPerformance(client2, "Implementation 2 (KurrentDB)");
  }
}
