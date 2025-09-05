package com.simple.bank.validator;

import com.simple.bank.api.request.ProductPurchaseRequest;
import com.simple.bank.dto.ProductDTO;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.ProductPurchaseMapper;

import com.simple.bank.service.redis.ProductRedisService;
import com.simple.bank.service.biz.ProductStockWarmupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
public class ProductPurchaseValidator {

    @Autowired
    private ProductPurchaseMapper productPurchaseMapper;
    @Autowired
    private ProductRedisService productRedisService;
    @Autowired
    private ProductStockWarmupService productStockWarmupService;

    public void validate(ProductPurchaseRequest request, ProductDTO productDTO, BigDecimal accountBalance) throws BusinessException {
        // 1. validate input
        if (request.getProductId() == null) {
            throw new BusinessException("INVALID_FIELD", "Product Id must not be empty");
        }
        if (request.getAccountId() == null) {
            throw new BusinessException("INVALID_FIELD", "Account Id must not be empty");
        }
        if (request.getPurchaseAmount() == null || request.getPurchaseAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_FIELD", "Purchase amount must > 0");
        }
        if (request.getTransactionTraceId() == null || request.getTransactionTraceId().trim().isEmpty()) {
            throw new BusinessException("INVALID_FIELD", "Purchase Trace Id must not be empty");
        }

        // 2. validate product
        if (!"ON_SALE".equals(productDTO.getStatus())) {
            throw new BusinessException("PRODUCT_NOT_ON_SALE", "Product is not on sale");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(productDTO.getStartDate()) || now.isAfter(productDTO.getEndDate())) {
            throw new BusinessException("PRODUCT_NOT_IN_PERIOD", "Product is outside the subscription period");
        }
        if (request.getPurchaseAmount().compareTo(productDTO.getMinPurchaseAmount()) < 0) {
            throw new BusinessException("AMOUNT_BELOW_MIN", "Investment amount < the minimum subscription amount");
        }
        if (productDTO.getMaxPurchaseAmount() != null &&
                request.getPurchaseAmount().compareTo(productDTO.getMaxPurchaseAmount()) > 0) {
            throw new BusinessException("AMOUNT_EXCEED_MAX", "Investment amount > the subscription limit");
        }

        // validate if still have enough remaining amount to purchase
        Integer isHot = productDTO.getIsHot();
        BigDecimal remainingAmount;
        // hot product : get remaining amount from redis; otherwise get it from DB(productDTO)
        if (isHot != null && isHot > 0) {
            remainingAmount = productRedisService.getProductStockById(productDTO.getProductId());
            if (remainingAmount == null) {
                // Redis缓存未命中，触发实时预热（避免缓存穿透）
                log.warn("Hot product Redis not hit，trigger warm up now，productId: {}", productDTO.getProductId());
                productStockWarmupService.warmupProductStock(productDTO.getProductId());
                // inquire redis again in case other txn has updated remaining amount in redis after warmup above
                remainingAmount = productRedisService.getProductStockById(productDTO.getProductId());
            }
        }else {
            remainingAmount = productDTO.getRemainingAmount();
        }
        if (request.getPurchaseAmount().compareTo(remainingAmount) > 0) {
            throw new BusinessException("PRODUCT_INSUFFICIENT", "Insufficient remaining subscription quota");
        }


        // 3. validate account
        if (accountBalance.compareTo(request.getPurchaseAmount()) < 0) {
            throw new BusinessException("INSUFFICIENT_BALANCE", "Insufficient Balance");
        }

        //4. validate traceid to avoid duplicate trx
        int exists = productPurchaseMapper.existsByTraceId(request.getTransactionTraceId());
        if (exists == 1) {
            throw new BusinessException("DUPLICATE_TRX", "Purchase " + request.getTransactionTraceId() + " processed by other transaction");
        }
    }
}