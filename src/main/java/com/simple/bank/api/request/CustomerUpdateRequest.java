package com.simple.bank.api.request;

import com.simple.bank.dto.OperContext;
import com.simple.bank.dto.CustomerDTO;
import lombok.Data;

@Data
public class CustomerUpdateRequest {
    private OperContext operContext;
    private CustomerDTO customer;

}