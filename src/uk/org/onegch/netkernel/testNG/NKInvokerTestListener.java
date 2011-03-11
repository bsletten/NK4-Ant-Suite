package uk.org.onegch.netkernel.testNG;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import uk.org.onegch.netkernel.testNG.NKRunner;

public class NKInvokerTestListener implements ITestListener {
  NKRunner runner= new NKRunner();

  public void onTestStart(ITestResult iTestResult) {}

  public void onTestSuccess(ITestResult iTestResult) {}

  public void onTestFailure(ITestResult iTestResult) {}

  public void onTestSkipped(ITestResult iTestResult) {}

  public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {}

  public void onStart(ITestContext iTestContext) {
    runner.start();
  }

  public void onFinish(ITestContext iTestContext) {
    runner.stop();
  }
}
