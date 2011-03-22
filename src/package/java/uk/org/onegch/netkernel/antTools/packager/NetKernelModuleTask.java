package uk.org.onegch.netkernel.antTools.packager;

import org.apache.tools.ant.taskdefs.Jar;

import java.io.File;

public class NetKernelModuleTask extends Jar {
  private ModuleDetails moduleDetails;
  private File destFolder;

  public void setModuleFile(File moduleFile) throws Exception {
    if (destFolder == null) {
      destFolder = getProject().getBaseDir();
    }
    moduleDetails= new ModuleDetails(moduleFile);
    setDestFile(new File(destFolder, ModuleDetails.calculateModuleJarName(moduleDetails)));
  }

  public void setDestDir(File destFolder) {
    this.destFolder = destFolder;
    if (moduleDetails != null) {
      setDestFile(new File(destFolder, ModuleDetails.calculateModuleJarName(moduleDetails)));
    }
  }
}
