package com.simple.bank.mapper;

import com.simple.bank.entity.ProductPurchaseEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductPurchaseMapper {
    // 新增购买记录
    @Insert("INSERT INTO product_purchase (" +
            "product_id, customer_id, account_id, purchase_amount, purchase_time, status, " +
            "transaction_trace_id)" +
            " VALUES (#{productId}, #{customerId}, #{accountId}, #{purchaseAmount}," +
            "#{purchaseTime}, #{status}, #{transactionTraceId})")
    @Options(useGeneratedKeys = true, keyProperty = "purchaseId", keyColumn = "purchase_id")
    int insertPurchase(ProductPurchaseEntity entity);

    // 查询用户购买记录
    @Select("SELECT * FROM product_purchase WHERE customer_id = #{customerId}")
    List<ProductPurchaseEntity> getPurchaseByCustomerId(Long customerId);

    // 按追踪ID查询（防重复购买）
    @Select("SELECT * FROM product_purchase WHERE transaction_trace_id = #{transactionTraceId}")
    ProductPurchaseEntity getPurchaseByTraceId(String transactionTraceId);
}