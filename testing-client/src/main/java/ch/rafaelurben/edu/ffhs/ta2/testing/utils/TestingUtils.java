/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.testing.utils;

import ch.rafaelurben.edu.ffhs.ta2.client.ApiException;

public class TestingUtils {
  private TestingUtils() {
    // Utility class
  }

  @FunctionalInterface
  public interface RunnableThrowing<E extends Exception> {
    void run() throws E;
  }

  public static void sleep(long millis) throws InterruptedException {
    Thread.sleep(millis);
  }

  public static void sleep() throws InterruptedException {
    sleep(100);
  }

  public static void assertEquals(Object expected, Object actual, String message) {
    if (!expected.equals(actual)) {
      throw new AssertionError(message + " Expected: " + expected + ", but got: " + actual);
    }
  }

  public static void assert404(RunnableThrowing<ApiException> action, String message)
      throws ApiException {
    try {
      action.run();
      throw new AssertionError(message);
    } catch (ApiException e) {
      if (e.getCode() != 404) {
        throw e;
      }
    }
  }
}
