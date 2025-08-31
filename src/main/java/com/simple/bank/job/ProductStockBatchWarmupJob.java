package com.simple.bank.job;

import com.simple.bank.service.biz.ProductStockWarmupService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;


@Slf4j
public class ProductStockBatchWarmupJob extends QuartzJobBean {

    @Autowired
    private ProductStockWarmupService productStockWarmupService;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        log.info("[Job scheduler] Starting to warmup hot product...");
        boolean result = productStockWarmupService.batchWarmupHotProductStock();
        if (result) {
            log.info("[Job scheduler] Product warmup completed");
        }else {
            log.error("[Job scheduler] Product warmup failed");
        }
    }
}