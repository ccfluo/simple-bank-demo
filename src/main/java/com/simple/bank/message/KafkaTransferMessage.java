package com.simple.bank.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaTransferMessage {
    public static final String TOPIC = "transfer_topic";

    private Long transferId;
    private Long fromCustomerId;
    private Long fromAccountId;
    private BigDecimal fromAccountBalance;
    private String fromCustomerMobile;
    private String fromCustomerEmail;
    private Long toCustomerId;
    private Long toAccountId;
    private BigDecimal toAccountBalance;
    private String toCustomerMobile;
    private String toCustomerEmail;
    private String transferType;
    private BigDecimal transferAmount;
    private LocalDateTime transferTime;

}
