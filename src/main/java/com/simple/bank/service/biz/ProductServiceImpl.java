package com.simple.bank.service.biz;

import com.simple.bank.converter.ProductConverter;
import com.simple.bank.dto.ProductDTO;
import com.simple.bank.entity.ProductEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductConverter productConverter;

    @Autowired
    private ProductRedisService productRedisService;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getOnSaleProducts() throws BusinessException {
        // try to get from Redis
        List<ProductDTO> cachedProducts = productRedisService.getOnSaleProducts();
        if (cachedProducts != null && !cachedProducts.isEmpty()) {
            log.debug("Get on-sale products from Redis");
            return cachedProducts;
        }

        // data not in redis, inquire DB
        List<ProductEntity> productEntities = productMapper.selectOnSaleProducts();
        if (productEntities.isEmpty()) {
            throw new BusinessException("NOT_FOUND", "No on-sale financial products found");
        }

        // update redis after get data from DB
        List<ProductDTO> productDTOs = productEntities.stream()
                .map(productConverter::productToDto)
                .collect(Collectors.toList());
        productRedisService.setOnSaleProducts(productDTOs);
        log.debug("Get on-sale products from DB, total: {}", productDTOs.size());

        return productDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long productId) throws BusinessException {
        if (productId == null) {
            throw new BusinessException("INVALID_PARAM", "Product ID cannot be null");
        }

        // try to get product from redis
        ProductDTO cachedProduct = productRedisService.getById(productId);
        if (cachedProduct != null) {
            // validate if product in redis is still valid
            validateProductStatus(cachedProduct);
            log.info("Get product {} from Redis", productId);
            return cachedProduct;
        }

        // get from DB if not in redis
        ProductEntity productEntity = productMapper.getProductById(productId);
        if (productEntity == null) {
            throw new BusinessException("PRODUCT_NOT_FOUND", "Financial product with id " + productId + " not found");
        }

        ProductDTO productDTO = productConverter.productToDto(productEntity);
        validateProductStatus(productDTO);

        // update redis
        productRedisService.set(productDTO);
        log.info("Get product {} from DB", productId);

        return productDTO;
    }

    private void validateProductStatus(ProductDTO productDTO) throws BusinessException {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(productDTO.getStartDate())) {
            throw new BusinessException("PRODUCT_NOT_STARTED",
                    "Product " + productDTO.getProductName() + " not started yet (starts at " + productDTO.getStartDate() + ")");
        }
        if (now.isAfter(productDTO.getEndDate())) {
            throw new BusinessException("PRODUCT_EXPIRED",
                    "Product " + productDTO.getProductName() + " has expired (ended at " + productDTO.getEndDate() + ")");
        }

        if (!"ON_SALE".equals(productDTO.getStatus())) {
            throw new BusinessException("PRODUCT_NOT_ON_SALE",
                    "Product " + productDTO.getProductName() + " is not on sale (status: " + productDTO.getStatus() + ")");
        }
    }
}
