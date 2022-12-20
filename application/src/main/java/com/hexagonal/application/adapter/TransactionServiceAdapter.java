package com.hexagonal.application.adapter;

import com.hexagonal.application.port.TransactionService;
import com.hexagonal.domain.port.TransactionPersistencePort;
import lombok.AllArgsConstructor;
import model.SortEnum;
import model.StatusTransactionRequest;
import model.StatusTransactionResponse;
import model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceAdapter implements TransactionService {

    private final TransactionPersistencePort transactionPersistencePort;

    @Override
    public void createTransaction(Transaction transaction) {
        transactionPersistencePort.createTransaction(transaction);
    }

    @Override
    public StatusTransactionResponse statusTransaction(StatusTransactionRequest statusTransactionRequest) {
        return transactionPersistencePort.statusTransaction(statusTransactionRequest);
    }

    @Override
    public List<Transaction> searchTransactions(String accountIban, SortEnum sort) {
        return transactionPersistencePort.searchTransactions(accountIban, sort);
    }
}
