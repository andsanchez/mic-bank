package io.andsanchez.bank.api.rest.controller;

import java.util.List;

import io.andsanchez.bank.api.rest.mapper.TransactionMapper;
import io.andsanchez.bank.api.rest.model.TransactionDto;
import io.andsanchez.bank.api.rest.service.TransactionApi;
import io.andsanchez.bank.usecase.CreateTransactionUseCase;
import io.andsanchez.bank.usecase.SearchTransactionsUseCase;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController implements TransactionApi {

  private final TransactionMapper mapper;

  private final CreateTransactionUseCase createTransactionUseCase;

  private final SearchTransactionsUseCase searchTransactionsUseCase;

  @Override
  public Mono<ResponseEntity<TransactionDto>> createTransaction(@Valid final Mono<TransactionDto> transactionDto,
      final ServerWebExchange exchange) {
        return transactionDto
            .map(this.mapper::asTransaction)
            .flatMap(this.createTransactionUseCase::execute)
            .map(this.mapper::asTransactionDto)
            .map(ResponseEntity.ok()::body);
  }

  @Override
  public Mono<ResponseEntity<Flux<TransactionDto>>> getTransactions(final String accountIban, @Valid final Boolean orderByAmountAsc,
      final ServerWebExchange exchange) {
    return this.searchTransactionsUseCase.execute(accountIban, orderByAmountAsc)
        .map(this.mapper::asTransactionDtoList)
        .switchIfEmpty(Mono.just(List.of()))
        .map(result -> ResponseEntity.ok(Flux.fromIterable(result)));
  }

}
