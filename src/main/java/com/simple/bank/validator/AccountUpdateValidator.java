package com.simple.bank.validator;

import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.exception.AccountNotFound;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // 注入Spring容器，便于在Service中调用
public class AccountUpdateValidator {

    @Autowired
    private OtherService otherService;

    public void Validate(AccountUpdateRequest accountUpdateRequest) {
        // 1. verify accountId <> null && > 0;
        Long accountId = accountUpdateRequest.getAccount().getAccountId();
        validateAccountId(accountId);

        // 2. check if account exists or not
        if (!otherService.isAccountExists(accountId)) {
            throw new AccountNotFound("Account to be updated not existing");
        };

        // 3. verify if any other field input besides customer id
        validateOtherFields(accountUpdateRequest.getAccount());
    }

    /* customerId非空且有效 */
    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new BusinessException("INVALID_FIELD",
                    "Account ID must not be null or negative");
        }

    }

    /* 验证除customerId外，至少有一个字段有值 */
    private void validateOtherFields(AccountDTO accountDTO) {
        // 检查所有可选字段（根据实际DTO中的字段调整）
        boolean hasType = accountDTO.getType() != null && !accountDTO.getType().trim().isEmpty();
        boolean hasProductCode = accountDTO.getProductCode() != null && !accountDTO.getProductCode().trim().isEmpty();
        boolean hasBalance = accountDTO.getBalance() != null;
        boolean hasAccountStatus = accountDTO.getAccountStatus() != null && !accountDTO.getAccountStatus().trim().isEmpty();
        boolean hasOverDraft = accountDTO.getOverDraft() != null;
        boolean hasIntestRate = accountDTO.getInterestRate() != null;
        boolean hasCustomerId = accountDTO.getCustomerId() != null;

        // 所有可选字段都为空时，验证失败
        if (!hasType && !hasProductCode && !hasBalance && !hasAccountStatus &&! hasOverDraft
                && !hasIntestRate && !hasCustomerId) { // 若有更多字段，用 && 连接
            throw new BusinessException("NO_UPDATE_FIELD",
                    "At least one field must be provided for update");
        }

    }
}
