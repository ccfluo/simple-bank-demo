package com.simple.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferEntity {
    private Long transferId;
    private String transferTraceId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal transferAmount;
    private String transactionType = "TRANSFER";  // 默认值
    private String status;
    private String remark;
    private LocalDateTime transferTime;
    private LocalDateTime createdAt;
}
