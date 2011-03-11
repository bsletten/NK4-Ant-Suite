package uk.org.onegch.netkernel.testNG.test;

import junit.framework.TestListener;
import org.testng.*;
import org.testng.annotations.Test;
import uk.org.onegch.netkernel.testNG.NKTestNG;

public class TestNGRunner {
  @Test
  public void quickTest() {
    Assert.assertTrue(true, "Test1");
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("uk.org.onegch.netkernel.testNG.modules", "test/urn.test.uk.org.onegch.netkernel.test");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNG testng = NKTestNG.privateMain(new String[]{"test-src/testng.xml"}, new ITestListener() {
      public void onTestStart(ITestResult iTestResult) {
        System.out.println("testStart");
      }

      public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("testSuccess");
      }

      public void onTestFailure(ITestResult iTestResult) {
        System.out.println("testFailure");
      }

      public void onTestSkipped(ITestResult iTestResult) {
        System.out.println("testSkipped");
      }

      public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("testFailedButWithinSuccessPercentage");
      }

      public void onStart(ITestContext iTestContext) {
        System.out.println("onStart");
      }

      public void onFinish(ITestContext iTestContext) {
        System.out.println("onFinish");
      }
    });
    testng.getStatus();
  }

 /* @Test
  public void quickTest2() {
    Assert.assertTrue(true, "Test2");
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("uk.org.onegch.netkernel.testNG.modules", "test/urn.test.uk.org.onegch.netkernel.test");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNG testng = NKTestNG.privateMain(new String[] {"test-src/testng.xml"}, null);
  }
*/
  public static void main(String[] args) {

  }
}
