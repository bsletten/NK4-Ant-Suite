package org.netkernelroc.antTools.testNG;

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
import org.apache.http.client.methods.HttpGet;
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

/******************************************************************************
 * (c) Copyright 2002,2007, 1060 Research Ltd
 *
 * This Software is licensed to You, the licensee, for use under the terms of
 * the 1060 Public License v1.0. Please read and agree to the 1060 Public
 * License v1.0 [www.1060research.com/license] before using or redistributing
 * this software.
 *
 * In summary the 1060 Public license has the following conditions.
 * A. You may use the Software free of charge provided you agree to the terms
 * laid out in the 1060 Public License v1.0
 * B. You are only permitted to use the Software with components or applications
 * that provide you with OSI Certified Open Source Code [www.opensource.org], or
 * for which licensing has been approved by 1060 Research Limited.
 * You may write your own software for execution by this Software provided any
 * distribution of your software with this Software complies with terms set out
 * in section 2 of the 1060 Public License v1.0
 * C. You may redistribute the Software provided you comply with the terms of
 * the 1060 Public License v1.0 and that no warranty is implied or given.
 * D. If you find you are unable to comply with this license you may seek to
 * obtain an alternative license from 1060 Research Limited by contacting
 * license@1060research.com or by visiting www.1060research.com
 *
 * NO WARRANTY:  THIS SOFTWARE IS NOT COVERED BY ANY WARRANTY. SEE 1060 PUBLIC
 * LICENSE V1.0 FOR DETAILS
 *
 * THIS COPYRIGHT NOTICE IS *NOT* THE 1060 PUBLIC LICENSE v1.0. PLEASE READ
 * THE DISTRIBUTED 1060_Public_License.txt OR www.1060research.com/license
 *
 * File:          $RCSfile$
 * Version:       $Name$ $Revision$
 * Last Modified: $Date$
 *****************************************************************************/

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
