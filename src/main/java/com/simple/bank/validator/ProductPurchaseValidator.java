package com.simple.bank.validator;

import com.simple.bank.api.request.ProductPurchaseRequest;
import com.simple.bank.dto.ProductDTO;
import com.simple.bank.entity.ProductPurchaseEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.ProductPurchaseMapper;
import com.simple.bank.service.biz.AccountInquireService;

import com.simple.bank.service.biz.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ProductPurchaseValidator {

//    @Autowired
//    private ProductService productService;
    @Autowired
    private ProductPurchaseMapper productPurchaseMapper;
//    @Autowired
//    private AccountInquireService accountService;

    public void validate(ProductPurchaseRequest request, ProductDTO product, BigDecimal accountBalance) throws BusinessException {
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
            throw new BusinessException("INVALID_FIELD", "Transaction Trace Id must not be empty");
        }

        // 2. validate product
//        ProductDTO product = productService.getProductById(request.getProductId());
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new BusinessException("PRODUCT_NOT_ON_SALE", "Product is not on sale");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(product.getStartDate()) || now.isAfter(product.getEndDate())) {
            throw new BusinessException("PRODUCT_NOT_IN_PERIOD", "Product is outside the subscription period");
        }
        if (request.getPurchaseAmount().compareTo(product.getMinPurchaseAmount()) < 0) {
            throw new BusinessException("AMOUNT_BELOW_MIN", "Investment amount < the minimum subscription amount");
        }
        if (product.getMaxPurchaseAmount() != null &&
                request.getPurchaseAmount().compareTo(product.getMaxPurchaseAmount()) > 0) {
            throw new BusinessException("AMOUNT_EXCEED_MAX", "Investment amount > the subscription limit");
        }
        if (request.getPurchaseAmount().compareTo(product.getRemainingAmount()) > 0) {
            throw new BusinessException("PRODUCT_INSUFFICIENT", "Insufficient remaining subscription quota");
        }

        // 3. validate account
        if (accountBalance.compareTo(request.getPurchaseAmount()) < 0) {
            throw new BusinessException("INSUFFICIENT_BALANCE", "Insufficient Balance");
        }

        //4. validate traceid to avoid duplicate trx
        ProductPurchaseEntity productPurseEntity = productPurchaseMapper.getPurchaseByTraceId(request.getTransactionTraceId());
        if (productPurseEntity != null) {
            throw new BusinessException("DUPLICATE_TRX", "Purchase " + request.getTransactionTraceId() + " processed by other transaction");
        }
    }
}