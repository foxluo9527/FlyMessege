package com.example.flymessagedome.api.support;


import com.example.flymessagedome.utils.LogUtils;

/**
 * @author liying.
 * @date 2016/12/13.
 */
public class Logger implements LoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        LogUtils.i("http : " + message);
    }
}
