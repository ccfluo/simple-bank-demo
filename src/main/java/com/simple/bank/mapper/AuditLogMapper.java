package com.simple.bank.mapper;

import com.simple.bank.entity.AuditLogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {
    @Insert("INSERT INTO audit_log (" +
            "entity_type, entity_id, operation_type, user, " +
            "maintenance_time, before_data, after_data, ip_address" +
            ") VALUES (" +
            "#{entityType}, #{entityId}, #{operationType}, #{user}, " +
            "#{maintenanceTime}, #{beforeData}, #{afterData}, #{ipAddress}" +
            ")")
    void insertAuditLog(AuditLogEntity auditLog);
}