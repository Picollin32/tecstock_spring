package tecstock_spring.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private static class LoginAttempt {
        int attempts;
        LocalDateTime lastAttempt;
        LocalDateTime blockedUntil;

        LoginAttempt() {
            this.attempts = 0;
            this.lastAttempt = LocalDateTime.now();
            this.blockedUntil = null;
        }
    }

    private final Map<String, LoginAttempt> fallbackAttempts = new ConcurrentHashMap<>();
    private final StringRedisTemplate redisTemplate;

    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;
    private static final int RESET_MINUTES = 30;
    private static final String ATTEMPTS_KEY_PREFIX = "rate:login:attempts:";
    private static final String BLOCKED_KEY_PREFIX = "rate:login:blocked:";

    public RateLimitService(ObjectProvider<StringRedisTemplate> redisTemplateProvider) {
        this.redisTemplate = redisTemplateProvider.getIfAvailable();
    }

    private boolean useRedis() {
        return redisTemplate != null;
    }

    private @NonNull String compositeKey(String username, String clientIp) {
        String safeUser = username != null ? username.toLowerCase() : "unknown";
        String safeIp = clientIp != null && !clientIp.isBlank() ? clientIp : "unknown";
        return safeUser + "|" + safeIp;
    }

    private @NonNull String attemptsKey(@NonNull String key) {
        return ATTEMPTS_KEY_PREFIX + key;
    }

    private @NonNull String blockedKey(@NonNull String key) {
        return BLOCKED_KEY_PREFIX + key;
    }

    public boolean isBlocked(String username, String clientIp) {
        String key = compositeKey(username, clientIp);

        if (useRedis()) {
            try {
                Long ttlMinutes = redisTemplate.getExpire(blockedKey(key), TimeUnit.MINUTES);
                return ttlMinutes != null && ttlMinutes > 0;
            } catch (Exception ex) {

            }
        }

        LoginAttempt attempt = fallbackAttempts.get(key);
        if (attempt == null) {
            return false;
        }

        if (attempt.blockedUntil != null && LocalDateTime.now().isBefore(attempt.blockedUntil)) {
            return true;
        }

        if (attempt.blockedUntil != null && LocalDateTime.now().isAfter(attempt.blockedUntil)) {
            fallbackAttempts.remove(key);
            return false;
        }

        return false;
    }

    public void recordFailedAttempt(String username, String clientIp) {
        String key = compositeKey(username, clientIp);

        if (useRedis()) {
            try {
                Long attempts = redisTemplate.opsForValue().increment(attemptsKey(key));
                Duration resetDuration = Duration.ofMinutes(RESET_MINUTES);
                if (attempts != null && attempts == 1L) {
                    redisTemplate.expire(attemptsKey(key), Objects.requireNonNull(resetDuration));
                } else {
                    Long ttl = redisTemplate.getExpire(attemptsKey(key), TimeUnit.MINUTES);
                    if (ttl == null || ttl < 0) {
                        redisTemplate.expire(attemptsKey(key), Objects.requireNonNull(resetDuration));
                    }
                }

                if (attempts != null && attempts >= MAX_ATTEMPTS) {
                    Duration blockDuration = Duration.ofMinutes(BLOCK_DURATION_MINUTES);
                    redisTemplate.opsForValue().set(blockedKey(key), "1", Objects.requireNonNull(blockDuration));
                }
                return;
            } catch (Exception ex) {

            }
        }

        LoginAttempt attempt = fallbackAttempts.computeIfAbsent(key, k -> new LoginAttempt());
        if (LocalDateTime.now().isAfter(attempt.lastAttempt.plusMinutes(RESET_MINUTES))) {
            attempt.attempts = 1;
        } else {
            attempt.attempts++;
        }

        attempt.lastAttempt = LocalDateTime.now();

        if (attempt.attempts >= MAX_ATTEMPTS) {
            attempt.blockedUntil = LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES);
        }
    }

    public void resetAttempts(String username, String clientIp) {
        String key = compositeKey(username, clientIp);

        if (useRedis()) {
            try {
                redisTemplate.delete(attemptsKey(key));
                redisTemplate.delete(blockedKey(key));
                return;
            } catch (Exception ex) {

            }
        }

        fallbackAttempts.remove(key);
    }

    public long getBlockedMinutesRemaining(String username, String clientIp) {
        String key = compositeKey(username, clientIp);

        if (useRedis()) {
            try {
                Long ttl = redisTemplate.getExpire(blockedKey(key), TimeUnit.MINUTES);
                return ttl != null && ttl > 0 ? ttl : 0;
            } catch (Exception ex) {
            }
        }

        LoginAttempt attempt = fallbackAttempts.get(key);
        if (attempt == null || attempt.blockedUntil == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(attempt.blockedUntil)) {
            return Duration.between(now, attempt.blockedUntil).toMinutes();
        }

        return 0;
    }

    public int getRemainingAttempts(String username, String clientIp) {
        String key = compositeKey(username, clientIp);

        if (useRedis()) {
            try {
                String value = redisTemplate.opsForValue().get(attemptsKey(key));
                long attempts = value != null ? Long.parseLong(value) : 0L;
                return (int) Math.max(0, MAX_ATTEMPTS - attempts);
            } catch (Exception ex) {

            }
        }

        LoginAttempt attempt = fallbackAttempts.get(key);
        if (attempt == null) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempt.attempts);
    }
}
