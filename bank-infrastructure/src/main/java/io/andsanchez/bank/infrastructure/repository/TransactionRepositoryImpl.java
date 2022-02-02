package io.andsanchez.bank.infrastructure.repository;

import java.util.List;

import io.andsanchez.bank.entity.Transaction;
import io.andsanchez.bank.infrastructure.repository.mongodb.TransactionDocumentMapper;
import io.andsanchez.bank.infrastructure.repository.mongodb.TransactionMongoRepository;
import io.andsanchez.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionRepositoryImpl implements TransactionRepository {

  private final TransactionMongoRepository repository;

  private final TransactionDocumentMapper mapper;

  @Override
  public Mono<Transaction> save(final Transaction transaction) {
    return Mono.just(transaction)
        .doOnNext(cmd -> log.debug("Start save transaction with reference '{}'", transaction.getReference()))
        .map(this.mapper::asTransactionDocument)
        .flatMap(this.repository::save)
        .map(this.mapper::asTransaction)
        .doOnSuccess(savedTransaction -> log.debug("End save transaction: '{}'", savedTransaction));
  }

  @Override
  public Mono<List<Transaction>> findByAccountIban(final String accountIban, final Boolean orderByAmountAsc) {
    return
        this.repository.findByAccountIban(accountIban,
            Sort.by(orderByAmountAsc != null && orderByAmountAsc ? Direction.ASC : Direction.DESC, "amount"))
            .map(this.mapper::asTransaction)
            .collectList();
  }

  @Override
  public Mono<Boolean> existsById(final Transaction transaction) {
    return this.repository.existsById(transaction.getReference());
  }
}
