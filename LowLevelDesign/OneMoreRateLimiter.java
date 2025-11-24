package LowLevelDesign.RateLimiter_Atharv.java;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

//enums
enum UserTier{
    FREE, PREMIUM
}
enum RateLimitType{
    FIXED_WINDOW, TOKEN_BUCKET, SLIDING_WINDOW
}
@AllArgsConstructor
class User{
    String id;
    UserTier tier;
}
@Getter
@AllArgsConstructor
class RateLimitConfig {
    private final int maxRequests;
    private final int windowInSeconds;
}

@AllArgsConstructor
abstract class RateLimiter {
    protected final RateLimitConfig config;
    protected final RateLimitType type;

    public abstract boolean allowRequest(String userId);
}
class RateLimiterFactory {
    public static RateLimiter createRateLimiter(RateLimitType algo, RateLimitConfig config) {
        return switch (algo) {
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(config);
            case FIXED_WINDOW -> new FixedWindowRateLimiter(config);
            case SLIDING_WINDOW -> new SlidingWindowRateLimiter(config);
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algo);
        };
    }
}

class TokenBucketRateLimiter extends RateLimiter {
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRefillTime = new HashMap<>();

    public TokenBucketRateLimiter(RateLimitConfig config) {
        super(config, RateLimitType.TOKEN_BUCKET);
    }

    @Override
    public boolean allowRequest(String userId) {
        AtomicBoolean allowed = new AtomicBoolean(false);
        long now = System.currentTimeMillis();
        tokens.compute(userId, (uId, availableTokens) -> {
           int currentTokens = refillTokens(userId, now);
           if(currentTokens > 0){
               allowed.set(true);
               return currentTokens - 1;
           }
           else {
               allowed.set(false);
               return currentTokens;
           }
        });
        return allowed.get();
    }

    private int refillTokens(String userId, long now) {
        double refillRate = (double)config.getWindowInSeconds() / config.getMaxRequests();

        long lastRefill = lastRefillTime.getOrDefault(userId, now);
        long elapsedTime = (now - lastRefill) / 1000;
        int refillToken = (int) (elapsedTime / refillRate);
        int currenTokens = tokens.getOrDefault(userId, config.getMaxRequests());

        currenTokens = Math.min(currenTokens + refillToken, config.getMaxRequests());

        if(refillToken > 0)
            lastRefillTime.put(userId, now);

        return currenTokens;
    }
}

class SlidingWindowRateLimiter extends RateLimiter{
    private final Map<String, Queue<Long>> requestLog = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(RateLimitConfig config) {
        super(config, RateLimitType.SLIDING_WINDOW);
    }

    @Override
    public boolean allowRequest(String userId) {
        AtomicBoolean allowed = new AtomicBoolean(false);
        long now = System.currentTimeMillis();

        requestLog.compute(userId, (uId, log) -> {
           if(log == null)
               log = new ArrayDeque<>();
           while(!log.isEmpty() && (now - log.peek()) > config.getWindowInSeconds()){
               log.poll();
           }
           if(log.size() < config.getMaxRequests()){
               log.add(now);
               allowed.set(true);
           }
           return log;
        });
        return allowed.get();
    }
}

class FixedWindowRateLimiter extends RateLimiter {
    private final Map<String, Integer> requestCount = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStart = new HashMap<>();

    public FixedWindowRateLimiter(RateLimitConfig config) {
        super(config, RateLimitType.FIXED_WINDOW);
    }

    @Override
    public boolean allowRequest(String userId) {
        AtomicBoolean allowed = new AtomicBoolean(false);

        long currentReqWindow = System.currentTimeMillis() / 1000 / config.getWindowInSeconds();

        requestCount.compute(userId, (id, count) -> {
            long lastReqWindow = windowStart.getOrDefault(id, currentReqWindow);

            if (lastReqWindow != currentReqWindow) {
                windowStart.put(id, currentReqWindow);
                allowed.set(true);
                return 1;
            }

            if (count == null) count = 0;

            if (count < config.getMaxRequests()) {
                allowed.set(true);
                return count + 1;
            }

            return count;
        });

        return allowed.get();
    }
}

class RateLimiterService{
    private final Map<UserTier, RateLimiter> rateLimiters = new HashMap<>();

    public RateLimiterService(){
        rateLimiters.put(UserTier.FREE, RateLimiterFactory.createRateLimiter(RateLimitType.TOKEN_BUCKET, new RateLimitConfig(10, 60)));
        rateLimiters.put(UserTier.PREMIUM, RateLimiterFactory.createRateLimiter(RateLimitType.FIXED_WINDOW, new RateLimitConfig(100, 60)));
    }

    public boolean allowRequest(User user){
        RateLimiter rateLimiter = rateLimiters.get(user.tier);
        if (rateLimiter == null){
            throw new IllegalArgumentException("No limiter configured for tier " + user.tier);
        }
        return rateLimiter.allowRequest(user.id);
    }
}
public class Solution {
    public static void main(String[] args) throws InterruptedException {
        RateLimiterService rateLimiterService = new RateLimiterService();

        User freeUser = new User("user1", UserTier.FREE); // 10 req in 60 sec
        User premiumUser = new User("user2", UserTier.PREMIUM); // 100 req in 60 sec

        System.out.println("=== Free User Requests ===");
        for (int i = 1; i <= 15; i++) {
            boolean allowed = rateLimiterService.allowRequest(freeUser);
            System.out.println("Request " + i + " for Free User: " + (allowed ? "ALLOWED" : "BLOCKED"));
            Thread.sleep(100); // simulate delay between requests
        }

        System.out.println("\n=== Premium User Requests ===");
        for (int i = 1; i <= 120; i++) {
            boolean allowed = rateLimiterService.allowRequest(premiumUser);
            System.out.println("Request " + i + " for Premium User: " + (allowed ? "ALLOWED" : "BLOCKED"));
            Thread.sleep(100);
        }
    }
}
