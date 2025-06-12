/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing;

import static ch.rafaelurben.edu.ffhs.ta2.testing.utils.MetricsUtils.exportMetricsToCsv;
import static ch.rafaelurben.edu.ffhs.ta2.testing.utils.TestingUtils.sleep;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiClient;
import ch.rafaelurben.edu.ffhs.ta2.client.ApiException;
import ch.rafaelurben.edu.ffhs.ta2.testing.functionality.Example1Test;
import ch.rafaelurben.edu.ffhs.ta2.testing.functionality.Example2Test;
import ch.rafaelurben.edu.ffhs.ta2.testing.functionality.StatusTest;
import ch.rafaelurben.edu.ffhs.ta2.testing.performance.ManyChildrenTest;
import ch.rafaelurben.edu.ffhs.ta2.testing.performance.ManyParentsTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestRunner {
  private TestRunner() {
    // Utility class
  }

  public static void testFunctionality(ApiClient client, String label) {
    try {
      log.info("STARTING tests for {} at {}", label, client.getBasePath());
      log.info("Testing status of {}", label);
      StatusTest.testStatusSuccessful(client);

      log.info("Running example 1 tests for {}", label);
      Example1Test.test(client);

      log.info("Running example 2 tests for {}", label);
      Example2Test.test(client);

      log.info("ALL TESTS PASSED for {}", label);
    } catch (ApiException | AssertionError e) {
      log.error("TESTS FAILED for {}:", label, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // Restore interrupted status
      log.error("TESTS INTERRUPTED for {}:", label, e);
    } catch (Exception e) {
      log.error("Unexpected error during tests for {}:", label, e);
    }
  }

  public static void testPerformance(ApiClient client, String label) {
    try {
      log.info("STARTING performance tests for {} at {}", label, client.getBasePath());

      sleep(500); // Ensure some delay before starting performance tests

      log.info("Running ManyChildren performance test for {}", label);
      var resultManyChildren = ManyChildrenTest.run(client);
      exportMetricsToCsv("ManyChildren - " + label, resultManyChildren);

      sleep(500); // Ensure some delay between tests

      log.info("Running ManyParents performance test for {}", label);
      var resultManyParents = ManyParentsTest.run(client);
      exportMetricsToCsv("ManyParents - " + label, resultManyParents);

      log.info("PERFORMANCE TESTS COMPLETED for {}", label);
    } catch (ApiException | AssertionError e) {
      log.error("PERFORMANCE TESTS FAILED for {}:", label, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // Restore interrupted status
      log.error("TESTS INTERRUPTED for {}:", label, e);
    } catch (Exception e) {
      log.error("Unexpected error during performance tests for {}:", label, e);
    }
  }
}
