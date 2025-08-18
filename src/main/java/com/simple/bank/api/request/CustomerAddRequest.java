package com.simple.bank.api.request;

import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.dto.OperContext;
import lombok.Data;

@Data
public class CustomerAddRequest {
    private OperContext operContext;
    private CustomerDTO customer;
}