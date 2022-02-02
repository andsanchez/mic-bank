package io.andsanchez.bank.api.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import io.andsanchez.bank.api.rest.mapper.TransactionMapper;
import io.andsanchez.bank.api.rest.model.TransactionDto;
import io.andsanchez.bank.api.rest.support.TransactionFactory;
import io.andsanchez.bank.entity.Transaction;
import io.andsanchez.bank.usecase.CreateTransactionUseCase;
import io.andsanchez.bank.usecase.SearchTransactionsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

  public static final boolean SOME_SORT = true;

  @InjectMocks
  private TransactionController controller;

  @Mock
  private CreateTransactionUseCase createTransactionUseCase;

  @Mock
  private SearchTransactionsUseCase searchTransactionsUseCase;

  @Mock
  private TransactionMapper mapper;

  @Mock
  private ServerWebExchange serverWebExchange;

  @Test
  void createTransactionOk() {
    // Arrange
    final TransactionDto transactionDto = TransactionFactory.createTransactionDto();
    final Transaction transactionEntity = TransactionFactory.createTransaction().build();
    when(this.mapper.asTransaction(transactionDto)).thenReturn(transactionEntity);
    when(this.mapper.asTransactionDto(transactionEntity)).thenReturn(transactionDto);
    final PublisherProbe<Transaction> executeProbe = PublisherProbe.of(Mono.just(transactionEntity));
    when(this.createTransactionUseCase.execute(transactionEntity)).thenReturn(executeProbe.mono());

    // Act
    final Mono<ResponseEntity<TransactionDto>> result =
        this.controller.createTransaction(Mono.just(transactionDto), this.serverWebExchange);

    // Assert
    StepVerifier.create(result)
        .assertNext(
            transactionDtoResponseEntity -> assertThat(transactionDtoResponseEntity).isEqualTo(ResponseEntity.ok().body(transactionDto)))
        .verifyComplete();
    executeProbe.assertWasRequested();
  }

  @Test
  void getTransactionsOK() {
    // Arrange
    final TransactionDto someTransactionDto = TransactionFactory.createTransactionDto();
    final TransactionDto anotherTransactionDto = TransactionFactory.createTransactionDto();
    final Transaction someTransaction = TransactionFactory.createTransaction().build();
    final Transaction anotherTransaction = TransactionFactory.createTransaction().build();
    final List<Transaction> transactions = List.of(someTransaction, anotherTransaction);
    when(this.mapper.asTransactionDtoList(transactions)).thenReturn(List.of(anotherTransactionDto, someTransactionDto));

    final PublisherProbe<List<Transaction>> executeProbe = PublisherProbe.of(Mono.just(transactions));
    when(this.searchTransactionsUseCase.execute(TransactionFactory.SOME_IBAN, SOME_SORT)).thenReturn(executeProbe.mono());

    // Act
    final Mono<ResponseEntity<Flux<TransactionDto>>> result =
        this.controller.getTransactions(TransactionFactory.SOME_IBAN, SOME_SORT, this.serverWebExchange);

    // Assert
    StepVerifier.create(result)
        .assertNext(fluxResponseEntity -> {
          assertThat(fluxResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(fluxResponseEntity.getBody()).isNotNull();
          StepVerifier.create(fluxResponseEntity.getBody())
              .expectNext(anotherTransactionDto, someTransactionDto)
              .verifyComplete();
        })
        .verifyComplete();
    executeProbe.assertWasRequested();
  }

}