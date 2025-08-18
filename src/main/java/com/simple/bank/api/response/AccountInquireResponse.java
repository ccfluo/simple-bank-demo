package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "accountDTO"})
@Setter @Getter
public class AccountInquireResponse {
    private Response response;
    private AccountDTO account;

    public AccountInquireResponse(AccountDTO accountDTO){
//        this.response = ResultSetter.success().getResponse();
        this.response = new Response();
        this.account = accountDTO;
    }

}
