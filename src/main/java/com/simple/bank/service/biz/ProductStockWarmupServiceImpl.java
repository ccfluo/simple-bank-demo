package com.simple.bank.service.biz;

import com.simple.bank.entity.ProductEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductStockWarmupServiceImpl implements ProductStockWarmupService {
//    private static final long expirySeconds = 86400;  //86400s = 24 hours
    private static final long expirySeconds = 300;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRedisService productRedisService;

    // warm up a product into redis
    @Override
    public boolean warmupProductStock(Long productId) throws BusinessException {
        // 1. 校验产品存在且为热卖产品 TODO: check how to handle if other trx is updating product...
        ProductEntity productEntity = productMapper.getProductById(productId);

        if (productEntity == null) {
            throw new BusinessException("NOT_FOUND", "product not found，productId: " + productId);
        }
        if (productEntity.getIsHot() == null || productEntity.getIsHot() != 1) {
            throw new BusinessException("PRODUCT_NOT_HOT", "non-hot product no need warm up，productId: " + productId);
        }
        if (productEntity.getRemainingAmount() == null) {
            throw new BusinessException("PRODUCT_STOCK_NULL", "product remaining amount is 0，productId: " + productId);
        }

        // 2. 转换金额为“分”存储（避免Redis浮点精度问题）
//        String redisKey = getRedisStockKey(productId);
//        long stockInCent = convertYuanToCent(product.getRemainingAmount());

        // 3. 存入Redis（设置过期时间：24小时，避免缓存永久有效）
        productRedisService.setProductStockById(productId,
                productEntity.getRemainingAmount(), expirySeconds);
        log.info("Product warm up into redis，productId: {}, remaining amount: {}",
                productId, productEntity.getRemainingAmount());
        return true;
    }

    // warm up all hot products
    @Override
    public boolean batchWarmupHotProductStock() throws BusinessException {

        // 1. 查询所有“在售且热卖”的产品（避免预热下架产品）
        List<ProductEntity> hotProducts = productMapper.selectHotAndOnSaleProducts();
        if (hotProducts.isEmpty()) {
            log.info("no hot product on sale");
            return true;
        }

        // 2. 批量预热到Redis
        int warmupProductCount = 0;
        for (ProductEntity product : hotProducts) {
            try {
                productRedisService.setProductStockById(product.getProductId(),
                        product.getRemainingAmount(), expirySeconds);
                log.info("[Product Warmup] Batch warm up for productId: {}, remaining amount: {}",
                        product.getProductId(), product.getRemainingAmount());
                warmupProductCount++;
            } catch (Exception e) {
                // 单个产品预热失败不影响整体，记录日志后继续
                log.error("[Product Warmup] Batch warm up failed for productId: {}", product.getProductId(), e);
                return false;
            }
        }

        log.info("[Product Warmup] Total {} hot product warmed up!", warmupProductCount);
        return true;
    }

    // clean up warmup product
    @Override
    public void clearProductStockCache(Long productId) {
        boolean deleteSuccess = productRedisService.deleteProductStockById(productId);
        if (deleteSuccess) {
            log.info("Cleaned up product stock for productId: {}", productId);
        } else {
            log.warn("Product stock not in redis for productId: {}", productId);
        }
    }

}