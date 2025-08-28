package com.simple.bank.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long productId;
    private String productName;
    private String productCode;
    private String type;
    private BigDecimal expectedReturnRate;
    private Integer termDays;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxPurchaseAmount;
    private BigDecimal remainingAmount;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

