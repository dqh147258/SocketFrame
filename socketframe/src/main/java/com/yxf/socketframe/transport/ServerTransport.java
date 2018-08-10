package com.yxf.socketframe.transport;

import com.yxf.socketframe.Lifecycle;

public interface ServerTransport extends Lifecycle{

    void setServerTransportCallback(ServerTransportCallback callback);



    interface ServerTransportCallback {

        void onTransportAccept(Transport transport);
    }
}

