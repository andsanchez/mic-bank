package io.andsanchez.bank.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.andsanchez.bank.entity.Transaction;
import io.andsanchez.bank.infrastructure.repository.mongodb.TransactionDocument;
import io.andsanchez.bank.infrastructure.repository.mongodb.TransactionDocumentMapper;
import io.andsanchez.bank.infrastructure.repository.mongodb.TransactionMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryImplTest {

  private static final String SOME_REFERENCE = "658c85a4c47311eb85290242ac130003";

  private static final String SOME_IBAN = "ES9820385778983000760236";

  private static final Double SOME_AMOUNT = 193.38D;

  private static final Double SOME_FEE = 3.18D;

  private static final String SOME_DESCRIPTION = "Hotel Payment";

  private static final long SOME_EPOCH_DATE = 1643652850L;

  @Mock
  private TransactionMongoRepository mongoRepository;

  @Mock
  private TransactionDocumentMapper transactionDocumentMapper;

  @InjectMocks
  private TransactionRepositoryImpl transactionRepository;

  @Test
  void saveNewTransactionSuccess() {
    // Arrange
    final ArgumentCaptor<TransactionDocument> transactionDocumentArgumentCaptor = ArgumentCaptor.forClass(TransactionDocument.class);
    final var transactionEntity = this.createTransaction();
    final var transactionDocument = this.createTransactionDocument();
    when(this.transactionDocumentMapper.asTransactionDocument(transactionEntity)).thenReturn(transactionDocument);
    when(this.transactionDocumentMapper.asTransaction(transactionDocument)).thenReturn(transactionEntity);
    when(this.mongoRepository.save(transactionDocumentArgumentCaptor.capture())).thenReturn(Mono.just(transactionDocument));

    // Act
    final Mono<Transaction> result = this.transactionRepository.save(transactionEntity);

    // Assert
    StepVerifier.create(result)
        .assertNext(transaction -> assertThat(transaction)
            .isEqualTo(transactionEntity))
        .verifyComplete();

    assertThat(transactionDocumentArgumentCaptor.getValue())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(transactionDocument);
  }

  private Transaction createTransaction() {
    return Transaction.builder()
        .reference(SOME_REFERENCE)
        .accountIban(SOME_IBAN)
        .date(SOME_EPOCH_DATE)
        .amount(SOME_AMOUNT)
        .fee(SOME_FEE)
        .description(SOME_DESCRIPTION)
        .build();
  }

  private TransactionDocument createTransactionDocument() {
    return TransactionDocument.builder()
        .reference(SOME_REFERENCE)
        .accountIban(SOME_IBAN)
        .date(SOME_EPOCH_DATE)
        .amount(SOME_AMOUNT)
        .fee(SOME_FEE)
        .description(SOME_DESCRIPTION)
        .build();
  }
}