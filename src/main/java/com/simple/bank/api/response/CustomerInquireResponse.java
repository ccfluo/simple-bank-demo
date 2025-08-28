package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "customer"})
@Setter @Getter
public class CustomerInquireResponse {
    private Response response;
    private CustomerDTO customer;

    public CustomerInquireResponse(CustomerDTO customerDTO){
//        this.response = ResultSetter.success().getResponse();
        this.response = new Response();
        this.customer = customerDTO;
    }

}
