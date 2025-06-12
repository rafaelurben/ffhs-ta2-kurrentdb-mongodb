/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl2kurrentdb.exceptions;

public class KurrentException extends RuntimeException {
  public KurrentException(Throwable cause) {
    super(cause);
  }

  public KurrentException(String message, Throwable cause) {
    super(message, cause);
  }
}
