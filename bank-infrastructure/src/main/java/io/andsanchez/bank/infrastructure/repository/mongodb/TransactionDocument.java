package io.andsanchez.bank.infrastructure.repository.mongodb;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Value
@Document(collection = "transactions")
public class TransactionDocument {

  @Id
  String reference;

  String accountIban;

  Long date;

  Double amount;

  Double fee;

  String description;

}