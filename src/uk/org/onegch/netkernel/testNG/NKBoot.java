package uk.org.onegch.netkernel.testNG;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.netkernel.container.IKernelListener;
import org.netkernel.container.ILogger;
import org.netkernel.container.impl.Kernel;
import org.netkernel.layer0.boot.IModuleFactory;
import org.netkernel.layer0.boot.ModuleManager;
import org.netkernel.layer0.logging.LogManager;
import org.netkernel.layer0.tools.ExtraMimeTypes;
import org.netkernel.layer0.util.FastSchematron;
import org.netkernel.layer0.util.Layer0Factory;
import org.netkernel.layer0.util.PropertyConfiguration;
import org.netkernel.urii.INetKernelThrowable;
import org.netkernel.util.Utils;

import com.ten60.netkernel.cache.se.representation2.ConcurrentCache;
import com.ten60.netkernel.cache.se.resolution.ResolutionCache;

/**
 * Inner boot class loaded once kernel, layer0 and classic are in the classpath
 * 
 * @author tab
 */
public class NKBoot {
  protected static String EXPAND_PREFIX="netkernel_";
  
  private ConcurrentCache cache = null;
  private ModuleManager mm = null;

  public NKBoot(String installDir) {
    String classpath = System.getProperty("java.class.path");
    int j = classpath.indexOf(File.pathSeparatorChar);
    if (j > 0) {
      classpath = classpath.substring(0, j);
    }
    File expandDir = null;
    String expandDirString = System.getProperty("netkernel.expand.dir");
    if (expandDirString != null) {
      expandDir = new File(expandDirString);
    }

    Kernel k = new Kernel();

    try {
      // initialise mimetype stuff
      ExtraMimeTypes emt = ExtraMimeTypes.getInstance();

      // construct and set logger
      ILogger logger = new LogManager(new File("").getCanonicalPath()).getKernelLogger();

      // construct and set kernel configuration provider
      PropertyConfiguration config = new PropertyConfiguration(new URL(installDir + "/etc/kernel.properties"), logger);
      
      config.setProperty("netkernel.install.path", installDir);
      config.setProperty("netkernel.boot.time", Long.toString(System
          .currentTimeMillis()));
      k.setConfiguration(config);
      k.setLogger(logger);

      // construct and set cache implementation
      cache = new ConcurrentCache(k);
      k.setRepresentationCache(cache);
      k.addConfigurationListener(cache);
      ResolutionCache rcache = new ResolutionCache(k);
      k.setResolutionCache(rcache);
      k.addConfigurationListener(rcache);

      IKernelListener monitor = Layer0Factory.createMonitor(k);
      k.setMonitor(monitor);

      FastSchematron.loadValidationCache();

      // load some contexts
      List<IModuleFactory> mf = new ArrayList<IModuleFactory>();
      ClassLoader cl = this.getClass().getClassLoader();
      Enumeration<URL> mfcs = cl.getResources("etc/moduleFactory.conf");
      while (mfcs.hasMoreElements()) {
        URL url = mfcs.nextElement();
        try {
          BufferedReader r = new BufferedReader(new InputStreamReader(url
              .openStream()));
          String moduleFactoryClassname = r.readLine();
          r.close();
          Class moduleFactoryClass = cl.loadClass(moduleFactoryClassname);
          mf.add((IModuleFactory) moduleFactoryClass.newInstance());
        } catch (Exception e2) {
          logger.logRaw(ILogger.WARNING, this, Utils.throwableToString(e2));
        }
      }
      IModuleFactory[] factories = new IModuleFactory[mf.size()];
      mf.toArray(factories);

      JarFile jarFile = new JarFile(classpath);
      Enumeration e = jarFile.entries();
      mm = new ModuleManager(k, factories);

      String modPrefix = "modules/";
      while (e.hasMoreElements()) {
        JarEntry entry = (JarEntry) e.nextElement();
        if (entry.isDirectory())
          continue;
        String name = entry.getName();
        if (name.startsWith(modPrefix)
            && (name.endsWith(".jar") || name.endsWith(".sjar"))) {
          name = name.substring(modPrefix.length(), name.length());
          try {
            InputStream is = jarFile.getInputStream(entry);
            File expanded = expandJar(is, name, expandDir);
            URI source = expanded.toURI();
            mm.addModule(source);
          } catch (Exception e2) {
            System.out.println(e2.toString());
          }
        }
      }

      String basedir = System.getProperty(
          "uk.org.onegch.netkernel.testNG.modules.basedir", "");
      String modules = System
          .getProperty("uk.org.onegch.netkernel.testNG.modules");

      if (modules != null) {
        String[] moduleStr = modules.split(",");
        for (int i = 0; i < moduleStr.length; i++) {
          mm.addModule(new File(basedir + moduleStr[i]).toURI());
        }
      }
    } catch (Throwable e) {
      System.out.println(e.toString());
      if (!(e instanceof INetKernelThrowable)) {
        e.printStackTrace();
        stop();
      }
    }
  }

  public static File expandJar(InputStream aIs, String aName, File aExpandDir)
      throws IOException {
    String suffix = ".jar";
    if (aName.endsWith(".sjar")) {
      suffix = ".sjar";
    }

    String prefix = aName.substring(0, aName.length() - suffix.length());
    StringBuffer sb = new StringBuffer(prefix.length());
    for (int n = 0; n < prefix.length(); n++) {
      char c = prefix.charAt(n);
      if (Character.isLetterOrDigit(c) || c == '-' || c == '.') {
        sb.append(c);
      } else {
        sb.append('_');
      }
    }
    sb.append("_");
    prefix = sb.toString();

    File dest;
    if (aExpandDir != null) {
      dest = new File(aExpandDir, prefix);
    } else {
      dest = File.createTempFile(EXPAND_PREFIX + prefix, suffix);
      dest.deleteOnExit();
    }

    // TODO: need to only copy if dest doesn't exist or is stale
    System.out.println("Expanding " + aName); // + " to " +
                                              // dest.getAbsolutePath());
    FileOutputStream fos = new FileOutputStream(dest);
    pipe(aIs, fos);
    return dest;
  }

  /**
   * copy an input stream to an outputsteam and close streams when finished
   * 
   * @throws IOException
   *           if there are any problems
   */
  public static void pipe(InputStream aInput, OutputStream aOutput)
      throws IOException {
    byte b[] = new byte[256];
    int c;
    try {
      while ((c = aInput.read(b)) > 0) {
        aOutput.write(b, 0, c);
      }
    } finally {
      try {
        aInput.close();
      } finally {
        aOutput.close();
      }
    }
  }

  public void start() {
    mm.setRunLevel(ModuleManager.DEFAULT_RUN_LEVEL);
  }

  public void stop() {
    mm.stop();
    cache.stop();
  }
}