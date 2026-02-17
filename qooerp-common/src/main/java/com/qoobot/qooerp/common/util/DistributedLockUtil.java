package com.qoobot.qooerp.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类（基于Redisson）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockUtil {

    private final RedissonClient redissonClient;

    /**
     * 获取锁
     *
     * @param lockKey 锁的key
     * @return 锁对象
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey    锁的key
     * @param waitTime   等待时间
     * @param leaseTime  持有锁时间
     * @param unit       时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: lockKey={}", lockKey, e);
            return false;
        }
    }

    /**
     * 尝试获取锁（默认时间单位：秒）
     *
     * @param lockKey    锁的key
     * @param waitTime   等待时间（秒）
     * @param leaseTime  持有锁时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        return tryLock(lockKey, waitTime, leaseTime, TimeUnit.SECONDS);
    }

    /**
     * 获取锁（阻塞直到获取成功）
     *
     * @param lockKey   锁的key
     * @param leaseTime 持有锁时间
     * @param unit      时间单位
     */
    public void lock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        lock.lock(leaseTime, unit);
    }

    /**
     * 获取锁（默认时间单位：秒）
     *
     * @param lockKey   锁的key
     * @param leaseTime 持有锁时间（秒）
     */
    public void lock(String lockKey, long leaseTime) {
        lock(lockKey, leaseTime, TimeUnit.SECONDS);
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的key
     */
    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 释放锁（使用锁对象）
     *
     * @param lock 锁对象
     */
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 判断锁是否被锁定
     *
     * @param lockKey 锁的key
     * @return 是否被锁定
     */
    public boolean isLocked(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.isLocked();
    }

    /**
     * 判断锁是否被当前线程持有
     *
     * @param lockKey 锁的key
     * @return 是否被当前线程持有
     */
    public boolean isHeldByCurrentThread(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.isHeldByCurrentThread();
    }

    /**
     * 执行带锁的方法
     *
     * @param lockKey   锁的key
     * @param waitTime  等待时间（秒）
     * @param leaseTime 持有锁时间（秒）
     * @param runnable 执行的方法
     * @return 是否执行成功
     */
    public boolean executeWithLock(String lockKey, long waitTime, long leaseTime, Runnable runnable) {
        if (!tryLock(lockKey, waitTime, leaseTime)) {
            return false;
        }

        try {
            runnable.run();
            return true;
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 执行带锁的方法（有返回值）
     *
     * @param lockKey   锁的key
     * @param waitTime  等待时间（秒）
     * @param leaseTime 持有锁时间（秒）
     * @param supplier  执行的方法
     * @param <T>       返回值类型
     * @return 返回值
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, java.util.function.Supplier<T> supplier) {
        if (!tryLock(lockKey, waitTime, leaseTime)) {
            throw new RuntimeException("获取锁失败: " + lockKey);
        }

        try {
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 强制释放锁
     *
     * @param lockKey 锁的key
     */
    public void forceUnlock(String lockKey) {
        RLock lock = getLock(lockKey);
        lock.forceUnlock();
    }
}
