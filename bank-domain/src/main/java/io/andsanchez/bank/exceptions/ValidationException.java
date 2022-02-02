package io.andsanchez.bank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ValidationException(final String message) {
    super(message);
  }

}
