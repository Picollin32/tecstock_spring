package tecstock_spring.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.Map;

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

    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;
    private static final int RESET_MINUTES = 30;

    public boolean isBlocked(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt == null) {
            return false;
        }

        if (attempt.blockedUntil != null && LocalDateTime.now().isBefore(attempt.blockedUntil)) {
            return true;
        }

        if (attempt.blockedUntil != null && LocalDateTime.now().isAfter(attempt.blockedUntil)) {
            loginAttempts.remove(username);
            return false;
        }

        return false;
    }

    public void recordFailedAttempt(String username) {
        LoginAttempt attempt = loginAttempts.computeIfAbsent(username, k -> new LoginAttempt());
        
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

    public void resetAttempts(String username) {
        loginAttempts.remove(username);
    }

    public long getBlockedMinutesRemaining(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt == null || attempt.blockedUntil == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(attempt.blockedUntil)) {
            return java.time.Duration.between(now, attempt.blockedUntil).toMinutes();
        }

        return 0;
    }

    public int getRemainingAttempts(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt == null) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempt.attempts);
    }
}
