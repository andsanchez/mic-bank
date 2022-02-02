package io.andsanchez.bank.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import io.andsanchez.bank.entity.Transaction;
import io.andsanchez.bank.entity.Transaction.TransactionBuilder;
import io.andsanchez.bank.exceptions.DuplicateReferenceException;
import io.andsanchez.bank.repository.TransactionRepository;
import io.andsanchez.bank.validator.EntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseImplTest {

  private static final String NULL_REFERENCE = null;

  private static final String SOME_REFERENCE = "658c85a4c47311eb85290242ac130003";

  private static final String SOME_IBAN = "ES9820385778983000760236";

  private static final Double SOME_AMOUNT = 193.38D;

  private static final Double SOME_FEE = 3.18D;

  private static final String SOME_DESCRIPTION = "Hotel Payment";

  private static final long SOME_EPOCH_DATE = 1643652850L;

  @InjectMocks
  private CreateTransactionUseCaseImpl useCase;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private EntityValidator entityValidator;

  @Test
  void executeCreateTransaction() {
    // Arrange
    final Transaction someTransaction = this.createTransaction().build();
    final ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
    final PublisherProbe<Boolean> existsByIdProbe = PublisherProbe.of(Mono.just(false));
    when(this.transactionRepository.existsById(transactionArgumentCaptor.capture())).thenReturn(existsByIdProbe.mono());
    final PublisherProbe<Transaction> validatorProbe = PublisherProbe.of(Mono.just(someTransaction));
    when(this.entityValidator.validate(transactionArgumentCaptor.capture())).thenReturn(validatorProbe.mono());
    final PublisherProbe<Transaction> saveProbe = PublisherProbe.of(Mono.just(someTransaction));
    when(this.transactionRepository.save(transactionArgumentCaptor.capture())).thenReturn(saveProbe.mono());

    // Act
    final Mono<Transaction> result = this.useCase.execute(someTransaction);

    // Assert
    StepVerifier.create(result)
        .expectNext(someTransaction)
        .verifyComplete();
    existsByIdProbe.wasRequested();
    validatorProbe.wasRequested();
    saveProbe.wasRequested();
    final Transaction transactionToSave = transactionArgumentCaptor.getValue();
    this.assertCommonTransaction(transactionToSave);
    assertThat(transactionToSave.getReference()).isEqualTo(SOME_REFERENCE);
  }

  @Test
  void executeCreateTransactionShouldGenerateReference() {
    // Arrange
    final Transaction someTransaction = this.createTransaction().reference(NULL_REFERENCE).build();
    final PublisherProbe<Boolean> existsByIdProbe = PublisherProbe.of(Mono.just(false));
    when(this.transactionRepository.existsById(any(Transaction.class))).thenReturn(existsByIdProbe.mono());
    Answer<Mono<Transaction>> transactionMonoAnswer = invocation -> Mono.just((Transaction) invocation.getArguments()[0]);
    doAnswer(transactionMonoAnswer).when(this.entityValidator).validate(any(Transaction.class));
    doAnswer(transactionMonoAnswer).when(this.transactionRepository).save(any(Transaction.class));

    // Act
    final Mono<Transaction> result = this.useCase.execute(someTransaction);

    // Assert
    StepVerifier.create(result)
        .assertNext(transaction -> {
          this.assertCommonTransaction(transaction);
          assertThat(transaction.getReference()).isNotBlank().hasSize(32);
        })
        .verifyComplete();
    existsByIdProbe.wasRequested();
  }

  @Test
  void executeCreateTransactionShouldReturnDuplicateReferenceException() {
    // Arrange
    final Transaction someTransaction = this.createTransaction().build();
    final PublisherProbe<Boolean> existsByIdProbe = PublisherProbe.of(Mono.just(true));
    when(this.transactionRepository.existsById(any(Transaction.class))).thenReturn(existsByIdProbe.mono());
    final PublisherProbe<Transaction> validatorProbe = PublisherProbe.of(Mono.just(someTransaction));
    lenient().when(this.entityValidator.validate(any(Transaction.class))).thenReturn(validatorProbe.mono());
    final PublisherProbe<Transaction> saveProbe = PublisherProbe.of(Mono.just(someTransaction));
    lenient().when(this.transactionRepository.save(any(Transaction.class))).thenReturn(saveProbe.mono());

    // Act
    final Mono<Transaction> result = this.useCase.execute(someTransaction);

    // Assert
    StepVerifier.create(result)
        .expectError(DuplicateReferenceException.class)
        .verify();
    existsByIdProbe.wasRequested();
    validatorProbe.assertWasNotRequested();
    saveProbe.assertWasNotRequested();
  }

  private void assertCommonTransaction(final Transaction transactionToSave) {
    assertThat(transactionToSave)
        .isNotNull()
        .hasFieldOrPropertyWithValue("accountIban", SOME_IBAN)
        .hasFieldOrPropertyWithValue("date", SOME_EPOCH_DATE)
        .hasFieldOrPropertyWithValue("amount", SOME_AMOUNT)
        .hasFieldOrPropertyWithValue("fee", SOME_FEE)
        .hasFieldOrPropertyWithValue("description", SOME_DESCRIPTION);
  }

  private TransactionBuilder createTransaction() {
    return Transaction.builder()
        .reference(SOME_REFERENCE)
        .accountIban(SOME_IBAN)
        .date(SOME_EPOCH_DATE)
        .amount(SOME_AMOUNT)
        .fee(SOME_FEE)
        .description(SOME_DESCRIPTION);
  }
}