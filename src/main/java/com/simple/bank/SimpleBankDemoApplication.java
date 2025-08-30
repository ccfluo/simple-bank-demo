package com.simple.bank;


import com.simple.bank.service.biz.ProductStockWarmupService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

    @Bean
    public CommandLineRunner warmupHotProductStock(ProductStockWarmupService warmupService) {
        return args -> {
            log.info("[Application activated] starting to warmup hot product...");
            boolean result = warmupService.batchWarmupHotProductStock();
            log.info("[Application activated] Product warmup: {}", result ? "successfully" : "failed" );
        };
    }

}

