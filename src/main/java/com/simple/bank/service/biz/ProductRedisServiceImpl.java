package com.simple.bank.service.biz;

import com.simple.bank.dto.ProductDTO;
import com.simple.bank.utlility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductRedisServiceImpl implements ProductRedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String ON_SALE_PRODUCTS_KEY = "product:onSale";
    private static final String PRODUCTS_STOCK_KEY_PREFIX = "product:stock:";

    private static final long EXPIRY_SECONDS = 300;

    @Override
    public void set(ProductDTO productDTO) {
        String redisKey = formatKey(productDTO.getProductId());
        String jsonString = JsonUtils.toJsonString(productDTO);
        stringRedisTemplate.opsForValue().set(redisKey, jsonString, EXPIRY_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public ProductDTO getById(Long productId) {
        String redisKey = formatKey(productId);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), ProductDTO.class);
    }

    @Override
    public void delete(Long productId) {
        String redisKey = formatKey(productId);
        stringRedisTemplate.delete(redisKey);
    }

    @Override
    public void setOnSaleProducts(List<ProductDTO> products) {
        String jsonString = JsonUtils.toJsonString(products);
        stringRedisTemplate.opsForValue().set(ON_SALE_PRODUCTS_KEY, jsonString, EXPIRY_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public BigDecimal getRemainingAmountById(Long productId) {
        String redisKey = formatStockKey(productId);
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        if (redisValue == null) {
            //in case not found in redis
            // JsonUtils.parseObject will return null and assign to long ->  NullPointerException
            return BigDecimal.ZERO;
        }
        long amountInCent = JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), long.class);
        return (centToYuan(amountInCent));
    }

    @Override
    public void setRemainingAmount(Long productId, BigDecimal remainingAmount, long expirySeconds) {
        String redisKey = formatStockKey(productId);
        long value = yuanToCent(remainingAmount);
        String jsonString = JsonUtils.toJsonString(value);
        stringRedisTemplate.opsForValue().set(redisKey, jsonString, expirySeconds, TimeUnit.SECONDS);
    }

    @Override
    public Boolean deleteRemainingAmount(Long productId) {
        String redisKey = formatStockKey(productId);
        return stringRedisTemplate.delete(redisKey);
    }

    @Override
    public BigDecimal deductRemainingAmountById(Long productId, BigDecimal amount) {
        String redisKey = formatStockKey(productId);
        long value = yuanToCent(amount);
        Long remainingInCent = stringRedisTemplate.opsForValue().decrement(redisKey, value);
        return (centToYuan(remainingInCent));
    }

    @Override
    public BigDecimal increaseRemainingAmountById(Long productId, BigDecimal amount) {
        String redisKey = formatStockKey(productId);
        long value = yuanToCent(amount);
        Long remainingInCent = stringRedisTemplate.opsForValue().increment(redisKey, value);
        return(centToYuan(remainingInCent));
    }

    @Override
    public Map<Long, BigDecimal> getAllProductsRemainingAmount() {
        // 匹配所有产品库存的key
        String pattern = PRODUCTS_STOCK_KEY_PREFIX + "*";
        Set<String> keys = stringRedisTemplate.keys(pattern);

        Map<Long, BigDecimal> result = new HashMap<>();
        if (keys == null || keys.isEmpty()) {
            return result;
        }

        for (String key : keys) {
            // 从key中提取产品ID (格式: product:stock:123)
            String productIdStr = key.replace(PRODUCTS_STOCK_KEY_PREFIX, "");
            try {
                Long productId = Long.parseLong(productIdStr);
                BigDecimal remainingAmount = getRemainingAmountById(productId);
                result.put(productId, remainingAmount);
            } catch (NumberFormatException e) {
                log.warn("Invalid product stock key format: {}", key);
            }
        }
        return result;
    }

    @Override
//    @SuppressWarnings("unchecked")
    public List<ProductDTO> getOnSaleProducts() {
        String jsonString = stringRedisTemplate.opsForValue().get(ON_SALE_PRODUCTS_KEY);
        if (jsonString == null) {
            return null;
        }

        List<Object> objects = JsonUtils.parseArray(jsonString, Object.class);
        return objects.stream()
                .map(obj -> JsonUtils.parseObject(JsonUtils.toJsonString(obj), ProductDTO.class))
                .collect(Collectors.toList());
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