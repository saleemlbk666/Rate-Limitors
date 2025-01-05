package com.rate.limitors;

import java.time.Instant;

public class FixedWindowCounter {
    private final int maxRequestsPerWindow;     // Maximum number of requests allowed per window
    private final int windowSize;               // Size of each window in seconds
    private long currentWindow;                 // Start time of the current window
    private int requestCount;                   // Number of requests in the current window

    public FixedWindowCounter(int windowSize, int maxRequestsPerWindow) {
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSize = windowSize;
        this.currentWindow = Math.floorDiv(Instant.now().getEpochSecond(), windowSize);
        this.requestCount = 0;
    }

    public boolean allowRequest() {
        long currentTime = Instant.now().getEpochSecond();
        long window = Math.floorDiv(currentTime, windowSize);
        if (window != currentWindow) {
            currentWindow = window;
            requestCount = 0;
        }

        if (requestCount < maxRequestsPerWindow) {
            requestCount = requestCount + 1;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        FixedWindowCounter rateLimiter = new FixedWindowCounter(60, 5); // 5 requests per minute

        for (int i = 0; i < 15; i++) {
            System.out.println("Request " + i + " acceptance status: " + rateLimiter.allowRequest());
            Thread.sleep(100);
        }
    }
}
