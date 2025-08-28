package com.simple.bank.service.biz;

import com.simple.bank.dto.AccountDTO;
import com.simple.bank.exception.BusinessException;

import java.util.List;

public interface AccountInquireService {
    AccountDTO getAccountById(Long accountId) throws BusinessException;

    List<AccountDTO> getAllAccounts() throws BusinessException;

    List<AccountDTO> getAccountByCustomerId(Long customerId) throws BusinessException;


}
