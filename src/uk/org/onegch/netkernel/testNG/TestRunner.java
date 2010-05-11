package uk.org.onegch.netkernel.testNG;

import java.io.StringReader;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
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
      
      try {
        HttpGet httpget = new HttpGet("http://127.0.0.1:1060/test/exec/xml/" + identifier); 
        
        // Create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpget, responseHandler);
        
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
