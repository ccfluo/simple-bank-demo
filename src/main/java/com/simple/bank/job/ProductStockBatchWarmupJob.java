package com.simple.bank.job;

import com.simple.bank.service.biz.ProductRedisService;
import com.simple.bank.service.biz.ProductService;
import com.simple.bank.service.biz.ProductStockWarmupService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class ProductStockBatchWarmupJob extends QuartzJobBean {

    @Autowired
    private ProductStockWarmupService productStockWarmupService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("[Job scheduler] Starting to warmup hot product...");
        boolean result = productStockWarmupService.batchWarmupHotProductStock();
        if (result) {
            log.info("[Job scheduler] Product warmup completed");
        }else {
            log.error("[Job scheduler] Product warmup failed");
        }
    }
}