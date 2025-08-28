package com.simple.bank.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaPurchaseMessage {
    public static final String TOPIC = "purchase_topic";

    private Long purchaseId;
    private Long productId;
    private Long customerId;
    private Long accountId;
    private BigDecimal purchaseAmount;
    private LocalDateTime purchaseTime;
    private String status; // HOLDING, REDEEMED, EXPIRED
    private String transactionTraceId;
    private BigDecimal accountBalance;
    private String mobile;
    private String email;


}

