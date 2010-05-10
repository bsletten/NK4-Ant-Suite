package uk.org.onegch.netkernel.testNG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.netkernel.container.IKernelListener;
import org.netkernel.container.ILogger;
import org.netkernel.container.impl.Kernel;
import org.netkernel.layer0.boot.IModuleFactory;
import org.netkernel.layer0.boot.ModuleManager;
import org.netkernel.layer0.logging.LogManager;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.tools.ExtraMimeTypes;
import org.netkernel.layer0.util.Layer0Factory;
import org.netkernel.module.standard.StandardModuleFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ten60.netkernel.cache.se.representation2.ConcurrentCache;

public class TestRunner {
  private static int SYSTEM_RUN_LEVEL_START = 1;
  private ModuleManager mModuleManager;
  private ConcurrentCache mRepresentationCache;
  private INKFRequestContext mContext;
  
  public TestRunner() throws Exception {
    // Create a new micro-kernel.
    Kernel kernel = new Kernel();

    // NetKernel adds support for missing MIME types to the JDK.
    ExtraMimeTypes.getInstance();

    // NetKernel requires a logger
    ILogger logger = new LogManager(null).getKernelLogger();
    
    TestRunnerConfiguration config= new TestRunnerConfiguration();
    config.setString("netkernel.scheduler.threadcount", "4");
    config.setString("netkernel.scheduler.thread.timeout", "2000");
    config.setString("netkernel.schedulre.maxstackdepth", "20");
    config.setString("netkernel.debug", "true");
    config.setString("netkernel.exception.javastack", "10");
    config.setString("netkernel.poll", "500" /* ms */);
    config.setString("netkernel.statistics.period", "2000");
    config.setString("netkernel.statistics.window", "360");
    config.setString("netkernel.statistics.multiplier", "10");
    config.setString("se.concurrentcache.headroomMb", "9");
    config.setString("se.concurrentcache.cull%", "33");
    config.setString("se.concurrentcache.maxsize", "6000");
    config.setString("se.resolutioncache.size", "200");
    config.setString("se.resolutioncaache.interval", "100");
    config.setString("se.resolutioncache.touch", "1000");
    config.setString("netkernel.instance.identifier", "testng");
    config.setString("netkernel.instance.version.major", "4");
    config.setString("netkernel.instance.version.minor", "0");
    config.setString("netkernel.instance.product", "NetKernel Embedded TestNG");
    config.setString("netkernel.boot.time", Long.toString(System.currentTimeMillis()));
    
    kernel.setConfiguration(config);
    kernel.setLogger(logger);
    
    mRepresentationCache = new ConcurrentCache(kernel);
    kernel.setRepresentationCache(mRepresentationCache);
    kernel.addConfigurationListener(mRepresentationCache);
    
    IKernelListener monitor = Layer0Factory.createMonitor(kernel);
    kernel.setMonitor(monitor);
    
    // Instantiate the modules factories - only use StandardModule here.
    IModuleFactory[] factories = new IModuleFactory[]{new StandardModuleFactory()};

    // Create a Module Manager
    mModuleManager = new ModuleManager(kernel, factories);
    
    // Tell ModuleManager about the modules we want to use.
    InputStream is;
    
    is = new FileInputStream(new File("testNG-nk4/modules.conf"));

    BufferedReader r = new BufferedReader(new InputStreamReader(is));
    String line;
    while ((line = r.readLine()) != null) {
      line = line.trim();
      if (!line.startsWith("#") && line.length() > 0) {
        File moduleFile = new File(line);
        URI moduleURI = moduleFile.toURI();
        mModuleManager.addModule(moduleURI, SYSTEM_RUN_LEVEL_START);
      }
    }
  }
  
  @BeforeClass()
  public void start() {
    mModuleManager.setRunLevel(SYSTEM_RUN_LEVEL_START);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {}
  }
  
  @AfterClass()
  public void stop() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {}
    mModuleManager.stop();
    mRepresentationCache.stop();
  }
  
  public void executeTest(String identifier) {
    this.start();
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
            String name= ((Element) node).attributeValue("name");
            String testStatus= ((Element) node).attributeValue("testStatus");
            
            Assert.assertTrue(testStatus.equals("success"), space + " (" + version + ") [" + uri + "] " + name);
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
  
  @Test(description= "urn:test:uk:org:onegch:netkernel:ciexperiment")
  public void blahTest() throws ClientProtocolException, IOException, DocumentException {
    executeTest("urn:test:uk:org:onegch:netkernel:ciexperiment");
  }
  
  @Test(description= "urn:test:uk:org:onegch:netkernel:ciexperiment2")
  public void blah2Test() throws ClientProtocolException, IOException, DocumentException {
    executeTest("urn:test:uk:org:onegch:netkernel:ciexperiment");
  }
  
  public static void main(String[] args) throws Exception {
    TestRunner tr= new TestRunner();
    
    tr.blahTest();
  }
}
