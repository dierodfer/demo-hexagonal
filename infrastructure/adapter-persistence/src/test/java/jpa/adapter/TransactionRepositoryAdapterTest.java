package jpa.adapter;

import com.hexagonal.persistence.adapter.TransactionRepositoryAdapter;
import com.hexagonal.persistence.entity.AccountEntity;
import com.hexagonal.persistence.entity.TransactionEntity;
import com.hexagonal.persistence.mapper.TransactionMapper;
import com.hexagonal.persistence.repository.TransactionRepository;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TransactionRepositoryAdapterTest {
  @InjectMocks
  TransactionRepositoryAdapter transactionRepositoryAdapter;
  @Mock
  TransactionRepository transactionRepository;
  @Mock
  TransactionMapper transactionMapper;

  private final String REFERENCE = "12345A";
  private final String IBAN = "ES9820385778983000760236";
  private final LocalDateTime DATE = LocalDateTime.now();
  private final Double FEE = 3.18;
  private final Double AMOUNT = 193.38;
  private final String DESCRIPTION = "Restaurant payment";

  StatusTransactionRequest statusTransactionRequest;
  StatusTransactionResponse statusTransactionResponse;
  TransactionEntity transactionEntity;
  Transaction transaction;

  @BeforeEach
  void setUp() {
    statusTransactionRequest = new StatusTransactionRequest();
    statusTransactionRequest.setReference(REFERENCE);
    transactionEntity = new TransactionEntity();
    transactionEntity.setReference(REFERENCE);
    transactionEntity.setAmount(AMOUNT);
    transactionEntity.setFee(FEE);
    transactionEntity.setDescription(DESCRIPTION);
    transactionEntity.setAccount(AccountEntity.builder().iban(IBAN).build());
    transactionEntity.setDate(DATE);
    transaction = new Transaction();
    transaction.setReference(REFERENCE);
    transaction.setAmount(AMOUNT);
    transaction.setFee(FEE);
    transaction.setDescription(DESCRIPTION);
    transaction.setAccountIban(IBAN);
    transaction.setDate(DATE);
    statusTransactionResponse = new StatusTransactionResponse();
    statusTransactionResponse.setReference(REFERENCE);
    statusTransactionResponse.setAmount(AMOUNT);
    statusTransactionResponse.setFee(FEE);
  }

  @Test
  void businessRuleA() {
    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.INVALID, response.getStatus());
    assertNull(response.getAmount());
    assertNull(response.getFee());
  }

  @Test
  void businessRuleB1() {
    statusTransactionRequest.setChannel(ChannelEnum.CLIENT);
    transactionEntity.setDate(LocalDateTime.now().minusDays(1));

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.SETTLED, response.getStatus());
    assertEquals(AMOUNT-FEE, response.getAmount());
    assertNull(response.getFee());
  }

  @Test
  void businessRuleB2() {
    statusTransactionRequest.setChannel(ChannelEnum.ATM);
    transactionEntity.setDate(LocalDateTime.now().minusDays(1));

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.SETTLED, response.getStatus());
    assertEquals(AMOUNT-FEE, response.getAmount());
    assertNull(response.getFee());
  }

  @Test
  void businessRuleC() {
    statusTransactionRequest.setChannel(ChannelEnum.INTERNAL);
    transactionEntity.setDate(LocalDateTime.now().minusDays(1));

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.SETTLED, response.getStatus());
    assertEquals(AMOUNT, response.getAmount());
    assertEquals(FEE, response.getFee());
  }

  @Test
  void businessRuleD1() {
    statusTransactionRequest.setChannel(ChannelEnum.CLIENT);

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.PENDING, response.getStatus());
    assertEquals(AMOUNT-FEE, response.getAmount());
    assertNull(response.getFee());
  }

  @Test
  void businessRuleD2() {
    statusTransactionRequest.setChannel(ChannelEnum.ATM);

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.PENDING, response.getStatus());
    assertEquals(AMOUNT-FEE, response.getAmount());
    assertNull(response.getFee());
  }

  @Test
  void businessRuleE() {
    statusTransactionRequest.setChannel(ChannelEnum.INTERNAL);

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.PENDING, response.getStatus());
    assertEquals(AMOUNT, response.getAmount());
    assertEquals(FEE, response.getFee());
  }

  @Test
  void businessRuleF() {
    statusTransactionRequest.setChannel(ChannelEnum.CLIENT);
    transactionEntity.setDate(LocalDateTime.now().plusDays(1));

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.FUTURE, response.getStatus());
    assertEquals(AMOUNT-FEE, response.getAmount());
    assertNull(response.getFee());
  }

  @Test
  void businessRuleG() {
    statusTransactionRequest.setChannel(ChannelEnum.ATM);
    transactionEntity.setDate(LocalDateTime.now().plusDays(1));

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.PENDING, response.getStatus());
    assertEquals(AMOUNT-FEE, response.getAmount());
    assertNull(response.getFee());
  }

  @Test
  void businessRuleH() {
    statusTransactionRequest.setChannel(ChannelEnum.INTERNAL);
    transactionEntity.setDate(LocalDateTime.now().plusDays(1));

    when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
    when(transactionMapper.toResponse(any())).thenReturn(statusTransactionResponse);

    StatusTransactionResponse response = transactionRepositoryAdapter.statusTransaction(statusTransactionRequest);
    assertEquals(REFERENCE, response.getReference());
    assertEquals(StatusEnum.FUTURE, response.getStatus());
    assertEquals(AMOUNT, response.getAmount());
    assertEquals(FEE, response.getFee());
  }
  
}
