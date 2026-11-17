package com.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Re-runs a failed test up to "retry.count" times (default 1), for flaky network failures.
 * Run with -Dretry.count=0 to switch retries off.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY = Integer.parseInt(System.getProperty("retry.count", "1"));

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < MAX_RETRY) {
            attempt++;
            log.warn("RETRY  {} (attempt {} of {})", result.getMethod().getMethodName(), attempt, MAX_RETRY);
            return true;
        }
        return false;
    }
}
