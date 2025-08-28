package com.simple.bank.service.biz;

import com.simple.bank.dto.AccountDTO;
import com.simple.bank.utlility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AccountRedisServiceImpl implements AccountRedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(AccountDTO accountDTO){
        String redisKey = formatKey(accountDTO.getAccountId());
        String jsonString = JsonUtils.toJsonString(accountDTO);
        stringRedisTemplate.opsForValue().set(redisKey, jsonString, 300,TimeUnit.SECONDS);
    }

    public AccountDTO get(Long accountId){
        String redisKey = formatKey(accountId);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), AccountDTO.class);
    }

    public void delete(Long accountId){
        String redisKey = formatKey(accountId);
        stringRedisTemplate.delete(redisKey);
    }

    private static String formatKey(Long accountId) {
        return String.format("ACCOUNT#%d", accountId);
    }
}
