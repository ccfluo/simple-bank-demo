package com.simple.bank.validator;

import com.simple.bank.api.request.CustomerUpdateRequest;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.biz.CustomerInquireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // 注入Spring容器，便于在Service中调用
public class CustomerUpdateValidator {

    @Autowired
    private CustomerInquireService customerInquireService;

    public void Validate(CustomerUpdateRequest customerUpdateRequest) {
        // 1. verify customerId <> null && > 0;
        Long customerId = customerUpdateRequest.getCustomer().getCustomerId();
        validateCustomerId(customerId);

        // 2. check if customer exists or not
        if (!customerInquireService.isCustomerExists(customerId)) {
            throw new BusinessException("NOT_FOUND","Customer to be updated not existing");
        }

        // 3. verify if any other field input besides customer id
        validateOtherFields(customerUpdateRequest.getCustomer());
    }

    /* customerId非空且有效 */
    private void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new BusinessException("INVALID_FIELD",
                    "Customer ID must not be empty or <=0");
        }

    }

    /* 验证除customerId外，至少有一个字段有值 */
    private void validateOtherFields(CustomerDTO customerDTO) {
        // 检查所有可选字段（根据实际DTO中的字段调整）
        boolean hasName = customerDTO.getName() != null && !customerDTO.getName().trim().isEmpty();
        boolean hasEmail = customerDTO.getEmail() != null && !customerDTO.getEmail().trim().isEmpty();
        boolean hasMobile = customerDTO.getMobile() != null && !customerDTO.getMobile().trim().isEmpty();
        // 若有其他字段（如phone、address等），继续添加判断

        // 所有可选字段都为空时，验证失败
        if (!hasName && !hasEmail && !hasMobile) { // 若有更多字段，用 && 连接
            throw new BusinessException("NO_UPDATE_FIELD",
                    "At least one field must be provided for update");
        }
    }
}
