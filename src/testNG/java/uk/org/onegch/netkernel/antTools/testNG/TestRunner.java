package uk.org.onegch.netkernel.antTools.testNG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.List;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestRunner {
  @Test
  @Parameters(value = {"identifier", "name"})
  public void executeTest(String identifier, String name) {
    try {
      HttpClient httpclient= new DefaultHttpClient();
      
      String backendPort= System.getProperty("netkernel.http.backend.port", "1060");

      try {
        HttpGet httpget = new HttpGet("http://127.0.0.1:" + backendPort + "/test/exec/xml/" + identifier); 

        HttpResponse response = httpclient.execute(httpget);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String responseBody= EntityUtils.toString(entity);

        File responseFile= new File("test-output/test-responses/" + identifier + ".xml");
        if (!responseFile.exists()) {
          responseFile.getParentFile().mkdirs();
          responseFile.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(responseFile);
        fos.write(responseBody.getBytes());
        fos.close();

        Assert.assertEquals(statusCode, 200, "Incorrect HTTP status code");
        SAXReader saxReader= new SAXReader();
        Document doc= saxReader.read(new StringReader(responseBody));

                                     XPath spaceXpath = DocumentHelper.createXPath("/testlist/results/space/text()");
        Node spaceNode= spaceXpath.selectSingleNode(doc);
        String space= ((Text) spaceNode).getText();

        XPath versionXpath = DocumentHelper.createXPath("/testlist/results/version/text()");
        Node versionNode= versionXpath.selectSingleNode(doc);
        String version= ((Text) versionNode).getText();

        XPath uriXpath = DocumentHelper.createXPath("/testlist/results/uri/text()");
        Node uriNode= uriXpath.selectSingleNode(doc);
        String uri= ((Text) uriNode).getText();

        XPath testsXpath = DocumentHelper.createXPath("//test");
        List<? extends Node> results = testsXpath.selectNodes(doc);
        for (Node node : results) {
          if (node instanceof Element) {
            String aName= ((Element) node).attributeValue("name");
            String testStatus= ((Element) node).attributeValue("testStatus");
            if (aName.equals(name)) {
              Assert.assertTrue(testStatus.equals("success"), space + " (" + version + ") [" + uri + "] " + name);
            }
          }
        }
      } catch (Exception e) {
        Assert.fail("Unxpected exception running tests for " + identifier, e);
        httpclient.getConnectionManager().shutdown();
      }
    } catch (AssertionError e) {
      throw e;
    }
  }
  
  public static void main(String[] args) throws Exception {
    TestRunner tr= new TestRunner();
  }
}
