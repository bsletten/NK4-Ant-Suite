package uk.org.onegch.netkernel.antTools.testNG.test;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import uk.org.onegch.netkernel.antTools.testNG.TestRunner;

import java.util.HashMap;
import java.util.Map;

public class TestNKInvokerTestListener extends TestListenerAdapter {
  private Map<String, ITestResult> testResults= new HashMap<String, ITestResult>();
  private int testCount= 0;
  private int successCount= 0;
  private int failureCount= 0;
  private int skippedCount= 0;
  private int failedButWithinSuccessPercentageCount= 0;

  private String calculateName(ITestResult iTestResult) {
    if (iTestResult.getInstance() instanceof TestRunner) {
      Object[] parameters= iTestResult.getParameters();
      return parameters[0] + " / " + parameters[1];

    } else {
      return iTestResult.getTestName();
    }
  }

  @Override
  public void onTestStart(ITestResult iTestResult) {
    testCount++;
  }

  @Override
  public void onTestSuccess(ITestResult iTestResult) {
    successCount++;
    testResults.put(calculateName(iTestResult), iTestResult);
  }

  @Override
  public void onTestFailure(ITestResult iTestResult) {
    failureCount++;
    testResults.put(calculateName(iTestResult), iTestResult);
  }

  @Override
  public void onTestSkipped(ITestResult iTestResult) {
    skippedCount++;
    testResults.put(calculateName(iTestResult), iTestResult);
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
    failedButWithinSuccessPercentageCount++;
    testResults.put(calculateName(iTestResult), iTestResult);
  }

  public Map<String, ITestResult> getTestResults() {
    return testResults;
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
