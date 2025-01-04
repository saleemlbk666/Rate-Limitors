package com.rate.limitors;

import java.time.Duration;
import java.time.LocalDateTime;

public class TokenBucket {
    private final int capacity;             // Maximum number of tokens the bucket can hold
    private final int refillRate;           // Rate at which tokens are added to the bucket (tokens per second)
    private int availableTokens;            // Current number of tokens in the bucket
    private LocalDateTime lastTimeRefilled; // Last time we refilled the bucket

    public TokenBucket(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.availableTokens = capacity;
        this.lastTimeRefilled = LocalDateTime.now();
    }

    public boolean allowRequest(final int requests) {
        LocalDateTime timeNow = LocalDateTime.now();
        long timePassed = Duration.between(lastTimeRefilled, timeNow).getSeconds();
        long refillTokens = this.refillRate *timePassed;
        if(refillTokens > 0)
            this.lastTimeRefilled = LocalDateTime.now();
        this.availableTokens = (int) Math.min(this.capacity, this.availableTokens + refillTokens);

        if (this.availableTokens >= requests) {
            this.availableTokens = this.availableTokens - requests;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket tokenBucket = new TokenBucket(10, 5);

        for (int i = 0; i < 20; i++) {
            System.out.println("Request " + i + " Allowed? -> " + tokenBucket.allowRequest(1));
            Thread.sleep(100);
        }

        Thread.sleep(1000);

        System.out.println("Request 20 Allowed? -> " + tokenBucket.allowRequest(1));
    }
}
