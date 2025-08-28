package com.simple.bank.api.request;

import com.simple.bank.dto.OperContext;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private OperContext operContext;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal transferAmount;
    private String remark;
    private String transferTraceId;
}