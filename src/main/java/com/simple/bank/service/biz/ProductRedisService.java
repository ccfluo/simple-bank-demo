package com.simple.bank.service.biz;

import com.simple.bank.dto.ProductDTO;
import java.util.List;

public interface ProductRedisService {
    void delete(Long productId);

    ProductDTO getById(Long productId);

    void set(ProductDTO productDTO);

    List<ProductDTO> getOnSaleProducts();

    void setOnSaleProducts(List<ProductDTO> products);
}