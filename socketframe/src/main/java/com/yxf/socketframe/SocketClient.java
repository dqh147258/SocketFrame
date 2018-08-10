package com.yxf.socketframe;

import com.yxf.socketframe.handler.InvalidPacketHandler;
import com.yxf.socketframe.handler.PacketHandler;
import com.yxf.socketframe.heartbeat.PermissionCheck;
import com.yxf.socketframe.heartbeat.SimplePermissionCheck;
import com.yxf.socketframe.packet.Packet;
import com.yxf.socketframe.packet.WrapPacket;
import com.yxf.socketframe.transport.NettyAsyncTcpTransport;
import com.yxf.socketframe.transport.Transport;
import com.yxf.socketframe.util.SocketUtils;

public class SocketClient extends SocketBase implements Transport.TransportCallback {

    private Transport mTransport;
    private PacketHandler mPacketHandler;
    private PermissionCheck mPermissionCheck;


    private SocketClient(Builder builder) {
        mTransport = builder.mTransport;
        mTransport.setTransportCallback(this);
        mPacketHandler = builder.mPacketHandler;
        mPacketHandler.setPacketSender(this);
        mPermissionCheck = builder.mPermissionCheck;
    }


    @Override
    public void start() {
        mTransport.start();
        mPacketHandler.start();
    }

    @Override
    public void stop() {
        mTransport.stop();
        mPacketHandler.stop();
    }

    @Override
    public boolean sendPacketById(String id, Packet packet) {
        return SocketUtils.sendPacket(packet, mTransport);
    }

    @Override
    public void onNewPacketReceived(Transport transport, WrapPacket packet) {
        handleReceivedPacket(transport.getId(), packet);
    }


    @Override
    protected void parseNewPacket(String id, byte[] data) {
        Packet packet = mPacketHandler.generateEmptyPacket();
        packet.initialize(data, 0, data.length);
        mPacketHandler.handlePacket(id, packet);
    }

    @Override
    protected void checkHeartBeat(String id, byte[] data) {

    }

    @Override
    public void onDisconnected(String id) {
        mPacketHandler.onDisconnected(id);
    }

    @Override
    public void onConnectSuccessfully(String id) {
        mPacketHandler.onConnectSuccessfully(id);
    }

    @Override
    public void onConnectFailed(String id) {
        mPacketHandler.onConnectFailed(id);
    }

    @Override
    public void onConnectionClosed(String id) {
        mPacketHandler.onConnectionClosed(id);
    }

    public static class Builder {

        private Transport mTransport;

        private PacketHandler mPacketHandler;
        private PermissionCheck mPermissionCheck;

        public Builder(String address, int port) {
            if (address == null || address.length() < 1) {
                throw new NullPointerException("the address can not be a null or empty value");
            }
            if (port <= 0) {
                throw new IllegalArgumentException("the port can not less than 1");
            }
            mTransport = new NettyAsyncTcpTransport(address, port);
        }

        public Builder(Transport transport) {
            if (transport == null) {
                throw new NullPointerException("the transport can not be a null value");
            }
            mTransport = transport;
        }

        public SocketClient create() {
            if (mPermissionCheck == null) {
                mPermissionCheck = new SimplePermissionCheck();
            }
            if (mPacketHandler == null) {
                mPacketHandler = new InvalidPacketHandler();
            }
            return new SocketClient(this);
        }

        public Builder setPacketHandler(PacketHandler packetHandler) {
            mPacketHandler = packetHandler;
            return this;
        }

        public Builder setPermissionCheck(PermissionCheck permissionCheck) {
            mPermissionCheck = permissionCheck;
            return this;
        }
    }
}
