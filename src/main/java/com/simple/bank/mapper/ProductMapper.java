package com.simple.bank.mapper;

import com.simple.bank.entity.ProductEntity;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {
    // inquire products on sale
    @Select("SELECT * FROM wealth_product WHERE status = 'ON_SALE'")
    List<ProductEntity> selectOnSaleProducts();

    //TODO to add start_date validation - need consider warm up before start date
    @Select("SELECT * FROM wealth_product " +
            "WHERE status = 'ON_SALE' " +
            "  AND is_hot > 0" +
            "  AND remaining_amount > 0" +
            "  AND end_date >= NOW()")
    List<ProductEntity> selectHotAndOnSaleProducts();

    // inquire product by id
    @Select("SELECT * FROM wealth_product WHERE product_id = #{productId}")
    ProductEntity getProductById(Long productId);

    // deduct product remaining amount
    @Update("UPDATE wealth_product " +
            "SET remaining_amount = remaining_amount - #{amount} " +
            "WHERE product_id = #{productId} " +
            "AND status = 'ON_SALE' " +  // product on sale
            "AND remaining_amount >= #{amount}")  // still have enough remaining amount
    int deductRemainingAmount(Long productId, BigDecimal amount);

    @Update("<script>" +
            "UPDATE wealth_product " +
            "SET remaining_amount = CASE " +
            "<foreach collection='list' item='item' index='index'>" +
            "WHEN product_id = #{item.productId} THEN #{item.remainingAmount} " +
            "</foreach>" +
            "END " +
            "WHERE product_id IN " +
            "<foreach collection='list' item='item' index='index' open='(' separator=',' close=')'>" +
            "#{item.productId} " +
            "</foreach>" +
            "</script>")
    int batchUpdateRemainingAmount(@Param("list") List<ProductEntity> products);

//    // create a new product
//    @Insert("INSERT INTO wealth_product (name, code, status, start_date, end_date, create_time, update_time) " +
//            "VALUES (#{name}, #{code}, #{status}, " +
//            "#{startDate}, #{endDate}, NOW(), NOW())")
//    @Options(useGeneratedKeys = true, keyProperty = "productId", keyColumn = "product_id")
//    int insert(ProductEntity product);
//
//    // update product status
//    @Update("UPDATE wealth_product SET status = #{status}, " +
//            "update_time = NOW() WHERE id = #{productId}")
//    int updateStatus(Long productId, String status);
//
//    // delete product
//    @Delete("DELETE FROM wealth_product WHERE id = #{id}")
//    int deleteById(Long id);

}
