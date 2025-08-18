package com.simple.bank.mapper;

import com.simple.bank.entity.AccountTransaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransactionMapper {

    @Insert("INSERT INTO transaction_history (" +
            "transaction_amount, transaction_type, account_id, account_balance, description)" +
            " VALUES (" +
            "#{transactionAmount}, #{transactionType}, #{accountId}, #{accountBalance}, #{description}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "transactionId", keyColumn = "transaction_id")
    int insertTransaction(AccountTransaction transaction);

    @Select("SELECT * FROM transaction_history WHERE account_id = #{accountId} ORDER BY transaction_date DESC")
    List<AccountTransaction> getTransactionsByAccountId(Long accountId);
}