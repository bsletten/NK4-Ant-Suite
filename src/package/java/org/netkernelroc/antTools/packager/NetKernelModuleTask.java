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
