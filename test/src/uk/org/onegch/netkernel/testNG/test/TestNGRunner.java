package uk.org.onegch.netkernel.testNG.test;

import org.testng.*;
import org.testng.annotations.Test;
import org.testng.xml.Parser;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.xml.sax.SAXException;
import uk.org.onegch.netkernel.testNG.NKInvokerTestListener;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestNGRunner {
  @Test
  public void testSuccess() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("uk.org.onegch.netkernel.testNG.modules", "test/modules/urn.test.uk.org.onegch.netkernel.test");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-success.xml");
    
    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 1, "Only 1 test should have been run");
    Assert.assertEquals(listener.getSuccessCount(), 1, "Only 1 test should have been successful");
  }

  @Test
  public void testFailure() throws IOException, SAXException, ParserConfigurationException {
    System.setProperty("java.protocol.handler.pkgs", "org.ten60.netkernel.protocolhandler");
    System.setProperty("uk.org.onegch.netkernel.testNG.modules", "test/modules/urn.test.uk.org.onegch.netkernel.test");
    System.setProperty("netkernel.http.backend.port", "1068");

    TestNKInvokerTestListener listener= new TestNKInvokerTestListener();

    Parser parser= new Parser("test/src/testng-failure.xml");

    TestNG testng = new TestNG();
    testng.setXmlSuites((List<XmlSuite>) parser.parse());
    testng.addListener(listener);
    testng.run();

    Assert.assertEquals(listener.getTestCount(), 1, "Only 1 test should have been run");
    Assert.assertEquals(listener.getFailureCount(), 1, "Only 1 test should have failed");
  }

}
