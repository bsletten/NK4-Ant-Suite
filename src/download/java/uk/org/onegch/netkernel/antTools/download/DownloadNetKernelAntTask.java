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

package uk.org.onegch.netkernel.antTools.download;

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
