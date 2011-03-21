package uk.org.onegch.netkernel.antTools.testNG;

import org.testng.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NKInvokerSuiteListener implements ISuiteListener {
  Map<ISuite, NKRunner> runnerMap= new HashMap<ISuite, NKRunner>();

  public void onStart(ISuite iSuite) {
    
    NKRunner runner= new NKRunner();
    runnerMap.put(iSuite, runner);
    runner.start();
  }

  public void onFinish(ISuite iSuite) {
    NKRunner runner= runnerMap.get(iSuite);
    runner.stop();
    runnerMap.remove(iSuite);
  }
}
