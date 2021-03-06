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

import org.netkernelroc.antTools.testNG.NKInvokerSuiteListener;
import org.testng.*;
import org.testng.annotations.Test;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestNGRunner {
  @Test
  public void testSuccess() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-success.xml");
    
    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 1, "Only 1 test should have been run");
    Assert.assertEquals(listener.getSuccessCount(), 1, "Only 1 test should have been successful");
  }

  @Test
  public void testFailure() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-failure.xml");

    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 1, "Only 1 test should have been run");
    Assert.assertEquals(listener.getFailureCount(), 1, "Only 1 test should have failed");
  }

  @Test
  public void testException() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-exception.xml");

    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 1, "Only 1 test should have been run");
    Assert.assertEquals(listener.getFailureCount(), 1, "Only 1 test should have failed");
  }

  @Test
  public void testMixed() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-mixed.xml");

    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 3, "Only 3 tests should have been run");
    Assert.assertEquals(listener.getSuccessCount(), 1, "Only 1 test should have been successful");
    Assert.assertEquals(listener.getFailureCount(), 2, "Only 2 tests should have failed");

    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:mixedTest / successTest").getStatus(),
                        ITestResult.SUCCESS, "Success test should be successful");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:mixedTest / failureTest").getStatus(),
                        ITestResult.FAILURE, "Failure test should be a failure");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:mixedTest / exceptionTest").getStatus(),
                        ITestResult.FAILURE, "Exception test should be a failure");
  }

  @Test
  public void testMultipleSuccess() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-multipleSuccess.xml");

    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 2, "Only 2 tests should have been run");
    Assert.assertEquals(listener.getSuccessCount(), 2, "Only 2 tests should have been successful");

    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleSuccessTest / successTest1").getStatus(),
                        ITestResult.SUCCESS, "Success1 test should be successful");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleSuccessTest / successTest2").getStatus(),
                        ITestResult.SUCCESS, "Success2 test should be successful");
  }

  @Test
  public void testMultipleFailure() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-multipleFailure.xml");

    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 2, "Only 2 tests should have been run");
    Assert.assertEquals(listener.getFailureCount(), 2, "Only 2 tests should have been a failure");

    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleFailureTest / failureTest1").getStatus(),
                        ITestResult.FAILURE, "Failure1 test should be a failure");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleFailureTest / failureTest2").getStatus(),
                        ITestResult.FAILURE, "Failure2 test should be a failure");
  }

  @Test
  public void testMultipleException() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-multipleException.xml");

    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 2, "Only 2 tests should have been run");
    Assert.assertEquals(listener.getFailureCount(), 2, "Only 2 tests should have been exceptionful");

    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleExceptionTest / exceptionTest1").getStatus(),
                        ITestResult.FAILURE, "Exception1 test should be a failure");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleExceptionTest / exceptionTest2").getStatus(),
                        ITestResult.FAILURE, "Exception2 test should be a failure");
  }

  @Test
  public void testMultipleMixed() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-multipleMixed.xml");

    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 6, "Only 6 tests should have been run");
    Assert.assertEquals(listener.getSuccessCount(), 2, "Only 1 test should have been successful");
    Assert.assertEquals(listener.getFailureCount(), 4, "Only 2 tests should have failed");

    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleMixedTest / successTest1").getStatus(),
                        ITestResult.SUCCESS, "Success1 test should be successful");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleMixedTest / successTest2").getStatus(),
                        ITestResult.SUCCESS, "Success2 test should be successful");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleMixedTest / failureTest1").getStatus(),
                        ITestResult.FAILURE, "Failure1 test should be a failure");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleMixedTest / failureTest2").getStatus(),
                        ITestResult.FAILURE, "Failure2 test should be a failure");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleMixedTest / exceptionTest1").getStatus(),
                        ITestResult.FAILURE, "Exception1 test should be a failure");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:multipleMixedTest / exceptionTest2").getStatus(),
                        ITestResult.FAILURE, "Exception2 test should be a failure");
  }

  @Test
  public void testMultipleSuites() throws Exception {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("org.netkernelroc.antTools.testNG.modules", "test/modules/urn.test.org.netkernelroc.antTools.testNG.test/");
    System.setProperty("org.netkernelroc.antTools.testNG.moduleConf", "build/download-cache/modules.conf");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser successParser= new Parser("test/src/testng-success.xml");
    Parser failureParser= new Parser("test/src/testng-failure.xml");
    Parser exceptionParser= new Parser("test/src/testng-exception.xml");

    List<XmlSuite>suites= new ArrayList<XmlSuite>();
    suites.addAll(successParser.parse());
    suites.addAll(failureParser.parse());
    suites.addAll(exceptionParser.parse());

    TestNG testng = new TestNG();
    testng.setXmlSuites(suites);

    testng.addListener(listener);
    testng.addListener(new NKInvokerSuiteListener());
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 3, "Only 3 tests should have been run");
    Assert.assertEquals(listener.getSuccessCount(), 1, "Only 1 test should have been successful");
    Assert.assertEquals(listener.getFailureCount(), 2, "Only 2 tests should have failed");

    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:successTest / successTest").getStatus(),
                        ITestResult.SUCCESS, "Success test should be successful");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:failureTest / failureTest").getStatus(),
                        ITestResult.FAILURE, "Failure test should be a failure");
    Assert.assertEquals(listener.getTestResults().get("urn:test:org:netkernelroc:antTools:testNG:test:exceptionTest / exceptionTest").getStatus(),
                        ITestResult.FAILURE, "Exception test should be a failure");
  }
}
