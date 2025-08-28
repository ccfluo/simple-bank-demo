package com.simple.bank.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDTO {
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
