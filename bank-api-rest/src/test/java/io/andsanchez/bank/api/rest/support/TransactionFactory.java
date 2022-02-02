package io.andsanchez.bank.api.rest.support;

import java.time.Instant;
import java.time.ZoneOffset;

import io.andsanchez.bank.api.rest.model.TransactionDto;
import io.andsanchez.bank.entity.Transaction;

public class TransactionFactory {

  public static final String SOME_REFERENCE = "12345A";

  public static final String SOME_IBAN = "ES9820385778983000760236";

  public static final long SOME_EPOCH_DATE = 1643652850000L;

  public static final Double SOME_AMOUNT = 193.38D;

  public static final Double SOME_FEE = 3.18D;

  public static final String SOME_DESCRIPTION = "Some Description";

  public static Transaction.TransactionBuilder createTransaction() {
    return Transaction.builder()
        .reference(SOME_REFERENCE)
        .accountIban(SOME_IBAN)
        .date(SOME_EPOCH_DATE)
        .amount(SOME_AMOUNT)
        .fee(SOME_FEE)
        .description(SOME_DESCRIPTION);
  }

  public static TransactionDto createTransactionDto() {
    return new TransactionDto()
        .reference(SOME_REFERENCE)
        .accountIban(SOME_IBAN)
        .date(Instant.ofEpochMilli(SOME_EPOCH_DATE).atOffset(ZoneOffset.UTC))
        .amount(SOME_AMOUNT)
        .fee(SOME_FEE)
        .description(SOME_DESCRIPTION);
  }

}
