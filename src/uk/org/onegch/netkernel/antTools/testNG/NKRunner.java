package uk.org.onegch.netkernel.antTools.testNG;

import java.io.File;
import java.io.FilenameFilter;

public class NKRunner {

  private NKBoot innerBoot;

  public NKRunner() {
    try {
      String classpath = System.getProperty("java.class.path");

      int j = classpath.indexOf(File.pathSeparatorChar);
      if (j > 0) {
        classpath = classpath.substring(0, j);
      }

      String libPrefix = "lib/";

      File expandDir = null;
      String expandDirString = System.getProperty("netkernel.expand.dir");
      if (expandDirString != null) {
        expandDir = new File(expandDirString);
      }
      
      // delete temp files on next instantiation - they are locked when we exit
      // due to classloaders :-(
      if (expandDir == null) {
        String tempPath = System.getProperty("java.io.tmpdir");
        File tempDir = new File(tempPath);
        File[] files = tempDir.listFiles(new FilenameFilter() {
          public boolean accept(File aDir, String aName) {
            return aName.startsWith(NKBoot.EXPAND_PREFIX);
          }
        });
        for (int i = 0; i < files.length; i++) {
          files[i].delete();
        }
      }
      
      // URLClassLoader ucl = new
      // BootClassLoader(urls,Thread.currentThread().getContextClassLoader().getParent());
      ClassLoader cl = this.getClass().getClassLoader();
      org.ten60.netkernel.protocolhandler.nk.Handler.init(cl);
      org.ten60.netkernel.protocolhandler.sjar.Handler.init(cl);

      innerBoot = new NKBoot(new File(this.getClass().getProtectionDomain()
          .getCodeSource().getLocation().getPath()).getParentFile().toURI()
          .toString());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  public void start() {
    try {
      innerBoot.start();
      Thread.sleep(2000);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    try {
      Thread.sleep(1000);
      innerBoot.stop();
      Thread.sleep(3000);
    } catch (InterruptedException e) {
    }
  }
}
