package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "account"})
@Setter @Getter
public class AccountUpdateResponse {
    private Response response;
    private AccountDTO account;

    public AccountUpdateResponse(AccountDTO accountDTO, String message){
        this.response = new Response(message);
        this.account = accountDTO;
    }

}
