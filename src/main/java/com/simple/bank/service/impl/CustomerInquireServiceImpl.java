package com.simple.bank.service.impl;

import com.simple.bank.converter.CustomerConverter;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.entity.CustomerEntity;
import com.simple.bank.exception.CustomerNotFound;
import com.simple.bank.mapper.CustomerMapper;
import com.simple.bank.service.CustomerInquireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerInquireServiceImpl implements CustomerInquireService {
    @Autowired
    private CustomerMapper customerMapper;  // auto inject the mapper

//  @Autowired 是 Spring 的依赖注入注解，
//  用于自动获取 Spring 容器中已管理的 Bean，并注入到当前类的属性中。
//  告诉 Spring“我需要这个类型的实例，请从容器里找一个给我
    @Autowired
    private CustomerConverter customerConverter;
    @Autowired
    private CustomerRedisServiceImpl customerRedisService;

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long customerId) throws CustomerNotFound {
        CustomerDTO customerDTO;
        // get customerDTO from redis
        customerDTO = customerRedisService.get(customerId);
        if((customerDTO != null) && (customerDTO.getCustomerId().equals(customerId))) {
            return customerDTO;
        }
        // if not in redis, then get it from table
        CustomerEntity customerEntity = customerMapper.selectCustomerById(customerId);  // call Mapper to inquire
        if (customerEntity == null) {
            throw new CustomerNotFound("Customer with id " + customerId + " not found");
        } else {
            customerDTO = customerConverter.customerToDto(customerEntity);
            customerRedisService.set(customerDTO);
            return customerDTO;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() throws CustomerNotFound {
        List<CustomerEntity> customerEntityList = customerMapper.selectAllCustomers(); // call Mapper to inquire
        if (customerEntityList.isEmpty()) {
            throw new CustomerNotFound("No customer found");
        } else {
            List<CustomerDTO> customerDTOList = customerEntityList.stream()
                 .map(customerEntity -> customerConverter.customerToDto(customerEntity))
                 .collect(Collectors.toList());
            return customerDTOList;
        }
    }

}
