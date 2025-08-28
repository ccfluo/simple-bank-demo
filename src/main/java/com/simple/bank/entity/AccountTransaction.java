package com.simple.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransaction {
    private Long transactionId;
    private String transactionTraceId;
    private LocalDateTime transactionDate;
    private BigDecimal transactionAmount;
    private String transactionType; // 'CREDIT' /'DEBIT'
    private Long customerId;
    private Long accountId;
    private BigDecimal accountBalance;
    private String description;
    private LocalDateTime createdAt;
}