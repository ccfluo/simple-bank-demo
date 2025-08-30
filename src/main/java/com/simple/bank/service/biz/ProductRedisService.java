package com.simple.bank.service.biz;

import com.simple.bank.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductRedisService {
    void delete(Long productId);

    ProductDTO getById(Long productId);

    void set(ProductDTO productDTO);

    List<ProductDTO> getOnSaleProducts();

    void setOnSaleProducts(List<ProductDTO> products);

    BigDecimal getRemainingAmountById(Long productId);

    void setRemainingAmount(Long productId, BigDecimal remainingAmount, long expirySeconds);

    Boolean deleteRemainingAmount(Long productId);

    BigDecimal deductRemainingAmountById(Long productId, BigDecimal amount);

    BigDecimal increaseRemainingAmountById(Long productId, BigDecimal amount);

    Map<Long, BigDecimal> getAllProductsRemainingAmount();
}