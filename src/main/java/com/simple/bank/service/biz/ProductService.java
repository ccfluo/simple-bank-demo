package com.simple.bank.service.biz;

import com.simple.bank.dto.ProductDTO;
import com.simple.bank.dto.ProductMiniDTO;
import com.simple.bank.exception.BusinessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    // inquire all products on sale
    List<ProductMiniDTO> getOnSaleProducts() throws BusinessException;

    // inquire product given productId
    ProductDTO getProductById(Long productId) throws BusinessException;

    // batch sync update remaining amount to DB
    void batchUpdateProductStock(Map<Long, BigDecimal> productRemainingAmounts);
}