package uk.org.onegch.netkernel.antTools.testNG;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.netkernel.container.config.IConfiguration;
import org.netkernel.urii.INetKernelException;
import org.netkernel.urii.impl.NetKernelException;

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

public class TestRunnerConfiguration implements IConfiguration {
  private Map mProperties = new HashMap(17);

  public boolean containsKey(String s) {
    return mProperties.containsKey(s);
  }

  public String getString(String aKey) throws INetKernelException {
    String result;
    Object object = mProperties.get(aKey);
    if (object instanceof String) {
      result = (String) object;
    } else {
      result = object.toString();
    }

    if (result == null) {
      throw new NetKernelException("Null property value for [" + aKey
          + "], requested as String");
    }

    return result;
  }

  public String getString(String aKey, String aDefault) {
    String result;
    try {
      result = getString(aKey);
    } catch (Exception e) {
      result = aDefault;
    }

    return result;
  }

  public int getInt(String aKey) throws INetKernelException {
    Integer result = null;
    Object object = mProperties.get(aKey);
    if (object instanceof Integer) {
      result = (Integer) object;
    } else if (object instanceof String) {
      try {
        result = Integer.parseInt((String) object);
      } catch (Exception e) {
        throw new NetKernelException("Error fetching property [" + aKey + "]");
      }
    }

    if (result == null) {
      throw new NetKernelException("Null property value for [" + aKey
          + "], requested as int");
    }

    return result;
  }

  public int getInt(String aKey, int aDefault) {
    Integer result;
    try {
      result = getInt(aKey);
    } catch (Exception e) {
      result = aDefault;
    }

    return result;
  }

  public long getLong(String aKey) throws INetKernelException {
    Long result = null;
    Object object = mProperties.get(aKey);
    if (object instanceof Long) {
      result = (Long) object;
    } else if (object instanceof String) {
      try {
        result = Long.parseLong((String) object);
      } catch (Exception e) {
        throw new NetKernelException("Error fetching property [" + aKey + "]");
      }
    }

    if (result == null) {
      throw new NetKernelException("Null property value for [" + aKey
          + "], requested as long");
    }

    return result;
  }

  public long getLong(String aKey, long aDefault) {
    Long result;
    try {
      result = getLong(aKey);
    } catch (Exception e) {
      result = aDefault;
    }

    return result;
  }

  public boolean getBoolean(String aKey) throws INetKernelException {
    Boolean result = null;
    Object object = mProperties.get(aKey);
    if (object instanceof Boolean) {
      result = (Boolean) object;
    } else if (object instanceof String) {
      try {
        result = Boolean.parseBoolean((String) object);
      } catch (Exception e) {
        throw new NetKernelException("Error fetching property [" + aKey + "]");
      }
    }

    if (result == null) {
      throw new NetKernelException("Null property value for [" + aKey
          + "], requested as boolean");
    }

    return result;
  }

  public boolean getBoolean(String aKey, boolean aDefault) {
    Boolean result;
    try {
      result = getBoolean(aKey);
    } catch (Exception e) {
      result = aDefault;
    }

    return result;
  }

  public Iterator getKeys() {
    return mProperties.keySet().iterator();
  }

  public void setBoolean(String aKey, Boolean aValue) {
    if (mProperties.containsKey(aKey)) {
      mProperties.remove(aKey);
    }
    mProperties.put(aKey, aValue);
  }

  public void setString(String aKey, String aValue) {
    if (mProperties.containsKey(aKey)) {
      mProperties.remove(aKey);
    }
    mProperties.put(aKey, aValue);
  }

  public void setInteger(String aKey, int aValue) {
    if (mProperties.containsKey(aKey)) {
      mProperties.remove(aKey);
    }
    mProperties.put(aKey, new Integer(aValue));
  }

  public void setLong(String aKey, long aValue) {
    if (mProperties.containsKey(aKey)) {
      mProperties.remove(aKey);
    }
    mProperties.put(aKey, new Long(aValue));
  }
}
