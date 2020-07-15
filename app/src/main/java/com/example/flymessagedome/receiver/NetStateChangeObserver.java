package com.example.flymessagedome.receiver;

import com.example.flymessagedome.utils.NetworkType;

public interface NetStateChangeObserver {
    void onNetDisconnected();
    void onNetConnected(NetworkType networkType);
}