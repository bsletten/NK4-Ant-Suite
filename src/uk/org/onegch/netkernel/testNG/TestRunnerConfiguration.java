package uk.org.onegch.netkernel.testNG;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.netkernel.container.config.IConfiguration;
import org.netkernel.urii.INetKernelException;
import org.netkernel.urii.impl.NetKernelException;

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
