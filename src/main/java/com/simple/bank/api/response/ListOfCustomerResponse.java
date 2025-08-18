package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonPropertyOrder({"response", "customerDTOList"})
@Setter @Getter
public class ListOfCustomerResponse {
    private Response response;
    private List<CustomerDTO> customerDTOList;

    public ListOfCustomerResponse(List<CustomerDTO> customerDTOList){
//        this.response = ResultSetter.success().getResponse();
        this.response = new Response();
        this.customerDTOList = customerDTOList;
    }

}
