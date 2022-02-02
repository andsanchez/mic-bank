package io.andsanchez.bank.usecase;

import static org.mockito.Mockito.when;

import java.util.List;

import io.andsanchez.bank.entity.Transaction;
import io.andsanchez.bank.repository.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

@ExtendWith(MockitoExtension.class)
class SearchTransactionsUseCaseImplTest {

  private static final String SOME_IBAN = "ES9820385778983000760236";

  private static final boolean SOME_SORT = true;

  @InjectMocks
  private SearchTransactionsUseCaseImpl useCase;

  @Mock
  private TransactionRepository transactionRepository;

  @Test
  void executeSearchTransactions() {
    // Arrange
    final Transaction someTransaction = Transaction.builder().build();
    final PublisherProbe<List<Transaction>> repositoryProbe = PublisherProbe.of(Mono.just(List.of(someTransaction)));
    when(this.transactionRepository.findByAccountIban(SOME_IBAN, SOME_SORT)).thenReturn(repositoryProbe.mono());

    // Act
    final Mono<List<Transaction>> result = this.useCase.execute(SOME_IBAN, SOME_SORT);

    // Assert
    StepVerifier.create(result)
        .assertNext(transactions -> Assertions.assertThat(transactions).containsExactly(someTransaction))
        .verifyComplete();
    repositoryProbe.assertWasRequested();
  }

}