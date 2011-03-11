package uk.org.onegch.netkernel.testNG;

import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.testng.*;

public class NKTestNG {
  public static void main(String[] argv) {
    TestNG testng = privateMain(argv, null);
    System.exit(testng.getStatus());
  }

  public static TestNG privateMain(String[] argv, ITestListener testListener) {
    NKRunner runner= new NKRunner();
    runner.start();
    try {
      return TestNG.privateMain(argv, testListener);
    } catch (RuntimeException t) {
      throw t;
    } finally {
      runner.stop();
    }
  }
}
