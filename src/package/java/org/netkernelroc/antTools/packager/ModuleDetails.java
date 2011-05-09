/*
 * Copyright (c) 2010-2011 Christopher Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.netkernelroc.antTools.packager;

import com.sun.org.apache.xml.internal.serializer.*;
import com.sun.org.apache.xml.internal.serializer.Serializer;
import net.sf.saxon.s9api.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

public class ModuleDetails {
  private String uri;
  private String version;
  private int majorVersion;
  private int minorVersion;
  private int patchVersion;

  public ModuleDetails(File moduleFile) throws Exception {
    this(new FileInputStream(moduleFile));
  }

  public ModuleDetails(InputStream is) throws Exception {
    Processor p = new Processor(false);
    DocumentBuilder documentBuilder = p.newDocumentBuilder();

    XdmNode moduleConfigDoc = documentBuilder.build(new StreamSource(is));

    XPathCompiler xPathCompiler = p.newXPathCompiler();

    XPathSelector uriSelector = xPathCompiler.compile("xs:string(/module/meta/identity/uri)").load();
    uriSelector.setContextItem(moduleConfigDoc);
    uri = uriSelector.evaluateSingle().getStringValue();

    XPathSelector versionSelector = xPathCompiler.compile("xs:string(/module/meta/identity/version)").load();
    versionSelector.setContextItem(moduleConfigDoc);
    version = versionSelector.evaluateSingle().getStringValue();

    String[] versionParts = version.split("\\.");
    if (versionParts.length == 3) {
      majorVersion = Integer.parseInt(versionParts[0]);
      minorVersion = Integer.parseInt(versionParts[1]);
      patchVersion = Integer.parseInt(versionParts[2]);
    } else {
      throw new Exception("Unrecognised version: " + version);
    }
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public int getMajorVersion() {
    return majorVersion;
  }

  public void setMajorVersion(int majorVersion) {
    this.majorVersion = majorVersion;
  }

  public int getMinorVersion() {
    return minorVersion;
  }

  public void setMinorVersion(int minorVersion) {
    this.minorVersion = minorVersion;
  }

  public int getPatchVersion() {
    return patchVersion;
  }

  public void setPatchVersion(int patchVersion) {
    this.patchVersion = patchVersion;
  }

  public static String calculateModuleJarName(ModuleDetails moduleDetails) {
    String dottedIdentity = moduleDetails.uri.replaceAll(":", ".");

    return dottedIdentity + "-" + moduleDetails.version + ".jar";
  }

  @Override
  public String toString() {
    return uri + " " + majorVersion + "." + minorVersion + "." + patchVersion;
  }

  public void toXml(ContentHandler rch) throws Exception {
    rch.startElement("", "module", "module", new AttributesImpl());

    rch.startElement("", "uri", "uri", new AttributesImpl());
    rch.characters(uri.toCharArray(), 0, uri.length());
    rch.endElement("", "uri", "uri");

    rch.startElement("", "version", "version",  new AttributesImpl());
    rch.characters(version.toCharArray(), 0, version.length());
    rch.endElement("", "version", "version");

    rch.startElement("", "runlevel", "runlevel", new AttributesImpl());
    rch.characters("7".toCharArray(), 0, "7".length());
    rch.endElement("", "runlevel", "runlevel");

    rch.startElement("", "source", "source", new AttributesImpl());
    String modulePath = "modules" + File.separator + ModuleDetails.calculateModuleJarName(this);
    rch.characters(modulePath.toCharArray(), 0, modulePath.length());
    rch.endElement("", "source", "source");

    rch.startElement("", "expand", "expand", new AttributesImpl());
    rch.characters("true".toCharArray(), 0, "true".length());
    rch.endElement("", "expand", "expand");

    rch.endElement("", "module", "module");
  }
}
