package com.simple.bank.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMessage {
    public static final String TOPIC = "transaction_topic";

    private Long transactionId;
    private Long customerId;
    private Long accountId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime transactionTime;
    private String mobile;
    private String email;
}
