package com.simple.bank.service;

import com.simple.bank.dto.AccountDTO;
import com.simple.bank.exception.AccountNotFound;

import java.util.List;

public interface AccountInquireService {
    AccountDTO getAccountById(Long accountId) throws AccountNotFound;

    List<AccountDTO> getAllAccounts() throws AccountNotFound;

    List<AccountDTO> getAccountByCustomerId(Long customerId) throws AccountNotFound;


}
