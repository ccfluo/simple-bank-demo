package com.simple.bank.service;

import com.simple.bank.dto.AccountDTO;

public interface AccountRedisService {
    void delete(Long accountId);
    AccountDTO get(Long accountId);
    void set(AccountDTO accountDTO);
}
