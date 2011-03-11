package uk.org.onegch.netkernel.testNG.test;

import org.testng.ITestResult;
import uk.org.onegch.netkernel.testNG.NKInvokerTestListener;

import java.util.HashMap;
import java.util.Map;

public class TestNKInvokerTestListener extends NKInvokerTestListener {
  private Map<String, ITestResult> testResults= new HashMap<String, ITestResult>();
  private int testCount= 0;
  private int successCount= 0;
  private int failureCount= 0;
  private int skippedCount= 0;
  private int failedButWithinSuccessPercentageCount= 0;

  @Override
  public void onTestStart(ITestResult iTestResult) {
    testCount++;
  }

  @Override
  public void onTestSuccess(ITestResult iTestResult) {
    successCount++;
    testResults.put(iTestResult.getName(), iTestResult);
  }

  @Override
  public void onTestFailure(ITestResult iTestResult) {
    failureCount++;
    testResults.put(iTestResult.getName(), iTestResult);
  }

  @Override
  public void onTestSkipped(ITestResult iTestResult) {
    skippedCount++;
    testResults.put(iTestResult.getName(), iTestResult);
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
    failedButWithinSuccessPercentageCount++;
    testResults.put(iTestResult.getName(), iTestResult);
  }

  public int getTestCount() {
    return testCount;
  }

  public int getSuccessCount() {
    return successCount;
  }

  public int getFailureCount() {
    return failureCount;
  }

  public int getSkippedCount() {
    return skippedCount;
  }

  public int getFailedButWithinSuccessPercentageCount() {
    return failedButWithinSuccessPercentageCount;
  }
}
