package com.example.flymessagedome.component;


import android.content.Context;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.module.AppModule;
import com.example.flymessagedome.module.FlyMessageApiModule;

import dagger.Component;

@Component(modules = {AppModule.class, FlyMessageApiModule.class})
public interface AppComponent {
    Context getContext();
    FlyMessageApi getFlyMessageApi();
}
