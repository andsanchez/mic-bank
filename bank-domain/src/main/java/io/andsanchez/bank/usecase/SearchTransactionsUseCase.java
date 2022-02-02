package io.andsanchez.bank.usecase;

import java.util.List;

import io.andsanchez.bank.entity.Transaction;
import reactor.core.publisher.Mono;

public interface SearchTransactionsUseCase {

  Mono<List<Transaction>> execute(String accountIban, Boolean asc);

}
