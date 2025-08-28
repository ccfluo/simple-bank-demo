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
