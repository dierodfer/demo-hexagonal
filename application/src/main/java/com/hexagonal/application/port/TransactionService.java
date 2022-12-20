package com.hexagonal.application.port;

import model.SortEnum;
import model.StatusTransactionRequest;
import model.StatusTransactionResponse;
import model.Transaction;

import java.util.List;

public interface TransactionService {
    void createTransaction(Transaction transaction);
    StatusTransactionResponse statusTransaction(StatusTransactionRequest statusTransactionRequest);
    List<Transaction> searchTransactions(String accountIban, SortEnum sort);
}
