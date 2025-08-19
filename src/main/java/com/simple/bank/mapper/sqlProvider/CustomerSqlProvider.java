package com.simple.bank.mapper.sqlProvider;

import com.simple.bank.entity.CustomerEntity;
import org.apache.ibatis.jdbc.SQL;

public class CustomerSqlProvider {

    public String dynamicCustomerSelect(CustomerEntity customerEntity) {
        // 使用MyBatis的SQL工具类构建SQL
        return new SQL() {{
            SELECT("customer_id, name, email, created_at, updated_at");
            FROM("customer");
            if (customerEntity.getCustomerId() != null) {
                WHERE("customer_id = #{customerId}");
            }
            if (customerEntity.getName() != null && !customerEntity.getName().isEmpty()) {
                WHERE("name LIKE CONCAT('%', #{name}, '%')");
            }
            if (customerEntity.getEmail() != null && !customerEntity.getEmail().isEmpty()) {
                WHERE("email = #{email}");
            }
            if (customerEntity.getMobile() != null && !customerEntity.getMobile().isEmpty()) {
                WHERE("mobile= #{mobile}");
            }
            if (customerEntity.getCreatedAt() != null) {
                WHERE("created_at >= #{createdAt}");
            }
            if (customerEntity.getUpdatedAt() != null) {
                WHERE("updated_at >= #{updatedAt}");
            }
        }}.toString();
    }

    // 动态更新SQL：只更新非null字段
    public String dynamicCustomerUpdate(CustomerEntity customerEntity) {
        return new SQL() {{
            UPDATE("customer");
            if (customerEntity.getName() != null && !customerEntity.getName().isEmpty()) {
                SET("name = #{name}");
            }
            if (customerEntity.getEmail() != null && !customerEntity.getEmail().isEmpty()) {
                SET("email = #{email}");
            }
            if (customerEntity.getMobile() != null && !customerEntity.getMobile().isEmpty()) {
                SET("mobile = #{mobile}");
            }
//            if (customerEntity.getUpdatedAt() != null) {
//                SET("updated_at = #{updatedAt}");
//            }
            // 必须有更新条件（主键），避免全表更新
            WHERE("customer_id = #{customerId}");
        }}.toString();
    }
}

