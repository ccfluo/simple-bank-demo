package com.simple.bank.config;

//import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired   //phoebe @Resource
    private WebProperties webProperties;
    /**
     * 应用名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurePathMatch1(configurer, webProperties.getAdminApi());
    }

    private void configurePathMatch1(PathMatchConfigurer configurer, WebProperties.Api api) {
        log.error("Adding path prefix: {} for controllers matching package: {}", api.getPrefix(), api.getController());
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        //api.getPrefix() : the path to be added only if both 1 + 2 are true
        //1. isAnnotationPresent(RestController.class) : is class a RestController class - annotated by @RestController
        //2. check WebProperties.Api.controller （"**.controller.**"） = current controller's actual path
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getController(), clazz.getPackage().getName()));
    }
//
//    @Bean
//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    public GlobalExceptionHandler globalExceptionHandler(ApiErrorLogCommonApi apiErrorLogApi) {
//        return new GlobalExceptionHandler(applicationName, apiErrorLogApi);
//    }
//
//    @Bean
//    public GlobalResponseBodyHandler globalResponseBodyHandler() {
//        return new GlobalResponseBodyHandler();
//    }
//
//    @Bean
//    @SuppressWarnings("InstantiationOfUtilityClass")
//    public WebFrameworkUtils webFrameworkUtils(WebProperties webProperties) {
//        // 由于 WebFrameworkUtils 需要使用到 webProperties 属性，所以注册为一个 Bean
//        return new WebFrameworkUtils(webProperties);
//    }
//
//    // ========== Filter 相关 ==========
//
//    /**
//     * 创建 CorsFilter Bean，解决跨域问题
//     */
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
//        // 创建 CorsConfiguration 对象
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOriginPattern("*"); // 设置访问源地址
//        config.addAllowedHeader("*"); // 设置访问源请求头
//        config.addAllowedMethod("*"); // 设置访问源请求方法
//        // 创建 UrlBasedCorsConfigurationSource 对象
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config); // 对接口配置跨域设置
//        return createFilterBean(new CorsFilter(source), WebFilterOrderEnum.CORS_FILTER);
//    }
//
//    /**
//     * 创建 RequestBodyCacheFilter Bean，可重复读取请求内容
//     */
//    @Bean
//    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
//        return createFilterBean(new CacheRequestBodyFilter(), WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
//    }
//
//    /**
//     * 创建 DemoFilter Bean，演示模式
//     */
//    @Bean
//    @ConditionalOnProperty(value = "yudao.demo", havingValue = "true")
//    public FilterRegistrationBean<DemoFilter> demoFilter() {
//        return createFilterBean(new DemoFilter(), WebFilterOrderEnum.DEMO_FILTER);
//    }
//
//    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
//        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
//        bean.setOrder(order);
//        return bean;
//    }
//
//    /**
//     * 创建 RestTemplate 实例
//     *
//     * @param restTemplateBuilder {@link RestTemplateAutoConfiguration#restTemplateBuilder}
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
//        return restTemplateBuilder.build();
//    }

}
