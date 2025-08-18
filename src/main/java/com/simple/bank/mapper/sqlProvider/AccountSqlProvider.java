package com.simple.bank.mapper.sqlProvider;

import com.simple.bank.entity.AccountEntity;
import org.apache.ibatis.jdbc.SQL;

public class AccountSqlProvider {

    public String dynamicAccountSelect(AccountEntity accountEntity) {
        // 使用MyBatis的SQL工具类构建SQL
        return new SQL() {{
            SELECT("account_id, type, product_code, balance, account_status, " +
                    "over_draft, interest_rate, customer_id, " +
                    "created_at, updated_at");
            FROM("account");
            if (accountEntity.getAccountId() != null) {
                WHERE("account_id = #{accountId}");
            }
            if (accountEntity.getType() != null && !accountEntity.getType().isEmpty()) {
                WHERE("type = #{type}");
            }
            if (accountEntity.getProductCode() != null && !accountEntity.getProductCode().isEmpty()) {
                WHERE("product_code = #{productCode}");
            }
            if (accountEntity.getBalance() != null) {
                WHERE("balance = #{balance}");
            }
            if (accountEntity.getAccountStatus() != null && !accountEntity.getAccountStatus().isEmpty()) {
                WHERE("account_status = #{account}");
            }
            if (accountEntity.getOverDraft() != null) {
                WHERE("over_draft = #{overDraft}");
            }
            if (accountEntity.getInterestRate() != null) {
                WHERE("interest_Rate = #{interestRate}");
            }
            if (accountEntity.getCustomerId() != null) {
                WHERE("customer_id = #{customerId}");
            }
            if (accountEntity.getCreatedAt() != null) {
                WHERE("created_at >= #{createdAt}");
            }
            if (accountEntity.getUpdatedAt()!= null) {
                WHERE("updated_at >= #{updatedAt}");
            }
        }}.toString();
    }

    // 动态更新SQL：只更新非null字段
    public String dynamicAccountUpdate(AccountEntity accountEntity) {
        return new SQL() {{
            UPDATE("account");
            if (accountEntity.getType() != null && !accountEntity.getType().isEmpty()) {
                SET("type = #{type}");
            }
            if (accountEntity.getProductCode() != null && !accountEntity.getProductCode().isEmpty()) {
                SET("product_code = #{productCode}");
            }
            if (accountEntity.getBalance() != null) {
                SET("balance = #{balance}");
            }
            if (accountEntity.getAccountStatus() != null && !accountEntity.getAccountStatus().isEmpty()) {
                SET("account_status = #{accountStatus}");
            }
            if (accountEntity.getOverDraft() != null) {
                SET("over_draft = #{overDraft}");
            }
            if (accountEntity.getInterestRate() != null) {
                SET("interest_Rate = #{interestRate}");
            }
            if (accountEntity.getCustomerId() != null) {
                SET("customer_id = #{customerId}");
            }
            if (accountEntity.getCreatedAt() != null) {
                SET("created_at = #{createdAt}");
            }
            if (accountEntity.getUpdatedAt()!= null) {
                SET("updated_at = #{updatedAt}");
            }
            WHERE("account_id = #{accountId}");
        }}.toString();
    }
}

