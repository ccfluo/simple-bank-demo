package com.simple.bank.service.biz;

import com.simple.bank.exception.BusinessException;

// Product Warm Service - load into redis
public interface ProductStockWarmupService {
    // warm up a product into redis
    boolean warmupProductStock(Long productId) throws BusinessException;

    // warm up all hot products (for initialization / refresh regularly
    boolean batchWarmupHotProductStock() throws BusinessException;

    // clean up warmup product (product off sale, remaining amount = 0 etc)
    void clearProductStockCache(Long productId);
}