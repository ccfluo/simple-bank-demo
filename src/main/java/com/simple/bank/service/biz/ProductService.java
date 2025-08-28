package com.simple.bank.service.biz;

import com.simple.bank.dto.ProductDTO;
import com.simple.bank.exception.BusinessException;
import java.util.List;

public interface ProductService {
    // 查询所有在售产品
    List<ProductDTO> getOnSaleProducts() throws BusinessException;

    // 按ID查询产品
    ProductDTO getProductById(Long productId) throws BusinessException;
}