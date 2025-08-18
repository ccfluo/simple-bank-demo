package com.simple.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {
    private Long auditId;
    private String entityType;
    private Long entityId;
    private String operationType;
    private String user;
    private LocalDateTime maintenanceTime;
    private String beforeData;
    private String afterData;
    private String ipAddress;
}