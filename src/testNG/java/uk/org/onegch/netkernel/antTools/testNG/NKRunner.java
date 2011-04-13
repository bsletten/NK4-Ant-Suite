package uk.org.onegch.netkernel.antTools.testNG;

import java.io.File;
import java.io.FilenameFilter;

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
