package com.simple.bank;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@MapperScan(basePackages ="com.simple.bank.mapper")
//@Import(com.simple.bank.config.SimpleBankWebAutoConfiguration.class)
public class SimpleBankDemoApplication {
    public static void main(String[] args) {
        System.setProperty("project.name", "simplebank"); //phoebe : sentinel setup
        SpringApplication.run(SimpleBankDemoApplication.class, args);
    }

//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        System.out.println(event.getApplicationContext().getEnvironment().getProperty("simple.bank") + "------");
//    }

// remove warmupHotProductStock :
//    if application restarted during product sale phase, warm up will clean up all data in redis
//       which will cause product stock in redis lost
//    @Bean
//    public CommandLineRunner warmupHotProductStock(ProductStockWarmupService warmupService) {
//        return args -> {
//            log.info("[Application activated] starting to warmup hot product...");
//            boolean result = warmupService.batchWarmupHotProductStock();
//            if (result) {
//                log.info("[Application activated] Product warmup successfully");
//            }else {
//                log.error("[Application activated] Product warmup failed");
//            }
//        };
//    }

}

