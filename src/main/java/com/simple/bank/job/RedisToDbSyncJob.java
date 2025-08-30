package com.simple.bank.job;

import com.simple.bank.service.biz.ProductRedisService;
import com.simple.bank.service.biz.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class RedisToDbSyncJob extends QuartzJobBean {

    @Autowired
    private ProductRedisService productRedisService;

    @Autowired
    private ProductService productService;  // 假设存在该Service用于操作MySQL

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("[Product Stock Sync] Start product stock synchronization from Redis to DB");
        try {
            // 1. 从Redis获取所有产品的剩余金额
            Map<Long, BigDecimal> productRemainingAmounts = productRedisService.getAllProductsRemainingAmount();

            if (productRemainingAmounts.isEmpty()) {
                log.info("[Product Stock Sync] No product remaining amount found in Redis");
                return;
            }

            // 2. 批量同步到MySQL
            productService.batchUpdateRemainingAmount(productRemainingAmounts);
            log.info("[Product Stock Sync] Successfully sync {} products' remaining amount to MySQL", productRemainingAmounts.size());

        } catch (Exception e) {
            log.error("[Product Stock Sync] Failed to sync remaining amount from Redis to MySQL", e);
        }
        log.info("[Product Stock Sync] Product stock synchronization from Redis to DB completed");
    }
}