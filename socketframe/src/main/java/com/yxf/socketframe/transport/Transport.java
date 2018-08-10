package com.yxf.socketframe.transport;

import com.yxf.socketframe.ConnectionStateChangedListener;
import com.yxf.socketframe.Lifecycle;
import com.yxf.socketframe.packet.WrapPacket;

public interface Transport extends Lifecycle {

    boolean write(WrapPacket packet);

    void setTransportCallback(TransportCallback callback);

    String getId();

    interface TransportCallback extends ConnectionStateChangedListener {

        void onNewPacketReceived(Transport transport, WrapPacket packet);
    }

}
