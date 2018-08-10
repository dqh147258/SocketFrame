package com.yxf.socketframe.handler;

import com.yxf.socketframe.ConnectionStateChangedListener;
import com.yxf.socketframe.Lifecycle;
import com.yxf.socketframe.PacketSender;
import com.yxf.socketframe.packet.Packet;

public interface PacketHandler<P extends Packet> extends Lifecycle, ConnectionStateChangedListener {

    void setPacketSender(PacketSender packetSender);

    P generateEmptyPacket();

    void handlePacket(String id, P packet);

}
