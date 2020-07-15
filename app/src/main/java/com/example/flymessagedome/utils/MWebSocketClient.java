package com.example.flymessagedome.utils;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MWebSocketClient extends WebSocketClient {
    public MWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("MWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.e("MWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("MWebSocketClient", "onClose():"+code);
    }

    @Override
    public void onError(Exception ex) {
        Log.e("MWebSocketClient", "onError()");
    }
}