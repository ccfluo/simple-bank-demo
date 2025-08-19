package com.simple.bank.config;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomRequestOriginParser implements RequestOriginParser {

    @Override
    public String parseOrigin(HttpServletRequest request) {
        // <X> 从 Header 中，获得请求来源
        String origin = request.getHeader("s-user");
        // <Y> 如果为空，给一个默认的
        if (StringUtils.isEmpty(origin)) {
            origin = "default";
        }
        return origin;
    }

}
