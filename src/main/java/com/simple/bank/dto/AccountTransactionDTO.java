package com.simple.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionDTO {
    private Long transactionId;
    private LocalDateTime transactionDate;
    private BigDecimal transactionAmount;
    private String transactionType;
    private Long customerId;
    private Long accountId;
    private BigDecimal accountBalance;
    private String description;
    private LocalDateTime createdAt;
}
