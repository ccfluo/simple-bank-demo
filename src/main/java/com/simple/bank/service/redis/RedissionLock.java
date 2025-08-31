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
        // Use Reentrant Lock
        // other redission lock mode: fair lock - FIFO; ReadWrite lock
        RLock lock = redissonClient.getLock(lockKey); //get a lock object instance by name
        boolean locked = false;
        try {
            if (leaseTimeMillis > 0) {
                locked = lock.tryLock(leaseTimeMillis, TimeUnit.MILLISECONDS);
                if (!locked) {
                    throw new BusinessException("SYSTEM_BUSY", "System busy, try again later");
                }
            } else {
                lock.lock();  //enable watchdog mode，阻塞式等待
            }
            return action.get();
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Interrupted while acquiring lock" + e);
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public <T> T lockTwoAccounts(Long accountId1, Long accountId2, Long leaseTimeMillis, Supplier<T> supplier) throws BusinessException {
        // 确定加锁顺序：先小后大
        long lockFirst = Math.min(accountId1, accountId2);
        long lockSecond = Math.max(accountId1, accountId2);

        RLock lock1 = redissonClient.getLock("account:lock:" + lockFirst);
        RLock lock2 = redissonClient.getLock("account:lock:" + lockSecond);

        try {
            // try to lock 2
            boolean locked1 = lock1.tryLock(leaseTimeMillis, 500, TimeUnit.MILLISECONDS);
            if (!locked1) {
                throw new BusinessException("SYSTEM_BUSY", "System busy, try again later");
            }

            boolean locked2 = lock2.tryLock(leaseTimeMillis, 500, TimeUnit.MILLISECONDS);
            if (!locked2) {
                lock1.unlock(); // release locked 1st lock
                throw new BusinessException("SYSTEM_BUSY", "System busy, try again later");
            }
            // execute biz logic
            return supplier.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("OPERATION_INTERRUPTED", "Operation interrupted");
        } finally {
            // release lock in reverse sequence
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
