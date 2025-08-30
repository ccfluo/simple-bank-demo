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

// same as RedisToDbSyncJob.
// RedisToDbSyncDailyJob: schedule after jobStart @2am daily
// RedisToDbSyncJob: scheduled every 5 minutes from 8am ~ 5pm
@Slf4j
public class RedisToDbSyncDailyJob extends QuartzJobBean {

    @Autowired
    private ProductRedisService productRedisService;

    @Autowired
    private ProductService productService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("[Job scheduler] Starting product stock synchronization from Redis to DB...");
        try {
            // 1. get product stock from redis
            Map<Long, BigDecimal> productRemainingAmounts = productRedisService.getAllProductStock();

            if (productRemainingAmounts.isEmpty()) {
                log.info("[Job scheduler] No product stock found in Redis");
                return;
            }

            // 2. update product stock into DB
            productService.batchUpdateProductStock(productRemainingAmounts);
            log.info("[Job scheduler] Successfully sync {} products' stock to DB", productRemainingAmounts.size());

        } catch (Exception e) {
            log.error("[Job scheduler] Failed to sync product stock from Redis to DB", e);
        }
        log.info("[Job scheduler] Product stock synchronization from Redis to DB completed");
    }
}