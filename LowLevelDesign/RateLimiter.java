package LowLevelDesign.RateLimiter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

// Rate Limiter
enum RuleType {
    USER_ID, IP, ENDPOINT
}

class Rule {
    RuleType ruleType;
    int maxReqs;
    int windowSecs;

    public Rule(RuleType ruleType, int maxReqs, int windowSecs) {
        this.ruleType = ruleType;
        this.maxReqs = maxReqs;
        this.windowSecs = windowSecs;
    }
}

class Store {
    private static volatile Store instance;

    Map<String, Integer> fixedWindowCounter = new HashMap<>();
    Map<String, List<Request>> slidingWindowRequests = new HashMap<>();
    Map<String, TokenBucket> tokenBuckets = new HashMap<>();

    private Store() {}

    public synchronized static Store getInstance() {
        if (instance == null) instance = new Store();
        return instance;
    }
}

class Request {
    String id;
    String userId;
    String ip;
    String path;
    LocalDateTime timeStamp;

    public Request(String userId, String ip, String path) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.ip = ip;
        this.path = path;
        this.timeStamp = LocalDateTime.now();
    }
}

class RateLimiter {
    Rule rule;
    RateLimiterStrategy rateLimiterStrategy;

    public RateLimiter(Rule rule, RateLimiterStrategy rateLimiterStrategy) {
        this.rule = rule;
        this.rateLimiterStrategy = rateLimiterStrategy;
    }

    public void setRateLimiterStrategy(RateLimiterStrategy s) {
        this.rateLimiterStrategy = s;
    }

    public boolean allow(String key, Request req) {
        return rateLimiterStrategy.allow(key, req, rule);
    }
}

interface RateLimiterStrategy {
    boolean allow(String key, Request request, Rule rule);
}

class FixedWindowRateLimiter implements RateLimiterStrategy {
    private final Store store = Store.getInstance();

    @Override
    public boolean allow(String key, Request request, Rule rule) {

        long timestamp = request.timeStamp.toEpochSecond(ZoneOffset.UTC);
        long window = timestamp / rule.windowSecs;
        String windowKey = key + ":" + window;

        int count = store.fixedWindowCounter.getOrDefault(windowKey, 0);

        if (count >= rule.maxReqs)
            return false;

        store.fixedWindowCounter.put(windowKey, count + 1);
        return true;
    }
}

class SlidingWindowRateLimiter implements RateLimiterStrategy {
    private final Store store = Store.getInstance();

    @Override
    public boolean allow(String key, Request request, Rule rule) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusSeconds(rule.windowSecs);

        List<Request> list = store.slidingWindowRequests.getOrDefault(key, new ArrayList<>());
        
        int i = 0;
        while (i < list.size() && list.get(i).timeStamp.isBefore(windowStart)) {
            i++;
        }
        if (i > 0) list.subList(0, i).clear();

        if (list.size() >= rule.maxReqs)
            return false;

        list.add(request);
        store.slidingWindowRequests.put(key, list);
        return true;
    }
}

class TokenBucket {
    int capacity;
    int tokens;
    int refillRatePerSec; // tokens added per second
    long lastRefillTime;  // epoch seconds

    public TokenBucket(int capacity, int refillRatePerSec) {
        this.capacity = capacity;
        this.tokens = capacity;  // bucket starts full
        this.refillRatePerSec = refillRatePerSec;
        this.lastRefillTime = System.currentTimeMillis() / 1000;
    }

    public void refill() {
        long now = System.currentTimeMillis() / 1000;
        long seconds = now - lastRefillTime;

        int added = (int) (seconds * refillRatePerSec);
        if (added > 0) {
            tokens = Math.min(capacity, tokens + added);
            lastRefillTime = now;
        }
    }
}

class TokenBucketRateLimiter implements RateLimiterStrategy {
    private final Store store = Store.getInstance();

    @Override
    public boolean allow(String key, Request request, Rule rule) {

        TokenBucket bucket = store.tokenBuckets.getOrDefault(
                key,
                new TokenBucket(rule.maxReqs, rule.maxReqs / rule.windowSecs)
        );

        bucket.refill();

        if (bucket.tokens <= 0) {
            store.tokenBuckets.put(key, bucket);
            return false;
        }

        bucket.tokens -= 1;
        store.tokenBuckets.put(key, bucket);
        return true;
    }
}

public class Solution {
    public static void main(String[] args) {

        Rule rule = new Rule(RuleType.ENDPOINT, 3, 10);
        RateLimiter limiter = new RateLimiter(rule, new SlidingWindowRateLimiter());

        for (int i = 0; i < 5; i++) {
            boolean allowed = limiter.allow("USER_1", new Request("USER_1", "1.1.1.1", "/login"));
            System.out.println("Request " + i + " allowed = " + allowed);
        }
    }
}
