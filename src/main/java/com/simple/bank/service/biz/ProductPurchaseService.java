package com.simple.bank.service.biz;

import com.simple.bank.api.request.ProductPurchaseRequest;
import com.simple.bank.dto.ProductPurchaseDTO;
import com.simple.bank.exception.BusinessException;
import java.util.List;

public interface ProductPurchaseService {
    // purchase
    ProductPurchaseDTO purchase(ProductPurchaseRequest request) throws BusinessException;

    // get purchase history by customer id
    List<ProductPurchaseDTO> getPurchaseHistory(Long customerId) throws BusinessException;

    //get purchase history by trace id
    ProductPurchaseDTO getPurchaseByTraceId(String productPurchaseTraceId);
}