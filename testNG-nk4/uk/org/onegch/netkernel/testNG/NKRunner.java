package uk.org.onegch.netkernel.testNG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.netkernel.container.IKernelListener;
import org.netkernel.container.ILogger;
import org.netkernel.container.impl.Kernel;
import org.netkernel.layer0.boot.IModuleFactory;
import org.netkernel.layer0.boot.ModuleManager;
import org.netkernel.layer0.logging.LogManager;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernel.layer0.tools.ExtraMimeTypes;
import org.netkernel.layer0.util.Layer0Factory;
import org.netkernel.module.standard.StandardModuleFactory;

import com.ten60.netkernel.cache.se.representation2.ConcurrentCache;

public class NKRunner {

  private static int SYSTEM_RUN_LEVEL_START = 1;
  private ModuleManager mModuleManager;
  private ConcurrentCache mRepresentationCache;
  private INKFRequestContext mContext;
  
  public NKRunner() {
    try {
      // Create a new micro-kernel.
      Kernel kernel = new Kernel();
  
      // NetKernel adds support for missing MIME types to the JDK.
      ExtraMimeTypes.getInstance();
      
      // NetKernel requires a logger
      LogManager lm= new LogManager(new File("").getCanonicalPath());
      
      ILogger logger = lm.getKernelLogger();
      
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
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public void start() {
    mModuleManager.setRunLevel(SYSTEM_RUN_LEVEL_START);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {}
  }
  
  public void stop() {
    try {
      Thread.sleep(1000);
      mModuleManager.stop();
      mRepresentationCache.stop();
      Thread.sleep(3000);
    } catch (InterruptedException e) {}
  }
}
