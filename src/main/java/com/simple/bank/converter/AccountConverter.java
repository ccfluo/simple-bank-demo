package com.simple.bank.converter;

import com.simple.bank.dto.AccountDTO;
import com.simple.bank.entity.AccountEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


//@Component 后，Spring 会自动创建它的单例实例，
// 无需手动通过 new CustomerConverter() 创建对象，
// 实现了 “对象创建权由 Spring 接管”
// 告诉 Spring“这个类需要被管理，请创建它的实例并放到容器里”
@Component
public class AccountConverter {
    public AccountDTO accountToDto(AccountEntity accountEntity){
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(accountEntity,accountDTO);
        return accountDTO;
    }

    public AccountEntity accountDtoToEntity(AccountDTO accountDTO){
        AccountEntity accountEntity = new AccountEntity();
        BeanUtils.copyProperties(accountDTO, accountEntity);
        return accountEntity;
    }

}
