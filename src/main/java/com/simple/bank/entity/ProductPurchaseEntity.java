package com.simple.bank.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductPurchaseEntity {
    private Long purchaseId;
    private Long productId;
    private Long customerId;
    private Long accountId;
    private BigDecimal purchaseAmount;
    private LocalDateTime purchaseTime;
    private String status; // HOLDING, REDEEMED, EXPIRED
    private String transactionTraceId;
//    private String productName;   // From wealth_product table
}