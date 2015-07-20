  package com.beiang.airdog.utils.elink;
  
  public abstract interface FirstTimeConfigListener
  {
    public abstract void onFirstTimeConfigEvent(FtcEvent paramFtcEvent, Exception paramException);
  
    public static enum FtcEvent
    {
     FTC_SUCCESS, 
  
     FTC_ERROR, 
  
     FTC_TIMEOUT;
    }
  }
