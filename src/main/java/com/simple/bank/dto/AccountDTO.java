package com.simple.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long accountId;
    private String type;
    private String productCode;
    private BigDecimal balance;
    private String accountStatus;
    private BigDecimal overDraft;
    private BigDecimal interestRate;
    private Long customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
