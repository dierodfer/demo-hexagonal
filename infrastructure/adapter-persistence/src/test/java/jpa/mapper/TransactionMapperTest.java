package jpa.mapper;

import com.hexagonal.persistence.entity.TransactionEntity;
import com.hexagonal.persistence.mapper.TransactionMapper;
import com.hexagonal.persistence.mapper.TransactionMapperImpl;
import model.StatusTransactionResponse;
import model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class TransactionMapperTest {

  private TransactionMapper transactionMapper;

  PodamFactory podamFactory = new PodamFactoryImpl();

  List<TransactionEntity> transactionEntityList;
  Transaction transactionDomain;

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void setup() {
    transactionMapper = new TransactionMapperImpl();
    transactionEntityList =  podamFactory.manufacturePojo(ArrayList.class,TransactionEntity.class);
    transactionDomain =  podamFactory.manufacturePojo(Transaction.class);
  }

  @Test
  void toDomainListTransaction(){
    List<Transaction> transactionsList = transactionMapper.toDomain(transactionEntityList);
    assertEquals(transactionsList.size(), transactionEntityList.size());
  }

  @Test
  void toDomainTransaction(){
    Transaction transaction = transactionMapper.toDomain(transactionEntityList.get(0));
    assertEquals(transaction.getReference(), transactionEntityList.get(0).getReference());
    assertEquals(transaction.getDate(), transactionEntityList.get(0).getDate());
    assertEquals(transaction.getAmount(), transactionEntityList.get(0).getAmount());
    assertEquals(transaction.getFee(), transactionEntityList.get(0).getFee());
    assertEquals(transaction.getAccountIban(), transactionEntityList.get(0).getAccount().getIban());
    assertEquals(transaction.getDescription(), transactionEntityList.get(0).getDescription());
  }

  @Test
  void toEntityTransaction(){
    TransactionEntity transaction = transactionMapper.toEntity(transactionDomain);
    assertEquals(transaction.getReference(), transactionDomain.getReference());
    assertEquals(transaction.getDate(), transactionDomain.getDate());
    assertEquals(transaction.getAmount(), transactionDomain.getAmount());
    assertEquals(transaction.getFee(), transactionDomain.getFee());
    assertEquals(transaction.getAccount().getIban(), transactionDomain.getAccountIban());
    assertEquals(transaction.getDescription(), transactionDomain.getDescription());
  }

  @Test
  void toResponseTransaction(){
    StatusTransactionResponse response = transactionMapper.toResponse(transactionEntityList.get(0));
    assertEquals(response.getReference(), transactionEntityList.get(0).getReference());
    assertEquals(response.getAmount(), transactionEntityList.get(0).getAmount());
    assertEquals(response.getFee(), transactionEntityList.get(0).getFee());
  }
}
