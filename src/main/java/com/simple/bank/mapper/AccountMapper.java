package com.simple.bank.mapper;

import com.simple.bank.entity.AccountEntity;
import com.simple.bank.mapper.sqlProvider.AccountSqlProvider;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

public interface AccountMapper {
    @Select("SELECT COUNT(1) FROM account WHERE account_id =#{accountId}")
    int existsById(Long accountId);

    @Select("SELECT * FROM account WHERE account_id =#{accountId}")
    AccountEntity getAccountById(Long accountId);

    @Select("SELECT * FROM account WHERE account_id =#{accountId}  FOR UPDATE")
    AccountEntity getAccountByIdForUpdate(Long accountId);

    @Select("SELECT * FROM account")
    List<AccountEntity> getAllAccounts();

    @Select("SELECT * FROM account WHERE customer_id = #{customerId}")
    List<AccountEntity> getAccountByCustomerId(Long customerID);

    @Insert("INSERT INTO account (" +
            "type, product_code, balance, account_status, over_draft, interest_rate, " +
            "customer_id)" +
            " VALUES (#{type}, #{productCode}, #{balance}, #{accountStatus}," +
            "#{overDraft}, #{interestRate}, #{customerId})")
    @Options(useGeneratedKeys = true, keyProperty = "accountId", keyColumn = "account_id")
    int insertAccount(AccountEntity accountEntity);

    @UpdateProvider(type = AccountSqlProvider.class, method = "dynamicAccountUpdate")
    int dynamicUpdateAccount(AccountEntity accountEntity);

    @Delete("DELETE FROM account WHERE account_id =#{accountId}")
    int deleteAccount(Long customerId);

    @Update("UPDATE bank_account SET balance = #{newBalance}, updated_at = NOW() WHERE account_id = #{accountId}")
    int updateAccountBalance(Long accountId, BigDecimal newBalance);

}
