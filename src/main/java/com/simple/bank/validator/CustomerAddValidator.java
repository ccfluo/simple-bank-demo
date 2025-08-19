package com.simple.bank.validator;

import com.simple.bank.api.request.CustomerAddRequest;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // 注入Spring容器，便于在Service中调用
public class CustomerAddValidator {

    @Autowired
    private OtherService otherService;

    public void Validate(CustomerAddRequest customerAddRequest) {
        // 1. verify input fields;
        validateInputFields(customerAddRequest.getCustomer());

        // 2. add customer Dedupe here in future!!

    }

    /* customerId非空且有效 */
    private void validateInputFields(CustomerDTO customerDTO) {
        if (customerDTO.getCustomerId() != null) {
            throw new BusinessException("INVALID_FIELD",
                    "Customer ID provided which should be auto assigned");
        }
        if (customerDTO.getName() == null || customerDTO.getName().trim().isEmpty()){
            throw new BusinessException("INVALID_FIELD",
                    "Customer name is empty");
        };

       if (customerDTO.getEmail() == null || customerDTO.getEmail().trim().isEmpty()){
           throw new BusinessException("INVALID_FIELD",
                   "Customer email is empty");
       }

        if (customerDTO.getMobile() == null || customerDTO.getMobile().trim().isEmpty()){
            throw new BusinessException("INVALID_FIELD",
                    "Customer mobile is empty");
        }
    }
}
