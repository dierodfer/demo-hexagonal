package com.hexagonal.persistence.adapter;

import com.hexagonal.domain.port.TransactionPersistencePort;
import com.hexagonal.persistence.entity.AccountEntity;
import com.hexagonal.persistence.entity.TransactionEntity;
import com.hexagonal.persistence.mapper.TransactionMapper;
import com.hexagonal.persistence.repository.AccountRepository;
import com.hexagonal.persistence.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionRepositoryAdapter implements TransactionPersistencePort {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    private TransactionMapper mapper;

    @Override
    public void createTransaction(Transaction transaction) {
        Assert.isTrue(transaction.getReference() == null || !transactionRepository.existsById(transaction.getReference()),"Existing transaction with the same Reference");
        TransactionEntity entity = mapper.toEntity(transaction);
        Optional<AccountEntity> accountOpt = accountRepository.findById(transaction.getAccountIban());
        entity.getAccount().setTotal(accountOpt.isPresent()?accountOpt.get().getTotal():0.0);
        entity.calculateBalanceAccount();
        Assert.isTrue(entity.getAccount().getTotal()>0,"Total account balance bellow 0 is not allowed");
        transactionRepository.save(entity);
    }

    @Override
    public List<Transaction> searchTransactions(String accountIban, SortEnum sort) {
        List<Transaction> response = mapper.toDomain(transactionRepository.searchTransaction(accountIban));
        if(SortEnum.ASC.equals(sort)) Collections.reverse(response);
        return response;
    }

    @Override
    public StatusTransactionResponse statusTransaction(StatusTransactionRequest statusTransactionRequest) {
        StatusTransactionResponse response;
        Optional<TransactionEntity> transactionOpt = transactionRepository.findById(statusTransactionRequest.getReference());

        if(transactionOpt.isPresent()){
            response = this.applybusinessRules(statusTransactionRequest.getChannel(),transactionOpt.get());
        }else{
            response = new StatusTransactionResponse();
            response.setReference(statusTransactionRequest.getReference());
            response.setStatus(StatusEnum.INVALID);
        }

        return response;
    }

    private StatusTransactionResponse applybusinessRules(ChannelEnum channel, TransactionEntity transaction){
        StatusTransactionResponse response = mapper.toResponse(transaction);
        LocalDate transactionDate = transaction.getDate().toLocalDate();
        switch (channel){
            case CLIENT:
                if(transactionDate.isBefore(LocalDate.now()))
                    this.substractFeeAndNewStatus(response, StatusEnum.SETTLED);
                else if(transactionDate.isEqual(LocalDate.now()))
                    this.substractFeeAndNewStatus(response, StatusEnum.PENDING);
                else if(transactionDate.isAfter(LocalDate.now()))
                    this.substractFeeAndNewStatus(response, StatusEnum.FUTURE);
                break;
            case ATM:
                if(transactionDate.isBefore(LocalDate.now()))
                    this.substractFeeAndNewStatus(response, StatusEnum.SETTLED);
                else if(transactionDate.isEqual(LocalDate.now()) || transactionDate.isAfter(LocalDate.now()))
                    this.substractFeeAndNewStatus(response, StatusEnum.PENDING);
                break;
            case INTERNAL:
                if(transactionDate.isBefore(LocalDate.now()))
                    response.setStatus(StatusEnum.SETTLED);
                else if(transactionDate.isEqual(LocalDate.now()))
                    response.setStatus(StatusEnum.PENDING);
                else if(transactionDate.isAfter(LocalDate.now()))
                    response.setStatus(StatusEnum.FUTURE);
                break;
            default:
        }
        return response;
    }

    private void substractFeeAndNewStatus(StatusTransactionResponse response, StatusEnum status){
        response.setStatus(status);
        response.setAmount(response.getAmount() - response.getFee());
        response.setFee(null);
    }
}
