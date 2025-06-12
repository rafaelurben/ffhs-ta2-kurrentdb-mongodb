/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing.functionality;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiClient;
import ch.rafaelurben.edu.ffhs.ta2.client.ApiException;
import ch.rafaelurben.edu.ffhs.ta2.client.api.StatusApi;
import ch.rafaelurben.edu.ffhs.ta2.client.model.ConnectionTestResponseDto;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StatusTest {

  private StatusTest() {
    // Utility class
  }

  public static void testStatusSuccessful(ApiClient client) throws ApiException {
    StatusApi api = new StatusApi(client);

    ConnectionTestResponseDto status = api.testConnection();
    String expectedMessage = "Connection successful";
    String actualMessage = status.getMessage();

    if (!Objects.equals(actualMessage, expectedMessage)) {
      log.error("Expected status message '{}', but got '{}'", expectedMessage, actualMessage);
      throw new AssertionError(
          "Status message mismatch: expected '"
              + expectedMessage
              + "', got '"
              + actualMessage
              + "'");
    }

    log.info("Status is successful: {}", actualMessage);
  }
}
