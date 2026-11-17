package com.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Logs the start and result of every test. Registered in testng.xml.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("===== Starting <test>: {} =====", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("START  {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("PASS   {} ({} ms)", testName(result), duration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("FAIL   {} ({} ms): {}", testName(result), duration(result), result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("SKIP   {}", testName(result));
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("===== Finished <test>: {} | passed={} failed={} skipped={} =====",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    private String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
    }

    private long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
