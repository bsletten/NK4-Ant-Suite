package uk.org.onegch.netkernel.testNG;

import net.sf.saxon.s9api.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadFromRepository {
  private static final int BUFFER = 2048;

  public static void main(String[] args) throws Exception {
    File packagesFolder = new File("build/packages/");
    if (!packagesFolder.exists()) {
      packagesFolder.mkdirs();
    }

    Processor p= new Processor(false);

    DocumentBuilder documentBuilder = p.newDocumentBuilder();
    XdmNode repositoryDoc = documentBuilder.build(new File("repository.xml"));
    XdmNode processRepositoryDoc = documentBuilder.build(new File("processRepository.xsl"));

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


      File targetFolder = new File("build/" + target + "/");
      if (!targetFolder.exists()) {
        targetFolder.mkdirs();
      }

      copyModules(targetFolder, expandedPackageFolder);
    }
  }

  private static void writeModuleList(List<String> moduleList, File moduleListFile) throws Exception {
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(moduleListFile));
    for (String s : moduleList) {
      bufferedWriter.append("/lib/" + s);
      bufferedWriter.newLine();
    }
    bufferedWriter.close();
  }

  private static void copyModules(File modulesFolder, File expandedPackageFolder) {
    File packageModulesFolder= new File(expandedPackageFolder, "modules/");
    for(File module : packageModulesFolder.listFiles()) {
      String moduleName= module.getName();
      System.err.println(" * moving module " + moduleName);
      module.renameTo(new File(modulesFolder, moduleName));
    }
  }

  private static void expandPackage(File packageFile, File expandedPackageFolder) throws Exception {
    BufferedOutputStream out = null;
    ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(packageFile)));
    ZipEntry entry;
    while ((entry = in.getNextEntry()) != null) {
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
    in.close();
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
