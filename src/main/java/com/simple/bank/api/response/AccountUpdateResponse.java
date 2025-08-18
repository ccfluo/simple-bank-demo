package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "accountDTO"})
@Setter @Getter
public class AccountUpdateResponse {
    private Response response;
    private AccountDTO accountDTO;

    public AccountUpdateResponse(AccountDTO accountDTO, String message){
        this.response = new Response(message);
        this.accountDTO = accountDTO;
    }

}
