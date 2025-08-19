package com.simple.bank.exception;

import com.alibaba.csp.sentinel.slots.block.*;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.simple.bank.dto.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@Component
@ResponseBody
public class SentinelGlobalHandler implements BlockExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            BlockException ex
    ) throws Exception {
        System.out.println("Phoebe Sentinel 限流处理器被调用！");
        String code = "FLOW_LIMIT";
        String msg = "请求被限制";
        int status = HttpStatus.TOO_MANY_REQUESTS.value(); // 默认429

        // 根据异常类型细化处理
        if (ex instanceof FlowException) {
            code = "FLOW_LIMIT";
            msg = "请求频率过高，请稍后重试";
        } else if (ex instanceof DegradeException) {
            code = "TEMP_DEGRADE";
            msg = "service temporarily degraded";
            status = HttpStatus.SERVICE_UNAVAILABLE.value(); // 503
        } else if (ex instanceof ParamFlowException) {
            code = "PARM_FLOW";
            msg = "Hot Parm Blocking";
        } else if (ex instanceof SystemBlockException) {
            code = "SYS_BLOCK";
            msg = "System Blocking";
            status = HttpStatus.SERVICE_UNAVAILABLE.value(); // 503 ==> Phoebe to be checked
        } else if (ex instanceof AuthorityException) {
            code = "AUTH_DENY";
            msg = "权限校验未通过";
            status = HttpStatus.UNAUTHORIZED.value(); // 401
        }


        // 返回JSON响应
//        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
//        response.getWriter().print(
//                String.format(
//                        "{\"code\": %d, \"msg\": \"%s\", \"timestamp\": %d}",
//                        status, msg, System.currentTimeMillis()
//                )
//        );
        // 2. 构建你的 Response 对象
        Response errorResponse = new Response(code, msg);

        // 3. 外层包装为 { "response": { ... } }
        Map<String, Object> result = new HashMap<>();
        result.put("response", errorResponse);

        // 4. 设置响应格式为 JSON
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(objectMapper.writeValueAsString(result)); // 序列化结果
        out.flush();
        out.close();
    }
}