package com.simple.bank.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.simple.bank.dto.Response;
import com.simple.bank.utlility.ExceptionFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import static org.aspectj.util.LangUtil.unwrapException;

@Slf4j
@ControllerAdvice // 全局异常处理
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)  //告诉 Spring “当系统抛出BusinessException 异常时，由这个方法处理
    @ResponseBody //序列化方法返回值为 JSON/XML 响应体
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleBusinessException(BusinessException e) {
        log.warn("Business Exception：{}", e); // 警告级别，无需堆栈
        return (new Response(e.getCode(), ExceptionFormatter.format(e)));
    }

    @ExceptionHandler(CustomerNotFound.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleCustomerNotFound(CustomerNotFound e) {
        log.warn("Customer Not Found：{}", e); // 警告级别，无需堆栈
        return (new Response("NOT_FOUND", ExceptionFormatter.format(e))); // 统一响应格式
    }

    @ExceptionHandler(AccountNotFound.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleAccountNotFound(AccountNotFound e) {
        log.warn("Account Not Found：{}", e); // 警告级别，无需堆栈
        return (new Response("NOT_FOUND", ExceptionFormatter.format(e))); // 统一响应格式
    }

    // 处理JSON解析错误（如格式错误、字段名缺失双引号等）
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleJsonParseError(HttpMessageNotReadableException e) {
        // 提取错误信息（Jackson会返回具体错误位置，如"Unexpected character '}'"）
        String errorMsg = "Invalid JSON format: " + e.getMostSpecificCause().getMessage();

        log.error("Invalid JSON format：", e); // 错误级别，记录堆栈
        return (new Response("JSON_ERROR", errorMsg)); // 统一响应格式
    }

    // 处理JSON解析错误（如格式错误、字段名缺失双引号等）
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleNoResourceFoundError(NoResourceFoundException e) {
        log.error("Resource not found：", e); // 错误级别，记录堆栈
        return (new Response("RESOURCE_NOT_FOUND", ExceptionFormatter.format(e))); // 统一响应格式
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        // 获取错误详情：期望的类型、实际传入的值、参数名
        String expectedType = e.getRequiredType().getSimpleName();
        String actualValue = e.getValue() != null ? e.getValue().toString() : "null";
        String parameterName = e.getName();

        log.error("parameter type error：", e); // 错误级别，记录堆栈
        return (new Response("PARM_TYPE_ERROR", ExceptionFormatter.format(e))); // 统一响应格式
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleSystemException(Exception e) throws BlockException {
//        // 1. 解析被包装的异常（如 AOP 代理导致的 UndeclaredThrowableException）
//        Throwable realException = unwrapException(e);
//
//        // 2. 若原始异常是 BlockException，抛给 SentinelGlobalHandler
//        if (realException instanceof BlockException) {
//            throw (BlockException) realException;
//        }
//
//
//        // 3. 处理其他异常
        log.error("System Exception：", e);
        return new Response("SYSTEM_ERROR", "System Error: " + ExceptionFormatter.format(e));
    }


    @ExceptionHandler(value = BlockException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Response handleblockException(Exception e) {
        log.error("Too Many Requests", e); // 警告级别，无需堆栈
        return(new Response("TOO_MANY_REQUESTS", ExceptionFormatter.format(e)));

    }

}
