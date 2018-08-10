package com.yxf.socketframe.util;

import com.yxf.socketframe.ConnectionStateChangedListener;
import com.yxf.socketframe.DataBuffer;
import com.yxf.socketframe.packet.Packet;
import com.yxf.socketframe.packet.WrapPacket;
import com.yxf.socketframe.transport.Transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import static com.yxf.socketframe.ConnectionStateChangedListener.*;

public class SocketUtils {


    private static ThreadLocal<String> sTransportId = new ThreadLocal<String>();

    public static boolean writeWrapPacket(WrapPacket packet, Channel channel) {
        DataBuffer dataBuffer = new DataBuffer();
        byte[] bytes = packet.toBytes();
        dataBuffer.writeInt(bytes.length);
        dataBuffer.writeBytes(bytes);
        if (channel.isWritable()) {
            channel.writeAndFlush(dataBuffer.getOriginalBuffer());
            return true;
        }
        Log.d("channel can not write");
        return false;
    }

    public static WrapPacket decodeWrapPacketFromByteBufAfterSplit(ByteBuf byteBuf) {
        ByteBuf buffer = byteBuf;
        DataBuffer dataBuffer = new DataBuffer(buffer);
        WrapPacket packet = new WrapPacket();
        byte[] bytes = dataBuffer.readBytes(dataBuffer.readableBytes());
        packet.initialize(bytes, 0, bytes.length);
        return packet;
    }

    public static boolean sendPacket(Packet packet, Transport transport) {
        WrapPacket wrapPacket = new WrapPacket();
        wrapPacket.setPacket(packet);
        return transport.write(wrapPacket);
    }

    public static void dispatchConnectionStateChanged(ConnectionStateChangedListener listener, int state, String transportId) {
        switch (state) {
            case CONNECTION_STATE_DISCONNECTED:
                listener.onDisconnected(transportId);
                break;
            case CONNECTION_STATE_CONNECT_SUCCESSFULLY:
                listener.onConnectSuccessfully(transportId);
                break;
            case CONNECTION_STATE_CONNECT_FAILED:
                listener.onConnectFailed(transportId);
                break;
            case CONNECTION_STATE_CONNECT_CLOSED:
                listener.onConnectionClosed(transportId);
                break;
        }
    }

    public static void setId(String id) {
        sTransportId.set(id);
    }

    public static String getId() {
        return sTransportId.get();
    }

}
