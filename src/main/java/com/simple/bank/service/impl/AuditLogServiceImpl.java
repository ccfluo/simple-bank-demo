package com.simple.bank.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.bank.entity.AuditLogEntity;
import com.simple.bank.mapper.AuditLogMapper;
import com.simple.bank.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // 默认IP（实际项目可从请求中获取）
    private static final String DEFAULT_IP = "127.0.0.1";

    @Override
    @Transactional
    public void logCustomerOperation(Long customerId, String operationType, String beforeData, String afterData, String user) {
        AuditLogEntity log = new AuditLogEntity();
        log.setEntityType("CUSTOMER");
        log.setEntityId(customerId);
        log.setOperationType(operationType);
        log.setUser(user); // 对应表中 user 字段
        log.setMaintenanceTime(LocalDateTime.now()); // 对应 maitance_time
        log.setBeforeData(beforeData);
        log.setAfterData(afterData);
        log.setUser(user);
        log.setIpAddress(DEFAULT_IP);
        auditLogMapper.insertAuditLog(log);
    }

    @Override
    public void logAccountOperation(Long accountId, String operationType, String beforeData, String afterData, String user) {
        AuditLogEntity log = new AuditLogEntity();
        log.setEntityType("ACCOUNT");
        log.setEntityId(accountId);
        log.setOperationType(operationType);
        log.setUser(user);
        log.setMaintenanceTime(LocalDateTime.now());
        log.setBeforeData(beforeData);
        log.setAfterData(afterData);
        log.setIpAddress(DEFAULT_IP);
        auditLogMapper.insertAuditLog(log);
    }

    @Override
    public String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "JSON conversion failed: " + e.getMessage();
        }
    }
}