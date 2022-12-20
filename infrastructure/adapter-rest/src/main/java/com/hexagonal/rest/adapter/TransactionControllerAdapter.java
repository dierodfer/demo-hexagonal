package com.hexagonal.rest.adapter;

import api.TransactionApi;
import com.hexagonal.application.port.TransactionService;
import lombok.AllArgsConstructor;
import model.SortEnum;
import model.StatusTransactionRequest;
import model.StatusTransactionResponse;
import model.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class TransactionControllerAdapter implements TransactionApi {

  private final TransactionService transactionService;

  @Override
  public ResponseEntity<Void> createTransaction(Transaction transaction) {
    transactionService.createTransaction(transaction);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<Transaction>> searchTransactions(String accountIban, SortEnum sort) {
    return ResponseEntity.ok(transactionService.searchTransactions(accountIban,sort));
  }

  @Override
  public ResponseEntity<StatusTransactionResponse> statusTransaction(StatusTransactionRequest statusTransactionRequest) {
    return ResponseEntity.ok(transactionService.statusTransaction(statusTransactionRequest));
  }
}
