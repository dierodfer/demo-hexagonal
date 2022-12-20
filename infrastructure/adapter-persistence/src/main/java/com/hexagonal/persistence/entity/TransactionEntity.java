package com.hexagonal.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "TRANSACTION")
public class TransactionEntity {
  @Id
  private String reference;
  private LocalDateTime date;
  private Double amount;
  private Double fee;
  private String description;

  @ManyToOne(cascade = CascadeType.ALL)
  private AccountEntity account;

  @PrePersist
  private void prePersist(){
    if(null == reference) this.reference = UUID.randomUUID().toString().replace("-", "");
    if(null == date) this.date = LocalDateTime.now();
  }

  public void calculateBalanceAccount() {
    this.getAccount().setTotal(account.getTotal() + amount - (fee == null ? 0 : fee));
  }
}

