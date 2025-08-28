package com.simple.bank.service.biz;

import com.simple.bank.api.request.AccountAddRequest;
import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.converter.AccountConverter;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.entity.AccountEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.AccountMapper;
import com.simple.bank.validator.AccountAddValidator;
import com.simple.bank.validator.AccountDeleteValidator;
import com.simple.bank.validator.AccountUpdateValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountMaintServiceImpl implements AccountMaintService {
    @Autowired
    private AccountMapper accountMapper;  // auto inject the mapper

    //  @Autowired 是 Spring 的依赖注入注解，
//  用于自动获取 Spring 容器中已管理的 Bean，并注入到当前类的属性中。
//  告诉 Spring“我需要这个类型的实例，请从容器里找一个给我
    @Autowired
    private AccountConverter accountConverter;
    @Autowired
    private AccountUpdateValidator accountUpdateValidator;
    @Autowired
    private AccountAddValidator accountAddValidator;
    @Autowired
    private AccountDeleteValidator accountDeleteValidator;
    @Autowired
    private AccountInquireService accountInquireService;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private AccountRedisService accountRedisService;

    @Override
    @Transactional
    public Long createAccount(AccountAddRequest accountAddRequest) throws BusinessException {
        accountAddValidator.Validate(accountAddRequest);
        AccountEntity accountEntity = accountConverter.accountDtoToEntity(accountAddRequest.getAccount());
        try{
            int result = accountMapper.insertAccount(accountEntity);
            if (result < 1) {
                throw new BusinessException("DB_INSERT_FAIL", "No record inserted, pls check backend");
            }
            accountRedisService.delete(accountEntity.getCustomerId());
            AccountDTO createdAccountDTO = accountInquireService.getAccountById(accountEntity.getAccountId());
            auditLogService.logAccountOperation(
                    accountEntity.getAccountId(),
                    "CREATE",
                    null,
                    auditLogService.toJson(createdAccountDTO),
                    accountAddRequest.getOperContext().getUserId()
            );
        }catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Account existing");
        }

        return accountEntity.getAccountId();
    }

    @Override
    @Transactional
    public AccountDTO updateAccount(AccountUpdateRequest accountUpdateRequest) throws BusinessException {

        accountUpdateValidator.Validate(accountUpdateRequest);
//        AccountDTO beforeAccountDTO = accountInquireService.getAccountById(accountUpdateRequest.getAccount().getAccountId());
        AccountEntity beforeAccountEntity = accountMapper.getAccountByIdForUpdate(accountUpdateRequest.getAccount().getAccountId());
        AccountDTO beforeAccountDTO = accountConverter.accountToDto(beforeAccountEntity);
        String beforeData = auditLogService.toJson(beforeAccountDTO);

        AccountEntity accountEntity = accountConverter.accountDtoToEntity(accountUpdateRequest.getAccount());
        try{
            int result = accountMapper.dynamicUpdateAccount(accountEntity);
            if (result < 1) {
                throw new BusinessException("DB_UPDATE_FAIL", "No record updated, pls check backend");
            }
            accountRedisService.delete(accountEntity.getAccountId());
            // get customer details after update
            AccountDTO updatedAccountDTO = accountInquireService.getAccountById(accountUpdateRequest.getAccount().getAccountId());
            auditLogService.logAccountOperation(
                    accountUpdateRequest.getAccount().getAccountId(),
                    "UPDATE",
                    beforeData,
                    auditLogService.toJson(updatedAccountDTO),
                    accountUpdateRequest.getOperContext().getUserId()
            );
            // redis delayed double deletion
            accountRedisService.delete(accountEntity.getAccountId());
            return updatedAccountDTO;
        } catch (DuplicateKeyException e) {
            throw new BusinessException("fDUPLICATE_KEY", "Customer existing");
        }
    }

    @Override
    public int deleteAccount(Long accountId) throws BusinessException{
        accountDeleteValidator.Validate(accountId);
        AccountDTO beforeAccountDTO = accountInquireService.getAccountById(accountId);
        String beforeData = auditLogService.toJson(beforeAccountDTO);
        try{
            int result = accountMapper.deleteAccount(accountId);
            if (result < 1) {
                throw new BusinessException("DB_DELETE_FAIL", "No record deleted, pls check backend");
            }
            accountRedisService.delete(accountId);
            auditLogService.logAccountOperation(
                    accountId,
                    "DELETE",
                    beforeData,
                    null, // 删除操作无后置数据
                    "system"
            );
            return(result);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Account existing");
        }
    }

}
