package com.yxf.socketframe.handler;

import com.yxf.socketframe.PacketSender;
import com.yxf.socketframe.packet.InterfacePacket;

public class InvalidPacketHandler implements PacketHandler<InterfacePacket> {
    @Override
    public void setPacketSender(PacketSender packetSender) {

    }

    @Override
    public InterfacePacket generateEmptyPacket() {
        return null;
    }

    @Override
    public void handlePacket(String id, InterfacePacket packet) {

    }

    @Override
    public void onDisconnected(String id) {

    }

    @Override
    public void onConnectSuccessfully(String id) {

    }

    @Override
    public void onConnectFailed(String id) {

    }

    @Override
    public void onConnectionClosed(String id) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
