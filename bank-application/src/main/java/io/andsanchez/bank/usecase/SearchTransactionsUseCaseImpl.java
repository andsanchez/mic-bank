package io.andsanchez.bank.usecase;

import java.util.List;

import io.andsanchez.bank.entity.Transaction;
import io.andsanchez.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SearchTransactionsUseCaseImpl implements SearchTransactionsUseCase {

  private final TransactionRepository transactionRepository;

  @Override
  public Mono<List<Transaction>> execute(final String accountIban, final Boolean orderByAmountAsc) {
    return this.transactionRepository.findByAccountIban(accountIban, orderByAmountAsc);
  }
}
