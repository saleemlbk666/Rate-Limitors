package com.rate.limitors;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Queue;

public class LeakyBucket {
    private final int capacity;                     // Maximum number of requests the bucket can hold
    private final int leakRate;                     // Rate at which requests leak out of the bucket (requests per second)
    private final Queue<LocalDateTime> bucket;      // Queue to hold timestamps of requests
    private LocalDateTime lastLeakedTime;           // Last time we leaked from the bucket

    public LeakyBucket(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.bucket = new ArrayDeque<>();
        this.lastLeakedTime = LocalDateTime.now();
    }

    public boolean allowRequest() {
        LocalDateTime currentTime = LocalDateTime.now();
        long leakTime = Duration.between(lastLeakedTime, currentTime).getSeconds();
        long leakedDrops = this.leakRate * leakTime;

        if (leakedDrops > 0) {
            for (int i = 0; i < Math.min(leakedDrops, capacity); i++)
                bucket.poll();
            lastLeakedTime = LocalDateTime.now();
        }

        if (bucket.size() < this.capacity) {
            this.bucket.add(LocalDateTime.now());
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        LeakyBucket leakyBucket = new LeakyBucket(10, 1);

        for (int i = 0; i < 20; i++) {
            System.out.println("Request " + i + " acceptance status: " + leakyBucket.allowRequest());
            Thread.sleep(200);
        }
    }
}
