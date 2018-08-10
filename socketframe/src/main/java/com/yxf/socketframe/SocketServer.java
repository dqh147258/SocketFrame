package com.yxf.socketframe;

import com.yxf.socketframe.handler.InvalidPacketHandler;
import com.yxf.socketframe.handler.PacketHandler;
import com.yxf.socketframe.heartbeat.PermissionCheck;
import com.yxf.socketframe.heartbeat.SimplePermissionCheck;
import com.yxf.socketframe.packet.Packet;
import com.yxf.socketframe.packet.WrapPacket;
import com.yxf.socketframe.transport.NettyAsyncTcpServerTransport;
import com.yxf.socketframe.transport.ServerTransport;
import com.yxf.socketframe.transport.Transport;
import com.yxf.socketframe.util.Log;
import com.yxf.socketframe.util.SocketUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketServer extends SocketBase implements ServerTransport.ServerTransportCallback, Transport.TransportCallback {

    private ServerTransport mServerTransport;

    private Map<String, Transport> mTransportMap = new ConcurrentHashMap<String, Transport>();

    private Map<String, Object> mTransportPermissionCheckMap = new ConcurrentHashMap<String, Object>();

    private PacketHandler mPacketHandler;

    private PermissionCheck mPermissionCheck;

    private SocketServer(Builder builder) {
        this.mServerTransport = builder.mServerTransport;
        mServerTransport.setServerTransportCallback(this);
        this.mPacketHandler = builder.mPacketHandler;
        mPacketHandler.setPacketSender(this);
        mPermissionCheck = builder.mPermissionCheck;


    }

    @Override
    public void start() {
        mServerTransport.start();
        mPacketHandler.start();
    }

    @Override
    public void stop() {
        mServerTransport.stop();
        mPacketHandler.stop();
    }

    @Override
    protected void parseNewPacket(String id, byte[] data) {
        Packet packet = mPacketHandler.generateEmptyPacket();
        packet.initialize(data, 0, data.length);
        mPacketHandler.handlePacket(id, packet);
    }

    @Override
    protected void checkHeartBeat(String id, byte[] data) {
        Object lastCheck = mTransportPermissionCheckMap.get(id);
        Object currentCheck = mPermissionCheck.decode(data);
        if (currentCheck != null) {
            if (mPermissionCheck.equals(mPermissionCheck.change(lastCheck), currentCheck)) {
                Log.d("transport : " + id + "check permission successfully");

            }
        }
    }

    @Override
    public void onTransportAccept(Transport transport) {
        mTransportMap.put(transport.getId(), transport);
        transport.setTransportCallback(this);
    }

    @Override
    public void onNewPacketReceived(Transport transport, WrapPacket packet) {
        handleReceivedPacket(transport.getId(), packet);
    }

    @Override
    public boolean sendPacketById(String id, Packet packet) {
        Transport transport = mTransportMap.get(id);
        if (transport == null) {
            return false;
        }
        return SocketUtils.sendPacket(packet, transport);
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

        private ServerTransport mServerTransport;
        private PacketHandler mPacketHandler;
        private PermissionCheck mPermissionCheck;

        public Builder(int port) {
            if (port <= 0) {
                throw new IllegalArgumentException("the port can not less than 1");
            }
            mServerTransport = new NettyAsyncTcpServerTransport(port);
        }

        public Builder(ServerTransport transport) {
            if (transport == null) {
                throw new NullPointerException("the transport can not be a null value");
            }
            mServerTransport = transport;
        }

        public SocketServer create() {
            if (mPermissionCheck == null) {
                mPermissionCheck = new SimplePermissionCheck();
            }
            if (mPacketHandler == null) {
                mPacketHandler = new InvalidPacketHandler();
            }
            return new SocketServer(this);
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
