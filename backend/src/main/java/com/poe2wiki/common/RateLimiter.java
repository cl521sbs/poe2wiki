package com.poe2wiki.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimiter {

    private final StringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>(
        "local count = redis.call('INCR', KEYS[1])\n" +
        "if count == 1 then\n" +
        "    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[2]))\n" +
        "end\n" +
        "if count > tonumber(ARGV[1]) then\n" +
        "    return 0\n" +
        "end\n" +
        "return 1", Long.class
    );

    /**
     * @param key 限流 key（如 rate:login:username）
     * @param maxAttempts 最大尝试次数
     * @param windowSeconds 时间窗口（秒）
     * @return true=允许, false=触发限流
     */
    public boolean tryAcquire(String key, int maxAttempts, int windowSeconds) {
        Long result = redisTemplate.execute(
            RATE_LIMIT_SCRIPT,
            List.of(key),
            String.valueOf(maxAttempts),
            String.valueOf(windowSeconds)
        );
        return result != null && result == 1L;
    }

    public long getRemaining(String key, int maxAttempts) {
        String count = redisTemplate.opsForValue().get(key);
        if (count == null) return maxAttempts;
        return Math.max(0, maxAttempts - Long.parseLong(count));
    }
}
