package com.simple.bank.api.request;

import com.simple.bank.dto.OperContext;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private OperContext operContext;
    private Long accountId;
    private BigDecimal transactionAmount;
    private String description;
}