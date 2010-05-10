package uk.org.onegch.netkernel.ciexperiment.test;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public class Test2 extends StandardAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext) throws Exception {
    aContext.createResponseFrom(false);
  }
}
