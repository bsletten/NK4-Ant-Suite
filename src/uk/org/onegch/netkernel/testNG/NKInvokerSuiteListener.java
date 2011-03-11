package uk.org.onegch.netkernel.testNG;

import org.testng.*;
import uk.org.onegch.netkernel.testNG.NKRunner;

import java.util.HashMap;
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
