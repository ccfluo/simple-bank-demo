package com.simple.bank.validator;

import com.simple.bank.exception.AccountNotFound;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // 注入Spring容器，便于在Service中调用
public class AccountDeleteValidator {

    @Autowired
    private OtherService otherService;

    public void Validate(Long accountId) {
        // 1. verify accountId;
        validateAccountId(accountId);

        // 2. check if customer exists or not
        if (!otherService.isAccountExists(accountId)) {
            throw new AccountNotFound("Account to be deleted not existing");
        };
    }

    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new BusinessException("INVALID_FIELD",
                    "Customer ID must not be null or negative");
        }
    }

}
