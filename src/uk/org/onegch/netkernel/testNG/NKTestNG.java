package uk.org.onegch.netkernel.testNG;

import java.util.Map;

import org.testng.ITestListener;
import org.testng.TestNG;
import org.testng.TestNGCommandLineArgs;
import org.testng.TestNGException;
import org.testng.TestRunner;

public class NKTestNG extends TestNG {
  
  @Override
  public void run() {
    NKRunner runner= new NKRunner();
    runner.start();
    super.run();
    runner.stop();
  }
  
  public static NKTestNG privateMain(String[] argv, ITestListener listener) {
    Map arguments= checkConditions(TestNGCommandLineArgs.parseCommandLine(argv));
    
    NKTestNG result = new NKTestNG();
    if (null != listener) {
      result.addListener(listener);
    }

    result.configure(arguments);
    try {
      result.run();
    }
    catch(TestNGException ex) {
      if (TestRunner.getVerbose() > 1) {
        ex.printStackTrace(System.out);
      }
      else {
        System.err.println("[ERROR]: " + ex.getMessage());
      }
      result.setStatus(HAS_FAILURE);
    }
    
    return result;
  }
  
  public static void main(String[] argv) {
    NKTestNG testng = privateMain(argv, null);
    System.exit(testng.getStatus());
  }
}
