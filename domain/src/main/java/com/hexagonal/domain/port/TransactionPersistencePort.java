package com.hexagonal.domain.port;

import model.SortEnum;
import model.StatusTransactionRequest;
import model.StatusTransactionResponse;
import model.Transaction;

import java.util.List;

public interface TransactionPersistencePort {
    void createTransaction(Transaction transaction);
    StatusTransactionResponse statusTransaction(StatusTransactionRequest statusTransactionRequest);
    List<Transaction> searchTransactions(String accountIban, SortEnum sort);
}
