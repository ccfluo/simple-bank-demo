package com.simple.bank.service.biz;

import com.simple.bank.dto.ProductDTO;
import com.simple.bank.utlility.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ProductRedisServiceImpl implements ProductRedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String PRODUCT_KEY_PREFIX = "WEALTH_PRODUCT#";
    private static final String ON_SALE_PRODUCTS_KEY = "WEALTH_PRODUCT#ON_SALE";

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
}