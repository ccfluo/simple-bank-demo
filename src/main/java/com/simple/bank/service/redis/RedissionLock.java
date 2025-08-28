package com.simple.bank.service.redis;

import com.simple.bank.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
public class RedissionLock {

    @Autowired
    private RedissonClient redissonClient;

    public<T> T lock(String lockKey, Long leaseTimeMillis, Supplier<T> action) {
//        String lockKey = formatKey(id);
        RLock lock = redissonClient.getLock(lockKey); //get a lock object instance by name
        boolean locked = false;
        try {
            if (leaseTimeMillis > 0) {
                locked = lock.tryLock(leaseTimeMillis, TimeUnit.SECONDS);
                if (!locked) {
                    throw new BusinessException("DUP_TXN_LOCK","Transaction is processing");
                }
            } else {
                lock.lock();  //启用看门狗模式，阻塞式等待
            }
            return action.get();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Interrupted while acquiring lock" + e);
        }finally {
            lock.unlock();
        }
    }

    public <T> T lockTwoAccounts(Long accountId1, Long accountId2, Long leaseTimeMillis, Supplier<T> supplier) throws BusinessException {
        // 确定加锁顺序：先小后大
        Long lockFirst = Math.min(accountId1, accountId2);
        Long lockSecond = Math.max(accountId1, accountId2);

        RLock lock1 = redissonClient.getLock("account:lock:" + lockFirst);
        RLock lock2 = redissonClient.getLock("account:lock:" + lockSecond);

        try {
            // 尝试获取两把锁（最多等待waitTime毫秒）
            boolean locked1 = lock1.tryLock(leaseTimeMillis, 10, TimeUnit.SECONDS);
            if (!locked1) {
                throw new BusinessException("SYSTEM_BUSY", "System busy, try again later");
            }

            boolean locked2 = lock2.tryLock(leaseTimeMillis, 10, TimeUnit.SECONDS);
            if (!locked2) {
                lock1.unlock(); // 释放已获取的第一把锁
                throw new BusinessException("SYSTEM_BUSY", "System busy, try again later");
            }
            // execute biz logic
            return supplier.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("OPERATION_INTERRUPTED", "Operation interrupted");
        } finally {
            // 按相反顺序释放锁
            if (lock2.isHeldByCurrentThread()) {
                lock2.unlock();
            }
            if (lock1.isHeldByCurrentThread()) {
                lock1.unlock();
            }
        }
    }
//    private static String formatKey(String id) {
//        return String.format("transaction:lock%s", id);
//    }

}
