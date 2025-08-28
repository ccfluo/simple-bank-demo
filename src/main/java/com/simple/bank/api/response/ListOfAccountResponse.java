package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonPropertyOrder({"response", "accounts"})
@Setter
@Getter
public class ListOfAccountResponse {
    private Response response;
    private List<AccountDTO> accounts;

    public ListOfAccountResponse(List<AccountDTO> accountDTOList){
//        this.response = ResultSetter.success().getResponse();
        this.response = new Response();
        this.accounts = accountDTOList;
    }

}
