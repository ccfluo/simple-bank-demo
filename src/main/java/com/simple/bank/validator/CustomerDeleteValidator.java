package com.simple.bank.validator;

import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.biz.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // 注入Spring容器，便于在Service中调用
public class CustomerDeleteValidator {

    @Autowired
    private OtherService otherService;

    public void Validate(Long customerId) {
        // 1. verify customerId;
        validateCustomerId(customerId);

        // 2. check if customer exists or not
        if (!otherService.isCustomerExists(customerId)) {
            throw new BusinessException("NOT_FOUND", "Customer to be deleted not existing");
        }

    }

    private void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new BusinessException("INVALID_FIELD",
                    "Customer ID must not be empty or <=0");
        }
    }

}
