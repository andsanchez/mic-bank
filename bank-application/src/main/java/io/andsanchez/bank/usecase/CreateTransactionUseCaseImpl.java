package io.andsanchez.bank.usecase;

import java.util.UUID;

import io.andsanchez.bank.entity.Transaction;
import io.andsanchez.bank.exceptions.DuplicateReferenceException;
import io.andsanchez.bank.repository.TransactionRepository;
import io.andsanchez.bank.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

  private final TransactionRepository transactionRepository;

  private final EntityValidator entityValidator;

  @Override
  public Mono<Transaction> execute(final Transaction transaction) {
    return Mono.just(transaction)
        .doOnNext(cmd -> log.trace("Execute create transaction: '{}'", transaction))
        .map(this::assignReference)
        .zipWhen(this.transactionRepository::existsById)
        .flatMap(this::checkDuplicateTransaction)
        .flatMap(this.entityValidator::validate)
        .flatMap(this.transactionRepository::save);
  }

  private Transaction assignReference(final Transaction transaction) {
    if (transaction.getReference() == null) {
      return transaction.toBuilder()
          .reference(UUID.randomUUID().toString().replace("-", ""))
          .build();
    }
    return transaction;
  }

  private Mono<? extends Transaction> checkDuplicateTransaction(Tuple2<Transaction, Boolean> tupleTransactionExistsById) {
    Transaction transactionToSave = tupleTransactionExistsById.getT1();
    Boolean existsById = tupleTransactionExistsById.getT2();
    return existsById ? Mono.error(new DuplicateReferenceException(transactionToSave.getReference())) : Mono.just(transactionToSave);
  }

}
