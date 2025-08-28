package com.simple.bank.api.request;

import com.simple.bank.dto.OperContext;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductPurchaseRequest {
    private OperContext operContext;
    private Long productId;
    private Long accountId; // 支付账户ID
    private BigDecimal purchaseAmount;
    private String transactionTraceId; // 幂等性ID
}