package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "customer"})
@Setter @Getter
public class CustomerUpdateResponse {
    private Response response;
    private CustomerDTO customer;

    public CustomerUpdateResponse(CustomerDTO customerDTO, String message){
        this.response = new Response(message);
        this.customer = customerDTO;
    }

}
