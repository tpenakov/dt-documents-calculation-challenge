package design.technologies.api.core.exception;

/*
 * Created by triphon 12.11.22 г.
 */
public class InvalidInputException extends RuntimeException {
  public InvalidInputException() {
    super();
  }

  public InvalidInputException(final String message) {
    super(message);
  }

  public InvalidInputException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidInputException(final Throwable cause) {
    super(cause);
  }

  protected InvalidInputException(
          final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
