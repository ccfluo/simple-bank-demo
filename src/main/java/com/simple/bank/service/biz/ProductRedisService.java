package com.simple.bank.service.biz;

import java.math.BigDecimal;
import java.util.Map;

public interface ProductRedisService {

    BigDecimal getProductStockById(Long productId);

    void setProductStockById(Long productId, BigDecimal remainingAmount, long expirySeconds);

    Boolean deleteProductStockById(Long productId);

    BigDecimal deductProductStockById(Long productId, BigDecimal amount);

    BigDecimal increaseProductStockById(Long productId, BigDecimal amount);

    Map<Long, BigDecimal> getAllProductStock();
}