package org.netkernelroc.antTools.testNG;

import org.testng.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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