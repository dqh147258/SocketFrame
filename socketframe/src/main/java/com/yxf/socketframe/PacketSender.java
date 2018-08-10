package com.yxf.socketframe;

import com.yxf.socketframe.packet.Packet;

public interface PacketSender {

    boolean sendPacketById(String id, Packet packet);
}
