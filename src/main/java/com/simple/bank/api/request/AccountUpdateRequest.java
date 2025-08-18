package com.simple.bank.api.request;

import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.OperContext;
import lombok.Data;

@Data
public class AccountUpdateRequest {
    private OperContext operContext;
    private AccountDTO account;
}