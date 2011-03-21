package uk.org.onegch.netkernel.antTools.downloader;

import net.sf.saxon.s9api.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DownloadNetKernel {
  private static final int BUFFER = 2048;

  private File baseDir;
  private File path;
  private File repositoryConfig;
  private BufferedWriter modulesConfWriter;

  public DownloadNetKernel(File baseDir, File path, File repositoryConfig) {
    this.baseDir = baseDir;
    this.path = path;
    this.repositoryConfig = repositoryConfig;
  }

  public void download() throws Exception {
    if (!path.exists()) {
      path.mkdirs();
    }

    File packagesFolder = new File(path, "packages/");
    if (!packagesFolder.exists()) {
      packagesFolder.mkdirs();
    }

    modulesConfWriter = new BufferedWriter(new FileWriter(new File(path, "modules.conf")));

    Processor p= new Processor(false);

    DocumentBuilder documentBuilder = p.newDocumentBuilder();
    XdmNode repositoryDoc;
    if (repositoryConfig == null) {
      repositoryDoc = documentBuilder.build(new StreamSource(DownloadNetKernelAntTask.class.getResourceAsStream("/repository.xml")));
    } else {
      repositoryDoc = documentBuilder.build(repositoryConfig);
    }
    XdmNode processRepositoryDoc = documentBuilder.build(new StreamSource(DownloadNetKernelAntTask.class.getResourceAsStream("/processRepository.xsl")));

    XdmDestination repositories = new XdmDestination();

    XsltTransformer processRepositoryXslt = p.newXsltCompiler().compile(processRepositoryDoc.asSource()).load();
    processRepositoryXslt.setInitialContextNode(repositoryDoc);
    processRepositoryXslt.setDestination(repositories);
    processRepositoryXslt.transform();

    XPathCompiler xPathCompiler = p.newXPathCompiler();
    XPathSelector packageSelector = xPathCompiler.compile("//package").load();
    XPathSelector urlSelector = xPathCompiler.compile("xs:string(url)").load();
    XPathSelector nameSelector = xPathCompiler.compile("xs:string(name)").load();
    XPathSelector targetSelector = xPathCompiler.compile("xs:string(if (@target) then @target else 'modules')").load();
    XPathSelector versionSelector = xPathCompiler.compile("xs:string(version/string)").load();

    packageSelector.setContextItem(repositories.getXdmNode());
    for (XdmItem item : packageSelector.evaluate()) {
      urlSelector.setContextItem(item);
      nameSelector.setContextItem(item);
      versionSelector.setContextItem(item);
      targetSelector.setContextItem(item);

      String url = urlSelector.evaluateSingle().getStringValue();
      String name = nameSelector.evaluateSingle().getStringValue();
      String version = versionSelector.evaluateSingle().getStringValue();
      String target = targetSelector.evaluateSingle().getStringValue();

      File packageFolder= new File(packagesFolder, name + "-" + version + "/");
      if (!packageFolder.exists()) {
        packageFolder.mkdirs();
      }

      System.err.println("Processing package " + name + "-" + version);
      File packageFile= new File(packageFolder, "download.nkp.jar");
      if (packageFile.exists()) {
        System.err.println(" * skipping download - existing download found");
      } else {
        System.err.println(" * downloading started");
        downloadPackage(url, packageFile);
        System.err.println(" * downloading finished");
      }

      File expandedPackageFolder= new File(packageFolder, "expanded/");
      if (expandedPackageFolder.exists()) {
        deleteDir(expandedPackageFolder);
      }

      System.err.println(" * expanding download");
      expandPackage(packageFile, expandedPackageFolder);

      File targetFolder = new File(path, target + "/");
      if (!targetFolder.exists()) {
        targetFolder.mkdirs();
      }

      copyModules(targetFolder, expandedPackageFolder, (target.equals("lib")));
    }
    modulesConfWriter.close();
  }

  private void copyModules(File modulesFolder, File expandedPackageFolder, boolean isLibrary) throws Exception {
    File packageModulesFolder= new File(expandedPackageFolder, "modules/");
    for(File module : packageModulesFolder.listFiles()) {
      String moduleName= module.getName();

      File expandedModuleFolder= new File(expandedPackageFolder, "modules/expanded/" + moduleName);
      if (!expandedModuleFolder.exists()) {
        expandedModuleFolder.mkdirs();
      }
      File logConfigFile= expandModule(module, expandedModuleFolder);

      File moduleFile= new File(modulesFolder, moduleName);

      if (logConfigFile != null) {
        processLogConfigFile(logConfigFile);
        System.err.println(" * processing LogConfig.xml in module " + moduleName);
        zipDir(moduleFile, expandedModuleFolder, expandedModuleFolder.getPath() + "/");
      } else {
        System.err.println(" * moving module " + moduleName);
        module.renameTo(moduleFile);
      }

      if (!isLibrary) {
        String modulePath= moduleFile.getAbsolutePath().substring(baseDir.getAbsolutePath().length() + 1);
        modulesConfWriter.append(modulePath);
        modulesConfWriter.newLine();
      }
    }
  }

  private static void zipDir(File zipFile, File dir, String stripPrefix) throws Exception {
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
    addDir(dir, out, stripPrefix);
    out.close();
  }

  static void addDir(File dirObj, ZipOutputStream out, String stripPrefix) throws IOException {
    File[] files = dirObj.listFiles();
    byte[] tmpBuf = new byte[1024];

    for (int i = 0; i < files.length; i++) {
      String filePath= files[i].getPath().substring(stripPrefix.length());

      if (files[i].isDirectory()) {
        ZipEntry entry= new ZipEntry(filePath + "/");
        out.putNextEntry(entry);
        addDir(files[i], out, stripPrefix);
      } else {
        FileInputStream in = new FileInputStream(files[i]);
        out.putNextEntry(new ZipEntry(filePath));
        int len;
        while ((len = in.read(tmpBuf)) > 0) {
          out.write(tmpBuf, 0, len);
        }
        out.closeEntry();
        in.close();
      }
    }
  }

  private static void processLogConfigFile(File logConfigFile) throws Exception {
    Processor p= new Processor(false);

    DocumentBuilder documentBuilder = p.newDocumentBuilder();
    XdmNode logConfigDoc = documentBuilder.build(logConfigFile);
    XdmNode processLogConfigDoc = documentBuilder.build(new StreamSource(DownloadNetKernelAntTask.class.getResourceAsStream("/processLogConfig.xsl")));

    Serializer destination = new Serializer();
    destination.setOutputFile(logConfigFile);

    XsltTransformer processLogConfigXslt = p.newXsltCompiler().compile(processLogConfigDoc.asSource()).load();
    processLogConfigXslt.setInitialContextNode(logConfigDoc);
    processLogConfigXslt.setDestination(destination);
    processLogConfigXslt.transform();
  }

  private static void expandPackage(File packageFile, File expandedPackageFolder) throws Exception {
    BufferedOutputStream out = null;
    ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(packageFile)));
    ZipEntry entry;
    while ((entry = in.getNextEntry()) != null) {
      if (entry.isDirectory()) {
        File targetFile= new File(expandedPackageFolder.getPath() + "/" + entry.getName());
        targetFile.mkdirs();
      } else {
        int count;
        byte data[] = new byte[BUFFER];

        // write the files to the disk
        File targetFile= new File(expandedPackageFolder.getPath() + "/" + entry.getName());
        if (!targetFile.getParentFile().exists()) {
          targetFile.getParentFile().mkdirs();
        }
        out = new BufferedOutputStream(new FileOutputStream(targetFile), BUFFER);

        while ((count = in.read(data, 0, BUFFER)) != -1) {
          out.write(data, 0, count);
        }
        out.flush();
        out.close();
      }
    }
    in.close();
  }

  private static File expandModule(File moduleFile, File expandedModuleFolder) throws Exception {
    File logConfigFile= null;
    BufferedOutputStream out = null;
    ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(moduleFile)));
    ZipEntry entry;
    while ((entry = in.getNextEntry()) != null) {
      if (entry.isDirectory()) {
        File targetFile= new File(expandedModuleFolder.getPath() + "/" + entry.getName());
        targetFile.mkdirs();
      } else {
        int count;
        byte data[] = new byte[BUFFER];

        // write the files to the disk
        File targetFile= new File(expandedModuleFolder.getPath() + "/" + entry.getName());

        if (entry.getName().endsWith("LogConfig.xml")) {
          logConfigFile = targetFile;
        }

        out = new BufferedOutputStream(new FileOutputStream(targetFile), BUFFER);

        while ((count = in.read(data, 0, BUFFER)) != -1) {
          out.write(data, 0, count);
        }
        out.flush();
        out.close();
      }
    }
    in.close();
    return logConfigFile;
  }

  private static void downloadPackage(String url, File packageFile) throws Exception {
    HttpClient httpclient= new DefaultHttpClient();
    HttpGet httpget = new HttpGet(url);

    HttpResponse response = httpclient.execute(httpget);
    int statusCode = response.getStatusLine().getStatusCode();

    if (statusCode == 200) {
      byte[] bytes = EntityUtils.toByteArray(response.getEntity());
      FileOutputStream fos = new FileOutputStream(packageFile);
      fos.write(bytes);
      fos.close();
    } else {
      throw new Exception("Non-200 status code");
    }
  }

  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    // The directory is now empty so delete it
    return dir.delete();
  }
}
