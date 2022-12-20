package com.hexagonal.persistence.repository;

import com.hexagonal.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    @Query("select t from TransactionEntity t where t.account.iban = ?1 order by amount DESC")
    List<TransactionEntity> searchTransaction(String accountIban);
}
