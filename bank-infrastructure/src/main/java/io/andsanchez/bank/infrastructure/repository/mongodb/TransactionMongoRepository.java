package io.andsanchez.bank.infrastructure.repository.mongodb;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionMongoRepository extends ReactiveMongoRepository<TransactionDocument, String>,
    ReactiveCrudRepository<TransactionDocument, String> {

  Flux<TransactionDocument> findByAccountIban(String accountIban, Sort sort);
}