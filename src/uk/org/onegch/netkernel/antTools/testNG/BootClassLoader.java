package uk.org.onegch.netkernel.antTools.testNG;
import java.net.*;

/**
 * Minimal Wrapper around URLClassLoader to add a useful toString()
 * @author  tab
 */
public class BootClassLoader extends URLClassLoader
{
	public BootClassLoader(URL[] aURLs, ClassLoader aParent)
	{	super(aURLs, aParent);
	}
	
	public String toString()
	{	StringBuilder result=new StringBuilder(512);
		result.append("BootClassLoader [");
		URL[] urls=getURLs();
		for (int i=0; i<urls.length; i++)
		{	result.append(urls[i].toExternalForm());
			if (i!=urls.length-1)
			{	result.append(' ');
			}
		}
		result.append(']');
		return result.toString();
	}
}