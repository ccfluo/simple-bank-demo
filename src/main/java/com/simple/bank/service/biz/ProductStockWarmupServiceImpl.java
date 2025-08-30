package com.simple.bank.service.biz;

import com.simple.bank.entity.ProductEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class ProductStockWarmupServiceImpl implements ProductStockWarmupService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRedisService productRedisService;

    // warm up a product into redis
    @Override
    public boolean warmupProductStock(Long productId) throws BusinessException {
        // 1. validation
        ProductEntity productEntity = productMapper.getProductByIdForUpdate(productId);

        if (productEntity == null) {
            throw new BusinessException("NOT_FOUND", "product not found，productId: " + productId);
        }
        if (productEntity.getIsHot() == null || productEntity.getIsHot() != 1) {
            throw new BusinessException("PRODUCT_NOT_HOT", "no need warm up for non-hot product, productId : " + productId);
        }

        BigDecimal cachedProductAmount = productRedisService.getProductStockById(productId);
        if (cachedProductAmount == null) {
            if (productEntity.getRemainingAmount() == null) {
                throw new BusinessException("PRODUCT_STOCK_NULL",
                        "product remaining amount is 0，productId: " + productId);
            }
            productRedisService.setProductStockById(productId, productEntity.getRemainingAmount());
            log.info("[Product Warmup] Product warm up completed，productId: {}, remaining amount: {}",
                    productId, productEntity.getRemainingAmount());
            return true;
        } else {
            throw new BusinessException("WARM_UP", "Product has been warmed up by others, productId : " + productId);
        }
    }

    // warm up all hot products
    @Override
    public boolean batchWarmupHotProductStock() throws BusinessException {
        //Assume batchWarmup only happened during midnight when no product sale
        //  No need consider product stock update from others
        List<ProductEntity> hotProducts = productMapper.selectHotAndOnSaleProducts();
        if (hotProducts.isEmpty()) {
            log.info("[Product Warmup] No hot product on sale");
            return true;
        }

        int warmupProductCount = 0;
        for (ProductEntity product : hotProducts) {
            try {
                productRedisService.setProductStockById(product.getProductId(), product.getRemainingAmount());
                log.info("[Product Warmup] Batch warm up completed for productId: {}, remaining amount: {}",
                        product.getProductId(), product.getRemainingAmount());
                warmupProductCount++;
            } catch (Exception e) {
                // continue if only 1 product warm up failed
                log.error("[Product Warmup] Batch warm up failed for productId: {}", product.getProductId(), e);
            }
        }

        if (warmupProductCount == hotProducts.size()) {
            log.info("[Product Warmup] Total {} hot product(s) warmed up!", warmupProductCount);
            return true;
        } else {
            log.warn("[Product Warmup] Not all hot products warmed up! Total {} warmed up; Total {} not warm up {}.",
                    warmupProductCount, (hotProducts.size()-warmupProductCount) );
            return false;
        }
    }

    // clean up warmup product
    @Override
    public void clearProductStockCache(Long productId) {
        boolean deleteSuccess = productRedisService.deleteProductStockById(productId);
        if (deleteSuccess) {
            log.info("[Product Warmup] Cleaned up product stock for productId: {}", productId);
        } else {
            log.warn("[Product Warmup] Product stock not in warmup status for productId: {}", productId);
        }
    }

}