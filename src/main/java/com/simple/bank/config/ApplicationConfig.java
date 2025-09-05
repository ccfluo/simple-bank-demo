package com.simple.bank.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// batch load parameters for those prefix with simple.bank
@Component
@ConfigurationProperties(prefix = "simple.bank")
@Getter @Setter
public class ApplicationConfig {
    private LockConfig lock = new LockConfig();

    @Getter @Setter
    public static class LockConfig {
        private boolean enableLockforTransfer;
    }
}