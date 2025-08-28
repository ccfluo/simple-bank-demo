package com.simple.bank.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductEntity {
    private Long productId;
    private String productName;
    private String productCode;
    private String type;
    private BigDecimal expectedReturnRate;
    private Integer termDays;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxPurchaseAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private String status; // ON_SALE, OFF_SALE, EXPIRED
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

