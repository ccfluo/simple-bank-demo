package com.simple.bank.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductPurchaseDTO {
    private Long purchaseId;
    private Long productId;
    private String productName;
    private Long customerId;
    private Long accountId;
    private BigDecimal purchaseAmount;
    private LocalDateTime purchaseTime;
    private String status; // HOLDING, REDEEMED, EXPIRED
    private String transactionTraceId;
}