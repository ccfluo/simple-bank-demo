package com.simple.bank.config;

//import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
public class SimpleBankWebAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private WebProperties webProperties;

//    @Value("${spring.application.name}")
//    private String applicationName;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurePathMatch(configurer, webProperties.getAdminApi());
    }

    private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
        log.error("Adding path prefix: {} for controllers matching package: {}", api.getPrefix(), api.getController());
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        //api.getPrefix() : the path to be added only if both 1 + 2 are true
        //1. isAnnotationPresent(RestController.class) : is class a RestController class - annotated by @RestController
        //2. check WebProperties.Api.controller （"**.controller.**"） = current controller's actual path
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getController(), clazz.getPackage().getName()));
    }

}
