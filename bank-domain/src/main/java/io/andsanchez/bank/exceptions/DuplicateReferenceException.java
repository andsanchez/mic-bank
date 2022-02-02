package io.andsanchez.bank.exceptions;

public class DuplicateReferenceException extends ValidationException {

  private static final long serialVersionUID = 1L;

  private static final String MESSAGE = "Reference %s already exists. The transaction has not been created.";

  public DuplicateReferenceException(final String reference) {
    super(String.format(MESSAGE, reference));
  }

}
