package io.andsanchez.bank.api.rest.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.andsanchez.bank.api.rest.model.TransactionDto;
import io.andsanchez.bank.entity.Transaction;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

  @Mapping(target = "date", ignore = true)
  public abstract Transaction asTransaction(TransactionDto transactionDto);

  @AfterMapping
  void setDate(@MappingTarget final Transaction.TransactionBuilder transaction, final TransactionDto transactionDto) {
    Optional.ofNullable(transactionDto.getDate())
        .ifPresent(offsetDateTime -> transaction.date(this.fromOffsetDateTime(offsetDateTime)));
  }

  @Mapping(target = "date", ignore = true)
  public abstract TransactionDto asTransactionDto(Transaction transaction);

  @AfterMapping
  void setDate(@MappingTarget final TransactionDto transactionDto, final Transaction transaction) {
    Optional.ofNullable(transaction.getDate())
        .ifPresent(millis -> transactionDto.setDate(OffsetDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC)));
  }

  public List<TransactionDto> asTransactionDtoList(final List<Transaction> transactions) {
    return Optional.ofNullable(transactions).orElse(List.of()).stream()
        .filter(Objects::nonNull)
        .map(this::asTransactionDto)
        .collect(Collectors.toList());
  }

  private long fromOffsetDateTime(final OffsetDateTime offsetDateTime) {
    return offsetDateTime.toInstant().toEpochMilli();
  }

}
