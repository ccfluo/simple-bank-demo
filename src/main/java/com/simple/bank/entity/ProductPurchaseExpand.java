package com.simple.bank.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// expanded ProductionPurchaseEntity join fields from productEntity for frontend display purpose
@Data
public class ProductPurchaseExpand {
    // from ProductPurchaseEntity
    private Long purchaseId;
    private Long productId;
    private Long customerId;
    private Long accountId;
    private BigDecimal purchaseAmount;
    private LocalDateTime purchaseTime;
    private String status; // HOLDING, REDEEMED, EXPIRED
    private String transactionTraceId;
    // from ProductEntity
    private String productName;
    private String productCode;
    private String type;
    private BigDecimal expectedReturnRate;
    private Integer termDays;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxPurchaseAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private String productStatus; // renamed from status to productStatus:ON_SALE, OFF_SALE, EXPIRED
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer isHot;
}