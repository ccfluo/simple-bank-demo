package com.simple.bank.validator;

import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.biz.AccountInquireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // 注入Spring容器，便于在Service中调用
public class AccountDeleteValidator {

    @Autowired
    private AccountInquireService accountInquireService;

    public void Validate(Long accountId) {
        // 1. verify accountId;
        validateAccountId(accountId);

        // 2. check if account exists or not
        if (!accountInquireService.isAccountExists(accountId)) {
            throw new BusinessException("NOT_FOUND", "Account to be deleted not existing");
        }
    }

    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new BusinessException("INVALID_FIELD",
                    "Account Id must not be empty or <=0");
        }
    }

}
