package com.yxf.socketframe;

public interface ConnectionStateChangedListener {

    int CONNECTION_STATE_DISCONNECTED = 0;
    int CONNECTION_STATE_CONNECT_SUCCESSFULLY = CONNECTION_STATE_DISCONNECTED + 1;
    int CONNECTION_STATE_CONNECT_FAILED = CONNECTION_STATE_CONNECT_SUCCESSFULLY + 1;
    int CONNECTION_STATE_CONNECT_CLOSED = CONNECTION_STATE_CONNECT_FAILED + 1;


    void onDisconnected(String id);

    void onConnectSuccessfully(String id);

    void onConnectFailed(String id);

    void onConnectionClosed(String id);

}
