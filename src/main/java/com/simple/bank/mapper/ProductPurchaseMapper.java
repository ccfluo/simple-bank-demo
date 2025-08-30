package com.simple.bank.mapper;

import com.simple.bank.entity.ProductPurchaseEntity;
import com.simple.bank.entity.ProductPurchaseExpand;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductPurchaseMapper {

    // create a purchase record
    @Insert("INSERT INTO product_purchase (" +
            "product_id, customer_id, account_id, purchase_amount, purchase_time, status, " +
            "transaction_trace_id)" +
            " VALUES (#{productId}, #{customerId}, #{accountId}, #{purchaseAmount}," +
            "#{purchaseTime}, #{status}, #{transactionTraceId})")
    @Options(useGeneratedKeys = true, keyProperty = "purchaseId", keyColumn = "purchase_id")
    int insertPurchase(ProductPurchaseEntity entity);

//    // inquire customer purchase
//    @Select("SELECT product_purchase.*, wealth_product.product_name " +
//            "FROM product_purchase " +
//            "LEFT JOIN wealth_product ON product_purchase.product_id = wealth_product.product_id " +
//            "WHERE product_purchase.customer_id = #{customerId}")
//    List<ProductPurchaseEntity> getPurchaseByCustomerId(Long customerId);

    // inquire customer purchase
    @Select("SELECT product_purchase.*, " +
            "wealth_product.product_id AS wp_product_id, " +
            "wealth_product.* " +
            "FROM product_purchase " +
            "LEFT JOIN wealth_product ON product_purchase.product_id = wealth_product.product_id " +
            "WHERE product_purchase.customer_id = #{customerId}")
    List<ProductPurchaseExpand> getPurchaseByCustomerId(Long customerId);

    // inquire purchase by traceid
    @Select("SELECT product_purchase.*, " +
            "wealth_product.product_id AS wp_product_id, " +
            "wealth_product.* " +
            "FROM product_purchase " +
            "LEFT JOIN wealth_product ON product_purchase.product_id = wealth_product.product_id " +
            "WHERE transaction_trace_id = #{transactionTraceId}")
    ProductPurchaseExpand getPurchaseByTraceId(String transactionTraceId);

    @Select("SELECT COUNT(1) FROM product_purchase WHERE transaction_trace_id = #{transactionTraceId}")
    int existsByTraceId(String transactionTraceId);
}