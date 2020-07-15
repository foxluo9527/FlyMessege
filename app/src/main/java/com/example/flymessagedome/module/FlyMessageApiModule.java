package com.example.flymessagedome.module;


import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.api.support.Logger;
import com.example.flymessagedome.api.support.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class FlyMessageApiModule {

    @Provides
    public OkHttpClient provideOkHttpClient(){
        LoggingInterceptor interceptor = new LoggingInterceptor(new Logger());
        interceptor.setLevel(LoggingInterceptor.Level.HEADERS);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(20*1000,TimeUnit.MILLISECONDS)
                .readTimeout(20*1000,TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)// 失败重发
                .addInterceptor(interceptor);

        return builder.build();
    }

    @Provides
    protected FlyMessageApi provideFlyMessageService(OkHttpClient okHttpClient){
        return FlyMessageApi.getInstance(okHttpClient);
    }


}
