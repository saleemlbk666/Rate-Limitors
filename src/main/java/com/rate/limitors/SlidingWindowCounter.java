package com.rate.limitors;

import java.time.Instant;

public class SlidingWindowCounter {
    private final int windowSize; // Size of the sliding window in seconds
    private final int maxRequests; // Maximum number of requests allowed in the window
    private long currentWindow; // Start time of the current window
    private int currentWindowCount; // Number of requests in the current window
    private int previousWindowCount; // Number of requests in the previous window


    public SlidingWindowCounter(int windowSize, int maxRequests) {
        this.windowSize = windowSize;
        this.maxRequests = maxRequests;
        this.currentWindow = Math.floorDiv(Instant.now().getEpochSecond(), windowSize);
        this.currentWindowCount = 0;
        this.previousWindowCount = 0;
    }

    public boolean allowRequest() {
        long currentTime = Instant.now().getEpochSecond();
        long window = Math.floorDiv(currentTime, windowSize);

        if (window != currentWindow) {
            previousWindowCount = currentWindowCount;
            currentWindowCount = 0;
            currentWindow = window;
        }

        long windowElapsed = (currentTime % windowSize) / windowSize;
        long threshold = previousWindowCount * (1 - windowElapsed) + currentWindowCount;

        if (threshold < maxRequests) {
            currentWindowCount++;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowCounter rateLimiter = new SlidingWindowCounter(60, 5); // 5 requests per minute

        for (int i = 0; i < 10; i++) {
            System.out.println("Request " + i + " acceptance status: " + rateLimiter.allowRequest());
            Thread.sleep(100);
        }
        Thread.sleep(30000); // wait for 30 seconds
        System.out.println("Request 10 acceptance status: " + rateLimiter.allowRequest()); // Might be true or false depending on the exact timing
    }
}
