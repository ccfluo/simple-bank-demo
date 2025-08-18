package com.simple.bank.mapper;

import com.simple.bank.entity.CustomerEntity;
import com.simple.bank.mapper.sqlProvider.CustomerSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CustomerMapper {
    @Select("SELECT COUNT(1) FROM customer WHERE customer_id =#{customerId}")
    int existsById(Long customerId);

    @Select("SELECT * FROM customer WHERE customer_id =#{customerId}")
//    @Results({
//            @Result(column = "id", property = "customerid") // table id → entity customerid
//    })
    CustomerEntity selectCustomerById(Long customerId);

    @Select("SELECT * FROM customer")
//    @Results({
//            @Result(column = "id", property = "customerid") // table id → entity customerid
//    })
    List<CustomerEntity> selectAllCustomers();

    @Insert("INSERT INTO customer (" +
            "email, name)" +
            " VALUES (#{email}, #{name})")
    //auto increate key id and assignback to entity field customerid
    // if remove option, record will be still inserted// but no customer_id returned into entity.
     @Options(useGeneratedKeys = true, keyProperty = "customerId", keyColumn = "customer_id")
     int insertCusotmer(CustomerEntity customerEntity);

    @UpdateProvider(type = CustomerSqlProvider.class, method = "dynamicCustomerUpdate")
    int dynamicUpdateCustomer(CustomerEntity customerEntity);

    @Delete("DELETE FROM customer WHERE customer_id =#{customerId}")
    int deleteCustomer(Long customerId);
}
