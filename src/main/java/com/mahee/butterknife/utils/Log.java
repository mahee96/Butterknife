package com.mahee.butterknife.utils;

import com.intellij.openapi.diagnostic.Logger;

public class Log {
    private static final String delim = ": ";
    private Logger mLogger;
    private static Log mLog;
    private Log(){}
    public static Log getInstance(Class<?> cl){
        if (mLog == null) mLog = new Log();
        mLog.mLogger = Logger.getInstance(cl);
        return mLog;
    }
    public void d(String TAG, String message){
        mLogger.debug(TAG+delim+message);
    }
    public void d(String TAG, Throwable throwable){
        mLogger.debug(TAG, throwable);
    }
    public void d(String TAG, String message, Throwable throwable){
        mLogger.debug(TAG+delim+message, throwable);
    }
    public void e(String TAG, String message){
        mLogger.error(TAG+delim+message);
    }
    public void e(String TAG, Throwable throwable){
        mLogger.error(TAG, throwable);
    }
    public void e(String TAG, String message, Throwable throwable){
        mLogger.error(TAG+delim+message, throwable);
    }
    public void i(String TAG, String message){
        mLogger.info(TAG+delim+message);
    }
    public void i(String TAG, Throwable throwable){
        mLogger.info(TAG, throwable);
    }
    public void i(String TAG, String message, Throwable throwable){
        mLogger.info(TAG+delim+message, throwable);
    }

    public boolean isDebugEnabled() {
        return mLogger.isDebugEnabled();
    }
}