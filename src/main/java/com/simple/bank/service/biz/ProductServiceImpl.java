package com.simple.bank.service.biz;

import com.simple.bank.converter.ProductConverter;
import com.simple.bank.dto.ProductDTO;
import com.simple.bank.dto.ProductMiniDTO;
import com.simple.bank.entity.ProductEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.ProductMapper;
import com.simple.bank.service.redis.ProductRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ProductStockWarmupService productStockWarmupService;

    @Override
    @Transactional(readOnly = true)
    public List<ProductMiniDTO> getOnSaleProducts() throws BusinessException {
        List<ProductEntity> productEntities = productMapper.selectOnSaleProducts();
        if (productEntities.isEmpty()) {
            throw new BusinessException("NOT_FOUND", "No on-sale financial products found");
        }

        List<ProductMiniDTO> productMiniDTOs = productEntities.stream()
                .map(productConverter::productToMiniDto)
                .collect(Collectors.toList());

        return productMiniDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long productId) throws BusinessException {
        if (productId == null) {
            throw new BusinessException("INVALID_PARAM", "Product ID cannot be null");
        }

        // get from DB if not in redis
        ProductEntity productEntity = productMapper.getProductById(productId);
        if (productEntity == null) {
            throw new BusinessException("PRODUCT_NOT_FOUND", "Financial product with id " + productId + " not found");
        }

        ProductDTO productDTO = productConverter.productToDto(productEntity);
        validateProductStatus(productDTO);

        // if it's a hot product, update remaining amount from redis
        if (productDTO.getIsHot() != null && productDTO.getIsHot() > 0) {
            BigDecimal cachedRemainingAmount = productRedisService.getProductStockById(productId);
            if (cachedRemainingAmount == null) {
                // Redis缓存未命中，触发实时预热（避免缓存穿透）
                log.warn("Hot product Redis not hit，trigger warm up now，productId: {}", productId);
                productStockWarmupService.warmupProductStock(productId);
                // inquire redis again in case other txn has updated remaining amount in redis after warmup above
                cachedRemainingAmount = productRedisService.getProductStockById(productId);
            }

            productDTO.setRemainingAmount(cachedRemainingAmount);
        }

        return productDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateProductStock(Map<Long, BigDecimal> productRemainingAmounts) {
        if (productRemainingAmounts.isEmpty()) {
            return;
        }

        List<ProductEntity> products = productRemainingAmounts.entrySet().stream()
                .map(entry -> {
                    ProductEntity product = new ProductEntity();
                    product.setProductId(entry.getKey());
                    product.setRemainingAmount(entry.getValue());
                    return product;
                })
                .collect(Collectors.toList());

        int updatedCount = productMapper.batchUpdateRemainingAmount(products);
        if (updatedCount > 0) {
            log.info("Batch sync product stock to DB，updated count：{}", updatedCount);
        } else {
            log.warn("Product not found during batch sync product stock to DB.");
        }
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
