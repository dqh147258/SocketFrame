package com.yxf.socketframe;

import com.yxf.socketframe.packet.Packet;
import com.yxf.socketframe.packet.WrapPacket;
import com.yxf.socketframe.util.Log;

import static com.yxf.socketframe.Profile.VERSION_FIRST;
import static com.yxf.socketframe.packet.WrapPacket.FirstVersionData.DATA_TYPE_HEART_BEAT;
import static com.yxf.socketframe.packet.WrapPacket.FirstVersionData.DATA_TYPE_NEW_PACKET;

abstract class SocketBase implements Lifecycle, PacketSender {

    protected void handleReceivedPacket(String id, WrapPacket packet) {
        switch (packet.getVersion()) {
            case VERSION_FIRST:
                WrapPacket.FirstVersionData data = (WrapPacket.FirstVersionData) packet.getData();
                switch (data.getType()) {
                    case DATA_TYPE_HEART_BEAT:
                        //checkHeartBeat(id, data.getData());
                        break;
                    case DATA_TYPE_NEW_PACKET:
                        parseNewPacket(id, data.getData());
                        break;
                    default:
                        Log.d("handleReceivedPacket : unsupported data type : " + data.getType());
                        break;
                }
                break;
            default:
                Log.d("handleReceivedPacket : unsupported version : " + packet.getVersion());
                break;
        }
    }

    protected abstract void parseNewPacket(String id, byte[] data);

    protected abstract void checkHeartBeat(String id, byte[] data);


}
