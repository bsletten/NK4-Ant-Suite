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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.ZipFileSet;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class NetKernelPackageTask extends Zip {
  private List<ModuleDetails> moduleDetailsList = new ArrayList<ModuleDetails>();

  private File manifestXmlFile;
  private File modulesXmlFile;

  private String version = "0.0.0";
  private String name = "";
  private String description = "";

  public NetKernelPackageTask() {
    setFilesonly(true);
  }

  @Override
  public void execute() throws BuildException {
    try {
      buildManifestXml();
      buildModulesXml();
    } catch (Exception e) {
      throw new BuildException(e);
    }
    super.execute();
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void addConfiguredModulesFileSet(ZipFileSet fs) throws Exception {
    DirectoryScanner ds = fs.getDirectoryScanner(getProject());

    for(String file : ds.getIncludedFiles()) {
      JarInputStream jis = new JarInputStream(new FileInputStream(new File(fs.getDir(getProject()), file)));

      ZipEntry entry;
      while ((entry = jis.getNextEntry()) != null) {
        if (entry.getName().equals("module.xml")) {
          ModuleDetails moduleDetails = new ModuleDetails(jis);
          moduleDetailsList.add(moduleDetails);
          break;
        }
      }
    }

    addFileset(fs);
  }

  private void buildManifestXml() throws Exception {
    if (manifestXmlFile == null) {
      ZipFileSet zipFileSet = new ZipFileSet();

      File tempDir = new File(System.getProperty("java.io.tmpdir"));
      manifestXmlFile = new File(tempDir, "nk-package/manifest.xml");

      if (manifestXmlFile.exists()) {
        manifestXmlFile.delete();
      }
      manifestXmlFile.getParentFile().mkdirs();
      manifestXmlFile.createNewFile();

      zipFileSet.setDir(manifestXmlFile.getParentFile());
      zipFileSet.setIncludes(manifestXmlFile.getName());
      zipFileSet.setPrefix("");

      addFileset(zipFileSet);
    }

    Properties props= OutputPropertiesFactory.getDefaultMethodProperties("xml");
    props.setProperty("indent", "yes");
    props.setProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT , "2");
    props.setProperty("encoding", "UTF-8");

    Serializer s = SerializerFactory.getSerializer(props);
    s.setWriter(new FileWriter(manifestXmlFile));

    ContentHandler rch = s.asContentHandler();

    rch.startDocument();

    rch.startElement("", "manifest", "manifest", new AttributesImpl());

    rch.startElement("", "name", "name", new AttributesImpl());
    rch.characters(name.toCharArray(), 0, name.length());
    rch.endElement("", "name", "name");

    rch.startElement("", "description", "description", new AttributesImpl());
    rch.characters(description.toCharArray(), 0, description.length());
    rch.endElement("", "description", "description");

    rch.startElement("", "version", "version", new AttributesImpl());
    rch.characters(version.toCharArray(), 0, version.length());
    rch.endElement("", "version", "version");

    for (ModuleDetails moduleDetails : moduleDetailsList) {
      moduleDetails.toXml(rch);
    }

    rch.endElement("", "manifest", "manifest");

    rch.endDocument();
  }

  private void buildModulesXml() throws Exception {
    if (modulesXmlFile == null) {
      ZipFileSet zipFileSet = new ZipFileSet();

      File tempDir = new File(System.getProperty("java.io.tmpdir"));
      modulesXmlFile = new File(tempDir, "nk-package/module.xml");

      if (modulesXmlFile.exists()) {
        modulesXmlFile.delete();
      }
      modulesXmlFile.getParentFile().mkdirs();
      modulesXmlFile.createNewFile();

      zipFileSet.setDir(modulesXmlFile.getParentFile());
      zipFileSet.setIncludes(modulesXmlFile.getName());
      zipFileSet.setPrefix("");

      addFileset(zipFileSet);
    }

    Processor p = new Processor(false);

    DocumentBuilder documentBuilder = p.newDocumentBuilder();

    XdmNode modulesDoc = documentBuilder.build(new StreamSource(this.getClass().getResourceAsStream("/module.xml")));
    XdmNode processModulesDoc = documentBuilder.build(new StreamSource(this.getClass().getResourceAsStream("/processModule.xsl")));

    net.sf.saxon.s9api.Serializer serializer = new net.sf.saxon.s9api.Serializer();
    serializer.setOutputFile(modulesXmlFile);

    XsltTransformer processModulesXslt = p.newXsltCompiler().compile(processModulesDoc.asSource()).load();
    processModulesXslt.setInitialContextNode(modulesDoc);
    processModulesXslt.setDestination(serializer);
    processModulesXslt.setParameter(new QName("uri"), new XdmAtomicValue("urn:ant:created:package:" + name));
    processModulesXslt.setParameter(new QName("version"), new XdmAtomicValue(version));
    processModulesXslt.setParameter(new QName("name"), new XdmAtomicValue(name));
    processModulesXslt.setParameter(new QName("description"), new XdmAtomicValue(description));
    processModulesXslt.transform();

  }
}
