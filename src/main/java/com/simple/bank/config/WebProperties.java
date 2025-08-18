package com.simple.bank.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "simple.bank")
@Validated
@Getter @Setter
public class WebProperties {

    @NotNull(message = "Admin API can't be null")
    private Api adminApi = new Api("/admin", "**.controller.**");

//    @NotNull(message = "Admin UI can't be null")
//    private Ui adminUi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Valid
    public static class Api {

        @NotEmpty(message = "API prefix can't be null")
        private String prefix;

        @NotEmpty(message = "Controller package can't be null")
        private String controller;

    }
//
//    @Data
//    @Valid
//    public static class Ui {
//
//        /**
//         * 访问地址
//         */
//        private String url;
//
//    }

}
