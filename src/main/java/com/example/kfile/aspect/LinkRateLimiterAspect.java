package com.example.kfile.aspect;

import cn.hutool.cache.impl.TimedCache;
import com.example.kfile.annotation.LinkRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 校验直链访问频率.
 * <p>
 * 校验所有标注了 {@link LinkRateLimiter} 的注解
 */
@Aspect
@Component
@Slf4j
public class LinkRateLimiterAspect {

    Integer LinkLimitSecond = 100;
    Integer linkDownloadLimit = 100;
    private TimedCache<String, AtomicInteger> timedCache;

    /**
     * 校验直链访问频率.
     *
     * @param point 连接点
     * @return 方法运行结果
     */
    @Around(value = "@annotation(com.example.kfile.annotation.LinkRateLimiter)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Integer linkLimitSecond = 100;
        Integer linkDownloadLimit = 100;
        if (timedCache == null) {
            timedCache = new TimedCache<>(linkLimitSecond * 1000);
        }

        String clientIp = "";
        AtomicInteger ipDownloadCount = timedCache.get(clientIp, false, () -> new AtomicInteger(1));
        if (ipDownloadCount.get() > linkDownloadLimit) {
            throw new RuntimeException("当前系统限制每 " + LinkLimitSecond.toString() + " 秒内只能访问 " + linkDownloadLimit + " 次直链, 已超出请稍后访问.");
        }
        ipDownloadCount.incrementAndGet();
        log.info("ip {}, download count: {}", clientIp, ipDownloadCount.get());
        return point.proceed();
    }

}