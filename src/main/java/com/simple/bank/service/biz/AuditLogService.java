package com.simple.bank.service.biz;

public interface AuditLogService {
    // 记录客户操作日志
    void logCustomerOperation(Long customerId, String operationType, String beforeData, String afterData, String user);

    // 记录账户操作日志
    void logAccountOperation(Long accountId, String operationType, String beforeData, String afterData, String user);

    // 对象转JSON字符串
    String toJson(Object obj);
}