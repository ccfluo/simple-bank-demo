package com.simple.bank.config;

//import com.simple.bank.job.DemoJob01;
//import com.simple.bank.job.DemoJob02;
import com.simple.bank.job.TransactionStatisticsJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfiguration {

    public static class transactionStatisticsJobConfiguration {

        @Bean
        public JobDetail transactionStatisticsJob() {
            return JobBuilder.newJob(TransactionStatisticsJob.class)
                    .withIdentity("transactionStatisticsJob") // 名字为 transactionStatisticsJob
                    .storeDurably() // 没有 Trigger 关联的时候任务是否被保留。因为创建 JobDetail 时，还没 Trigger 指向它，所以需要设置为 true ，表示保留。
                    .build();
        }

        @Bean
        public Trigger transactionStatisticsJobTrigger() {
            // 基于 Quartz Cron 表达式的调度计划的构造器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.dailyAtHourAndMinute(2, 0);
            // Trigger 构造器
            return TriggerBuilder.newTrigger()
                    .forJob(transactionStatisticsJob()) // 对应 Job 为 demoJob02
                    .withIdentity("transactionStatisticsJobTrigger") // 名字为 demoJob02Trigger
                    .withSchedule(scheduleBuilder) // 对应 Schedule 为 scheduleBuilder
                    .build();
        }

    }
}