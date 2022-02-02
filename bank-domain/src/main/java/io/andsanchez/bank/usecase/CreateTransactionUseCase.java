package io.andsanchez.bank.usecase;

import io.andsanchez.bank.entity.Transaction;
import reactor.core.publisher.Mono;

public interface CreateTransactionUseCase {

  Mono<Transaction> execute(Transaction transaction);

}
