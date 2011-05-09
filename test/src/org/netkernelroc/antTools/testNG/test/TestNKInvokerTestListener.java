/*
 * Copyright (c) 2010-2011 Christopher Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.netkernelroc.antTools.testNG.test;

import org.netkernelroc.antTools.testNG.TestRunner;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

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
