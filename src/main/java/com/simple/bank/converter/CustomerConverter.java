package com.simple.bank.converter;

import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.entity.CustomerEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


//@Component 后，Spring 会自动创建它的单例实例，
// 无需手动通过 new CustomerConverter() 创建对象，
// 实现了 “对象创建权由 Spring 接管”
// 告诉 Spring“这个类需要被管理，请创建它的实例并放到容器里”
@Component
public class CustomerConverter {
    public CustomerDTO customerToDto(CustomerEntity customerEntity){
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customerEntity,customerDTO);
        return customerDTO;
    }

    public CustomerEntity customerDtoToEntity(CustomerDTO customerDTO){
        CustomerEntity customerEntity = new CustomerEntity();
        BeanUtils.copyProperties(customerDTO, customerEntity);
        return customerEntity;
    }

}
