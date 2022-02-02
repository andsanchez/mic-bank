package io.andsanchez.bank.repository;

import java.util.List;

import io.andsanchez.bank.entity.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionRepository {

  Mono<Transaction> save(Transaction transaction);

  Mono<List<Transaction>> findByAccountIban(String accountIban, Boolean orderByAmountAsc);

  Mono<Boolean> existsById(Transaction transaction);
}
