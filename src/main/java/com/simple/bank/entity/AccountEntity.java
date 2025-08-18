package com.simple.bank.entity;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.*;
import com.simple.bank.enums.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
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
