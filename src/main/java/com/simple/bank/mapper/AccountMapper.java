package com.simple.bank.mapper;

import com.simple.bank.entity.AccountEntity;
import com.simple.bank.mapper.sqlProvider.AccountSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface AccountMapper {
    @Select("SELECT COUNT(1) FROM account WHERE account_id =#{accountId}")
    int existsById(Long accountId);

    @Select("SELECT * FROM account WHERE account_id =#{accountId}")
    AccountEntity selectAccountById(Long accountId);

    @Select("SELECT * FROM account")
    List<AccountEntity> selectAllAccounts();

    @Select("SELECT * FROM account WHERE customer_id = #{customerId}")
    List<AccountEntity> selectAccountByCustomerId(Long customerID);

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
}
