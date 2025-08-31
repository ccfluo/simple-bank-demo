package com.simple.bank.service.redis;

import com.simple.bank.dto.CustomerDTO;

public interface CustomerRedisService {
    void delete(Long customerId);
    CustomerDTO get(Long customerId);
    void set(CustomerDTO customerDTO);
}
