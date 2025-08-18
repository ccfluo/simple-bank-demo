package com.simple.bank.validator;

import com.simple.bank.api.request.AccountAddRequest;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.enums.AccountStatus;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component // 注入Spring容器，便于在Service中调用
public class AccountAddValidator {

    @Autowired
    private OtherService otherService;

    // 缓存枚举的所有有效名称（仅初始化一次，提高性能）
    private static final Set<String> VALID_STATUS_NAMES;
    static {
        VALID_STATUS_NAMES = Arrays.stream(AccountStatus.values())
                .map(AccountStatus::name) // 获取枚举常量的名称
                .collect(Collectors.toSet());
    }

    public void Validate(AccountAddRequest accountAddRequest) {
        // 1. verify input fields;
        validateInputFields(accountAddRequest.getAccount());
        validateAccountStatus(accountAddRequest.getAccount().getAccountStatus());

        // 2. add customer Dedupe here in future!!
//        if (otherService.isCustomerExists(customerDTO.getCustomerId())) {
//            throw new CustomerNotFound("Customer to be updated not existing");
//        };


    }

    /* customerId非空且有效 */
    private void validateInputFields(AccountDTO accountDTO) {
        if (accountDTO.getAccountId() != null) {
            throw new BusinessException("INVALID_FIELD",
                    "Account ID provided which should be auto assigned");
        }
        if (accountDTO.getType() == null || accountDTO.getType().trim().isEmpty()){
            throw new BusinessException("INVALID_FIELD",
                    "Type is empty");
        };

        if (accountDTO.getProductCode() == null || accountDTO.getProductCode().trim().isEmpty()){
            throw new BusinessException("INVALID_FIELD",
                    "Account Product Code is empty");
        }
        if (accountDTO.getBalance() == null){
            throw new BusinessException("INVALID_FIELD",
                    "Account Balance is empty");
        }
//        if (accountDTO.getAccountStatus() == null || accountDTO.getAccountStatus().trim().isEmpty()){
//            throw new BusinessException("INVALID_FIELD",
//                    "Account Status is empty");
//        }

        if (accountDTO.getOverDraft() == null){
            throw new BusinessException("INVALID_FIELD",
                    "Account overdraft is empty");
        }
        if (accountDTO.getInterestRate() == null){
            throw new BusinessException("INVALID_FIELD",
                    "Account interest rate is empty");
        }
        if (accountDTO.getCustomerId() == null){
            throw new BusinessException("INVALID_FIELD",
                    "Account customer id is empty");
        }
    }

    private void validateAccountStatus(String accountStatus) {
        if (accountStatus == null || accountStatus.trim().isEmpty()) {
            throw new BusinessException("INVALID_FIELD",
                    "Account Status is empty");
        }

        if (!VALID_STATUS_NAMES.contains(accountStatus)) {
            throw new BusinessException("INVALID_FIELD",
                    "Invalid Account Status");
        };

    }
}