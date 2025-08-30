package com.simple.bank.service.biz;

import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.utlility.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CustomerRedisServiceImpl implements CustomerRedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(CustomerDTO customerDTO){
        String redisKey = formatKey(customerDTO.getCustomerId());
        String jsonString = JsonUtils.toJsonString(customerDTO);
        stringRedisTemplate.opsForValue().set(redisKey, jsonString, 300,TimeUnit.SECONDS);
    }

    public CustomerDTO get(Long customerId){
        String redisKey = formatKey(customerId);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), CustomerDTO.class);
    }

    public void delete(Long customerId){
        String redisKey = formatKey(customerId);
        stringRedisTemplate.delete(redisKey);
    }

    private static String formatKey(Long customerId) {
        return String.format("customer:%d", customerId);
    }
}
