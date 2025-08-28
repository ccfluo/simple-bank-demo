package com.simple.bank.mapper;

import com.simple.bank.entity.TransferEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TransferMapper {

    // 插入转账记录
    @Insert("INSERT INTO transfer_history (" +
            "transfer_trace_id, from_account_id, to_account_id, transfer_amount, " +
            "transaction_type, status, remark, transfer_time, created_at" +
            ") VALUES (" +
            "#{transferTraceId}, #{fromAccountId}, #{toAccountId}, #{transferAmount}, " +
            "#{transactionType}, #{status}, #{remark}, #{transferTime}, NOW()" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "transferId", keyColumn = "transfer_id")
    void insert(TransferEntity transferEntity);

    // 检查traceId是否已存在（防重复）
    @Select("SELECT COUNT(1) FROM transfer_history WHERE transfer_trace_id = #{transferTraceId}")
    int countByTraceId(String transferTraceId);
}