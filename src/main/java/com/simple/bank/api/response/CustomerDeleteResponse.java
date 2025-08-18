package com.simple.bank.api.response;

import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CustomerDeleteResponse {
    private Response response;

    public CustomerDeleteResponse(String messages){
        this.response = new Response(messages);
    }

}
