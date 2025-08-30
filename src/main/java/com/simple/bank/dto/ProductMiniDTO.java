package com.simple.bank.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// used for list of product onSale, only basic product data will be returned
// remaining amount won't be return which is updated in real time.
@Data
public class ProductMiniDTO {
    private Long productId;
    private String productName;
    private String productCode;
    private String type;
    private BigDecimal expectedReturnRate;
    private Integer termDays;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxPurchaseAmount;
//    private BigDecimal remainingAmount; ==> No remaining amount return
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
    private Integer isHot;
}

