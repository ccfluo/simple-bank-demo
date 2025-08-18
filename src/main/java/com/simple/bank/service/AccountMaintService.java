package com.simple.bank.service;

import com.simple.bank.api.request.AccountAddRequest;
import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.exception.AccountNotFound;
import com.simple.bank.exception.BusinessException;

public interface AccountMaintService {
    Long createAccount(AccountAddRequest accountAddRequest) throws BusinessException;
    AccountDTO updateAccount(AccountUpdateRequest accountUpdateRequest) throws AccountNotFound, BusinessException;
    int deleteAccount(Long accountId) throws AccountNotFound, BusinessException;
}
