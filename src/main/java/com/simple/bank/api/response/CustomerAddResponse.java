package com.simple.bank.api.response;

import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CustomerAddResponse {
    private Response response;

    public CustomerAddResponse(String message){
        response = new Response(message);
    }

}
