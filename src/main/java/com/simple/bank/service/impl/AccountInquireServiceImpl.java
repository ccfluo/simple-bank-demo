package com.simple.bank.service.impl;

import com.simple.bank.converter.AccountConverter;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.entity.AccountEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.AccountMapper;
import com.simple.bank.service.AccountInquireService;
import com.simple.bank.service.AccountRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountInquireServiceImpl implements AccountInquireService {
    @Autowired
    private AccountMapper accountMapper;  // auto inject the mapper

//  @Autowired 是 Spring 的依赖注入注解，
//  用于自动获取 Spring 容器中已管理的 Bean，并注入到当前类的属性中。
//  告诉 Spring“我需要这个类型的实例，请从容器里找一个给我
    @Autowired
    private AccountConverter accountConverter;
    @Autowired
    private AccountRedisService accountRedisService;

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long accountId) throws BusinessException {
        AccountDTO accountDTO;
        accountDTO = accountRedisService.get(accountId);
        if((accountDTO != null) && (accountDTO.getAccountId().equals(accountId))) {
            return accountDTO;
        }
        AccountEntity accountEntity = accountMapper.selectAccountById(accountId);  // call Mapper to inquire
//        return Optional.ofNullable(customerEntity);
        if (accountEntity == null) {
            throw new BusinessException("NOT_FOUND", "Account with id " + accountId + " not found");
        } else {
            accountDTO = accountConverter.accountToDto(accountEntity);
            accountRedisService.set(accountDTO);
            return accountDTO;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() throws BusinessException {
        List<AccountEntity> accountEntityList = accountMapper.selectAllAccounts(); // call Mapper to inquire
        if (accountEntityList.isEmpty()) {
            throw new BusinessException("NOT_FOUND", "Account not found");
        } else {
            List<AccountDTO> accountDTOList = accountEntityList.stream()
                 .map(accountEntity -> accountConverter.accountToDto(accountEntity))
                 .collect(Collectors.toList());
            return accountDTOList;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountByCustomerId(Long customerId) throws BusinessException {
        List<AccountEntity> accountEntityList = accountMapper.selectAccountByCustomerId(customerId); // call Mapper to inquire
        if (accountEntityList.isEmpty()) {
            throw new BusinessException("NOT_FOUND", "Account not found");
        } else {
            List<AccountDTO> accountDTOList = accountEntityList.stream()
                    .map(accountEntity -> accountConverter.accountToDto(accountEntity))
                    .collect(Collectors.toList());
            return accountDTOList;
        }
    }

}
