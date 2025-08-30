package com.simple.bank.config;

import com.simple.bank.job.JobEnd;
import com.simple.bank.job.JobStart;
import com.simple.bank.job.RedisToDbSyncJob;
import com.simple.bank.job.TransactionStatisticsJob;
import org.quartz.*;
import org.quartz.listeners.JobChainingJobListener;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfiguration {


    @Configuration
    public static class JobStartConfiguration {

        @Bean
        public JobDetail jobStart() {
            return JobBuilder.newJob(JobStart.class)
                    .withIdentity("jobStart")
                    .storeDurably()
                    .build();
        }

        @Bean
        public Trigger jobStartTrigger() {
            // 基于 Quartz Cron 表达式的调度计划的构造器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.dailyAtHourAndMinute(2, 0);
            // Trigger 构造器
            return TriggerBuilder.newTrigger()
                    .forJob(jobStart()) // 对应 Job 为 transactionStatisticsJob
                    .withIdentity("jobStartTrigger") // 名字为 jobStartTrigger
                    .withSchedule(scheduleBuilder) // 对应 Schedule 为 scheduleBuilder
                    .build();
        }
    }

    @Configuration
    public static class JobEndConfiguration {

        @Bean
        public JobDetail jobEnd() {
            return JobBuilder.newJob(JobEnd.class)
                    .withIdentity("jobEnd")
                    .storeDurably()
                    .build();
        }
    }

    @Configuration
    public static class transactionStatisticsJobConfiguration {

        @Bean
        public JobDetail transactionStatisticsJob() {
            return JobBuilder.newJob(TransactionStatisticsJob.class)
                    .withIdentity("transactionStatisticsJob") // 名字为 transactionStatisticsJob
                    .storeDurably() // 没有 Trigger 关联的时候任务是否被保留。因为创建 JobDetail 时，还没 Trigger 指向它，所以需要设置为 true ，表示保留。
                    .build();
        }
    }

    @Configuration
    public static class JobChainConfiguration {
        /**
         * Job dependency：TransactionStatisticsJob  → JobEnd
         */
        @Bean
        public JobListener jobChainingListener(
                JobDetail jobStart,
                JobDetail transactionStatisticsJob,
                JobDetail jobEnd) {
            JobChainingJobListener listener = new JobChainingJobListener("chainListener");
            listener.addJobChainLink(jobStart.getKey(), transactionStatisticsJob.getKey());
            listener.addJobChainLink(transactionStatisticsJob.getKey(), jobEnd.getKey());
            return listener;
        }

        /**
         * listener register into Scheduler
         */
        @Bean
        public SchedulerFactoryBeanCustomizer schedulerCustomizer(JobListener jobChainingListener) {
            return schedulerFactoryBean -> schedulerFactoryBean.setGlobalJobListeners(jobChainingListener);
        }

    }

    @Configuration
    public static class RedisToKafkaSyncJobConfiguration {

        @Bean
        public JobDetail redisToDbSyncJob() {
            return JobBuilder.newJob(RedisToDbSyncJob.class)
                    .withIdentity("redisToDbSyncJob")
                    .storeDurably()
                    .build();
        }

        @Bean
        public Trigger redisToKafkaSyncJobTrigger() {
            // Cron表达式：每5分钟执行一次（秒 分 时 日 月 周 年）
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");

            return TriggerBuilder.newTrigger()
                    .forJob(redisToDbSyncJob())
                    .withIdentity("redisToDbSyncJobTrigger")
                    .withSchedule(scheduleBuilder)
                    .build();
        }
    }
}