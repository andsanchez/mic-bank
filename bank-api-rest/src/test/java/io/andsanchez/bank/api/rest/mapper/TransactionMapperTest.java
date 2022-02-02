package io.andsanchez.bank.api.rest.mapper;

import static io.andsanchez.bank.api.rest.support.TransactionFactory.SOME_AMOUNT;
import static io.andsanchez.bank.api.rest.support.TransactionFactory.SOME_DESCRIPTION;
import static io.andsanchez.bank.api.rest.support.TransactionFactory.SOME_EPOCH_DATE;
import static io.andsanchez.bank.api.rest.support.TransactionFactory.SOME_FEE;
import static io.andsanchez.bank.api.rest.support.TransactionFactory.SOME_IBAN;
import static io.andsanchez.bank.api.rest.support.TransactionFactory.SOME_REFERENCE;
import static io.andsanchez.bank.api.rest.support.TransactionFactory.createTransaction;
import static io.andsanchez.bank.api.rest.support.TransactionFactory.createTransactionDto;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import io.andsanchez.bank.api.rest.model.TransactionDto;
import io.andsanchez.bank.entity.Transaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TransactionMapperTest {

  private static final OffsetDateTime NULL_DATE = null;

  private final TransactionMapper mapper = new TransactionMapperImpl();

  @Test
  void mapAsTransaction() {
    final TransactionDto transactionDto = createTransactionDto();

    final Transaction transaction = this.mapper.asTransaction(transactionDto);

    Assertions.assertThat(transaction)
        .isNotNull()
        .hasNoNullFieldsOrProperties()
        .hasFieldOrPropertyWithValue("reference", SOME_REFERENCE)
        .hasFieldOrPropertyWithValue("accountIban", SOME_IBAN)
        .hasFieldOrPropertyWithValue("date", SOME_EPOCH_DATE)
        .hasFieldOrPropertyWithValue("amount", SOME_AMOUNT)
        .hasFieldOrPropertyWithValue("fee", SOME_FEE)
        .hasFieldOrPropertyWithValue("description", SOME_DESCRIPTION);
  }

  @Test
  void mapAsTransactionWithoutDate() {
    final TransactionDto transactionDto = createTransactionDto();
    transactionDto.setDate(NULL_DATE);

    final Transaction transaction = this.mapper.asTransaction(transactionDto);

    Assertions.assertThat(transaction)
        .isNotNull()
        .hasFieldOrPropertyWithValue("reference", SOME_REFERENCE)
        .hasFieldOrPropertyWithValue("accountIban", SOME_IBAN)
        .hasFieldOrPropertyWithValue("date", NULL_DATE)
        .hasFieldOrPropertyWithValue("amount", SOME_AMOUNT)
        .hasFieldOrPropertyWithValue("fee", SOME_FEE)
        .hasFieldOrPropertyWithValue("description", SOME_DESCRIPTION);
  }

  @Test
  void mapAsTransactionDto() {
    Transaction transaction = createTransaction().build();

    final TransactionDto transactionDto = this.mapper.asTransactionDto(transaction);

    Assertions.assertThat(transactionDto)
        .isNotNull()
        .hasNoNullFieldsOrProperties()
        .hasFieldOrPropertyWithValue("reference", SOME_REFERENCE)
        .hasFieldOrPropertyWithValue("accountIban", SOME_IBAN)
        .hasFieldOrPropertyWithValue("amount", SOME_AMOUNT)
        .hasFieldOrPropertyWithValue("fee", SOME_FEE)
        .hasFieldOrPropertyWithValue("description", SOME_DESCRIPTION);
    Assertions.assertThat(transactionDto.getDate())
        .isEqualTo("2022-01-31T18:14:10Z");

  }

  @Test
  void mapAsTransactionDtoList() {
    List<Transaction> transactions = List.of(createTransaction().build(), createTransaction().build());

    final List<TransactionDto> transactionDtoList = this.mapper.asTransactionDtoList(transactions);

    Assertions.assertThat(transactionDtoList)
        .hasSize(2).first()
        .hasNoNullFieldsOrProperties()
        .hasFieldOrPropertyWithValue("reference", SOME_REFERENCE)
        .hasFieldOrPropertyWithValue("accountIban", SOME_IBAN)
        .hasFieldOrPropertyWithValue("amount", SOME_AMOUNT)
        .hasFieldOrPropertyWithValue("fee", SOME_FEE)
        .hasFieldOrPropertyWithValue("description", SOME_DESCRIPTION)
        .returns(OffsetDateTime.ofInstant(Instant.ofEpochMilli(SOME_EPOCH_DATE), ZoneOffset.UTC), TransactionDto::getDate);
  }

}