package io.andsanchez.bank.infrastructure.repository.mongodb;

import io.andsanchez.bank.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionDocumentMapper {

  Transaction asTransaction(TransactionDocument document);

  TransactionDocument asTransactionDocument(Transaction transaction);
}
