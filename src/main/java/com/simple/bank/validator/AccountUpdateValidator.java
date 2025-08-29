package com.simple.bank.validator;

import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.biz.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
            throw new BusinessException("NOT_FOUND", "Account to be updated not existing");
        }

        AccountDTO accountDTO = accountUpdateRequest.getAccount();

        // 3. verify if any other field input besides customer id
        validateOtherFields(accountDTO);

        //4. check if balance < 0
        if (accountDTO.getBalance() != null && accountDTO.getBalance().compareTo(BigDecimal.ZERO)< 0) {
            throw new BusinessException("INSUFFICIENT_BALANCE", "Insufficient Balance");
        }

        if (accountDTO.getOverDraft() != null && accountDTO.getOverDraft().compareTo(BigDecimal.ZERO)< 0) {
            throw new BusinessException("INVALID_FIELD", "Overdraft amount must be >= 0");
        }

        if (accountDTO.getInterestRate() != null && accountDTO.getInterestRate().compareTo(BigDecimal.ZERO)< 0) {
            throw new BusinessException("INVALID_FIELD", "Interest Rate must be >= 0");
        }

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
