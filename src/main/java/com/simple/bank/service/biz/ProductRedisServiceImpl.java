package com.simple.bank.service.biz;

import com.simple.bank.utlility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ProductRedisServiceImpl implements ProductRedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String PRODUCTS_STOCK_KEY_PREFIX = "product:stock:";
    private static final long EXPIRY_SECONDS = 86400; //86400s = 24 hours

    @Override
    public BigDecimal getProductStockById(Long productId) {
        String redisKey = formatStockKey(productId);
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        if (redisValue == null) {
            //in case not found in redis
            // JsonUtils.parseObject will return null and assign to long ->  NullPointerException
            return null;
        }
        Long amountInCent = JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), long.class);
        return (centToYuan(amountInCent));
    }

    @Override
    public void setProductStockById(Long productId, BigDecimal remainingAmount) {
        String redisKey = formatStockKey(productId);
        long value = yuanToCent(remainingAmount);
        String jsonString = JsonUtils.toJsonString(value);
        stringRedisTemplate.opsForValue().set(redisKey, jsonString, EXPIRY_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public Boolean deleteProductStockById(Long productId) {
        String redisKey = formatStockKey(productId);
        return stringRedisTemplate.delete(redisKey);
    }

    @Override
    public BigDecimal deductProductStockById(Long productId, BigDecimal amount) {
        String redisKey = formatStockKey(productId);
        long value = yuanToCent(amount);
        Long remainingInCent = stringRedisTemplate.opsForValue().decrement(redisKey, value);
        if (remainingInCent == null) {
            throw new RuntimeException("Redis stock decrement returned null, key: " + redisKey);
        }
        return (centToYuan(remainingInCent));
    }

    @Override
    public BigDecimal increaseProductStockById(Long productId, BigDecimal amount) {
        String redisKey = formatStockKey(productId);
        long value = yuanToCent(amount);
        Long remainingInCent = stringRedisTemplate.opsForValue().increment(redisKey, value);
        if (remainingInCent == null) {
            throw new RuntimeException("Redis stock increment returned null, key: " + redisKey);
        }
        return(centToYuan(remainingInCent));
    }

    @Override
    public Map<Long, BigDecimal> getAllProductStock() {
        String pattern = PRODUCTS_STOCK_KEY_PREFIX + "*";
        Map<Long, BigDecimal> result = new HashMap<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();

        Cursor<String> cursor = stringRedisTemplate.scan(options);
        while (cursor.hasNext()) {
            String redisKey = cursor.next();
            // get productId from redisKey (product:stock:123)
            String productIdStr = redisKey.replace(PRODUCTS_STOCK_KEY_PREFIX, "");
            try {
                Long productId = Long.parseLong(productIdStr);
                BigDecimal remainingAmount = this.getProductStockById(productId);
                result.put(productId, remainingAmount);
            } catch (NumberFormatException e) {
                log.warn("[Redis Product Stock] Invalid product stock key format: {}", redisKey);
            }
        }
        return result;
    }
    private static String formatKey(Long productId) {
        return String.format("%s%d", PRODUCT_KEY_PREFIX, productId);
    }

    private static String formatStockKey(Long productId) {
        return String.format("%s%d", PRODUCTS_STOCK_KEY_PREFIX, productId);
    }

    //TODO: if currency has 3 decimal - need special handling here
    private static long yuanToCent(BigDecimal amountInYuan) {
        long amountInCent = amountInYuan.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
        return amountInCent;
    }

    private static BigDecimal centToYuan(long amountInCent) {
        BigDecimal amountInYuan = new BigDecimal(amountInCent).
                divide(new BigDecimal("100"),2, RoundingMode.HALF_UP);
        return amountInYuan;
    }

}