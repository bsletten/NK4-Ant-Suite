package uk.org.onegch.netkernel.antTools.downloader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

public class DownloadNetKernelAntTask extends Task {
  private File path;
  private File config;

  public void setPath(File path) {
    this.path= path;
  }

  public void setConfig(File config) {
    this.config = config;
  }

  @Override
  public void execute() throws BuildException {
    if (path == null) {
      throw new BuildException("Path parameter must be supplied");
    }
    if (path.exists() && !path.isDirectory()) {
      throw new BuildException("Supplied path parameter is not a directory");
    }

    try {
      new DownloadNetKernel(super.getProject().getBaseDir(), path, config).download();
    } catch (Exception e) {
      throw new BuildException(e);
    }
  }
}
