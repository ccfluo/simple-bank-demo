package com.simple.bank;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * @ClassName SimpleBankDemoApplication
 * @Description: 启动类
 * @Author Luo Chuanfen
 * @Date 2025/8/10
 * @Version V1.0
 **/

@SpringBootApplication
@MapperScan(basePackages ="com.simple.bank.mapper")
//@Import(com.simple.bank.config.SimpleBankWebAutoConfiguration.class)
public class SimpleBankDemoApplication {
    public static void main(String[] args) {
        System.setProperty("project.name", "simplebank"); //phoebe : sentinel setup
        SpringApplication.run(SimpleBankDemoApplication.class, args);
    }

    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println(event.getApplicationContext().getEnvironment().getProperty("simple.bank") + "------");
    }

}

