package com.simple.bank.converter;

import com.simple.bank.dto.ProductDTO;
import com.simple.bank.dto.ProductMiniDTO;
import com.simple.bank.dto.ProductPurchaseDTO;
import com.simple.bank.entity.ProductEntity;
import com.simple.bank.entity.ProductPurchaseEntity;
import com.simple.bank.entity.ProductPurchaseExpand;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {
    public ProductDTO productToDto(ProductEntity productEntity) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(productEntity, dto);
        return dto;
    }

    public ProductMiniDTO productToMiniDto(ProductEntity productEntity) {
        ProductMiniDTO dto = new ProductMiniDTO();
        BeanUtils.copyProperties(productEntity, dto);
        return dto;
    }

    public ProductEntity dtoToFinancialProduct(ProductDTO dto) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(dto, product);
        return product;
    }

    public ProductPurchaseDTO productPurchaseToDto(ProductPurchaseEntity productPurchaseEntity) {
        ProductPurchaseDTO dto = new ProductPurchaseDTO();
        BeanUtils.copyProperties(productPurchaseEntity, dto);
        return dto;
    }

    public ProductPurchaseDTO productPurchaseExpandToDto(ProductPurchaseExpand productPurchaseExpand) {
        ProductPurchaseDTO dto = new ProductPurchaseDTO();
        BeanUtils.copyProperties(productPurchaseExpand, dto);
        return dto;
    }


    public ProductPurchaseEntity dtoToProductPurse(ProductPurchaseDTO dto) {
        ProductPurchaseEntity productPurchase = new ProductPurchaseEntity();
        BeanUtils.copyProperties(dto, productPurchase);
        return productPurchase;
    }




}