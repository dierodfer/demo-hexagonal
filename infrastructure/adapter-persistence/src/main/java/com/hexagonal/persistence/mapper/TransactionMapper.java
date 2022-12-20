package com.hexagonal.persistence.mapper;

import com.hexagonal.persistence.entity.TransactionEntity;
import model.StatusTransactionResponse;
import model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TransactionMapper {
  List<Transaction> toDomain(List<TransactionEntity> entity);
  @Mapping(target = "accountIban", source = "account.iban")
  Transaction toDomain(TransactionEntity entity);
  @Mapping(target = "account.iban", source = "accountIban")
  TransactionEntity toEntity(Transaction domain);
  StatusTransactionResponse toResponse(TransactionEntity transactionEntity);
}
