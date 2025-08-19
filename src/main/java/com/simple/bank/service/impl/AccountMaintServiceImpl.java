package com.simple.bank.service.impl;

import com.simple.bank.api.request.AccountAddRequest;
import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.converter.AccountConverter;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.entity.AccountEntity;
import com.simple.bank.exception.AccountNotFound;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.AccountMapper;
import com.simple.bank.service.AccountInquireService;
import com.simple.bank.service.AccountMaintService;
import com.simple.bank.service.AccountRedisService;
import com.simple.bank.service.AuditLogService;
import com.simple.bank.utlility.ExceptionFormatter;
import com.simple.bank.validator.AccountAddValidator;
import com.simple.bank.validator.AccountDeleteValidator;
import com.simple.bank.validator.AccountUpdateValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
            if (result <= 1) {
                throw new BusinessException("DB_INSERT_FAIL", "No record inserted, pls check backend");
            }
//            accountRedisService.delete(accountEntity.getCustomerId());
            AccountDTO createdAccountDTO = accountInquireService.getAccountById(accountEntity.getAccountId());
            auditLogService.logAccountOperation(
                    accountEntity.getAccountId(),
                    "CREATE",
                    null, 
                    auditLogService.toJson(createdAccountDTO),
                    accountAddRequest.getOperContext().getUserId()
            );
            return accountEntity.getAccountId();
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUP_KEY", "Customer existing");
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("DATA_INTEGRITY", ExceptionFormatter.format(e));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("UNKNOWN_ERROR", "Unknown Error: "+e);
        }

    }

    @Override
    @Transactional
    public AccountDTO updateAccount(AccountUpdateRequest accountUpdateRequest) throws AccountNotFound, BusinessException {

        accountUpdateValidator.Validate(accountUpdateRequest);
        AccountDTO beforeAccountDTO = accountInquireService.getAccountById(accountUpdateRequest.getAccount().getAccountId());
        String beforeData = auditLogService.toJson(beforeAccountDTO);

        AccountEntity accountEntity = accountConverter.accountDtoToEntity(accountUpdateRequest.getAccount());
        try{
            int result = accountMapper.dynamicUpdateAccount(accountEntity);
            if (result < 1) {
                throw new BusinessException("DB_UPDATE_FAIL", "No record updated, pls check backend");
            }
            accountRedisService.delete(accountEntity.getCustomerId());
            // get customer details after update
            AccountDTO updatedAccountDTO = accountInquireService.getAccountById(accountUpdateRequest.getAccount().getAccountId());
            log.info("updatedAccountDTO: " + updatedAccountDTO.toString());
            auditLogService.logAccountOperation(
                    accountUpdateRequest.getAccount().getAccountId(),
                    "UPDATE",
                    beforeData,
                    auditLogService.toJson(updatedAccountDTO),
                    accountUpdateRequest.getOperContext().getUserId()
            );
            // redis delayed double deletion
            accountRedisService.delete(accountEntity.getCustomerId());
            return updatedAccountDTO;
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUP_KEY", "Customer existing");
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("DATA_INTEGRITY", e.toString());
        }catch (BusinessException e) {
            throw e;
        }catch (Exception e) {
            throw new BusinessException("UNKNOWN_ERROR", e.toString());
        }

    }

    @Override
    public int deleteAccount(Long accountId) throws AccountNotFound, BusinessException{
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
            throw new BusinessException("DUP_KEY", "Account existing");
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("DATA_INTEGRITY", e.toString());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("UNKNOWN_ERROR", e.toString());
        }
    }

}
