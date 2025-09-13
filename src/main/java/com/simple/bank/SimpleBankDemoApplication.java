package com.simple.bank;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.util.Arrays;
//import java.util.Map;

@Slf4j
@SpringBootApplication
@MapperScan(basePackages ="com.simple.bank.mapper")
//@Import(com.simple.bank.config.SimpleBankWebAutoConfiguration.class)
public class SimpleBankDemoApplication {
    //    @Autowired
    private ApplicationContext applicationContext;

//    // 構造器注入
//    public SimpleBankDemoApplication(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }

    public static void main(String[] args) {
        System.setProperty("project.name", "simplebank"); //phoebe : sentinel setup
        SpringApplication.run(SimpleBankDemoApplication.class, args);
    }

//    // 项目启动后执行，打印所有Bean
//    @Bean
//    public CommandLineRunner printBeans() {
//        return args -> {
//            // get all Bean names
//            String[] beanNames = applicationContext.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println("[Bean name]：" + beanName + "，[Bean type]：" + applicationContext.getBean(beanName).getClass().getName());
//            }
//
//            Map<String, RedisTemplate> redisTemplateBeans = applicationContext.getBeansOfType(RedisTemplate.class);
//
//            if (redisTemplateBeans.isEmpty()) {
//                System.out.println("【诊断】未找到任何 RedisTemplate 类型的 Bean，可能 Redis 自动配置未生效！");
//            } else {
//                System.out.println("【诊断】找到 RedisTemplate  Bean，名称列表：" + redisTemplateBeans.keySet());
//            }
//        };
//    }


//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        System.out.println(event.getApplicationContext().getEnvironment().getProperty("simple.bank") + "------");
//    }


}

