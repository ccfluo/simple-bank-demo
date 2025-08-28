package com.simple.bank.service.biz;

import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.entity.ProductPurchaseEntity;
import com.simple.bank.entity.TransferEntity;

import java.math.BigDecimal;

public interface MessageNotification {
    void sendTransactionNotification(AccountTransactionDTO accountTransactionDTO, String mobile, String email);

    void sendTransferNotification(TransferEntity transferEntity,
                                  CustomerDTO fromCustomerDTO,
                                  CustomerDTO toCustomerDTO,
                                  BigDecimal fromAccountBalance,
                                  BigDecimal toAccountBalance);
    void sendPurchaseNotification(ProductPurchaseEntity purchaseEntity, BigDecimal accountBalance, String mobile, String email);
}
