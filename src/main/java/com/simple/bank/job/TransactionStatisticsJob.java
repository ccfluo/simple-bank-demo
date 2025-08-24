package com.simple.bank.job;

import com.simple.bank.service.job.JobDailyReport;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class TransactionStatisticsJob extends QuartzJobBean {
    @Autowired
    JobDailyReport jobDailyReport;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("===== Start Transaction Statistics Job =====");
        jobDailyReport.generateDailyTransactionReport();
        log.info("===== End Transaction Statistics Job =====");
    }
}
