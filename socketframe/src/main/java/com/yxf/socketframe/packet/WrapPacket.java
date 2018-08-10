package com.yxf.socketframe.packet;

import com.yxf.socketframe.Profile;
import com.yxf.socketframe.util.Log;

import static com.yxf.socketframe.Profile.VERSION_FIRST;
import static com.yxf.socketframe.packet.WrapPacket.FirstVersionData.DATA_TYPE_HEART_BEAT;
import static com.yxf.socketframe.packet.WrapPacket.FirstVersionData.DATA_TYPE_NEW_PACKET;

public class WrapPacket implements Packet {

    private byte[] mDivide;// 4 byte
    private int mVersion;// 1 byte
    private Data mData;

    public WrapPacket() {
        mVersion = Profile.getInstance().getVersion();
        mDivide = Profile.getInstance().getDivide();
        mData = getDataByVersion(mVersion);
    }

    @Override
    public void initialize(byte[] bytes, int start, int end) {
        System.arraycopy(bytes, start, mDivide, 0, 4);
        start += 4;
        mVersion = bytes[start++] & 0xff;
        if (mData == null) {
            mData = getDataByVersion(mVersion);
        }
        mData.initialize(bytes, start, end);
    }

    private Data getDataByVersion(int version) {
        switch (version) {
            case VERSION_FIRST:
                return new FirstVersionData();
            default:
                Log.d("unsupported version : " + version);
                break;
        }
        return new Data();
    }

    @Override
    public byte[] toBytes() {
        int len = mDivide.length + 1;
        byte[] dataBytes = mData.toBytes();
        len += dataBytes.length;
        byte[] bytes = new byte[len];
        int start = 0;
        System.arraycopy(mDivide, 0, bytes, start, mDivide.length);
        start += mDivide.length;
        bytes[start++] = (byte) (mVersion & 0xff);
        System.arraycopy(dataBytes, 0, bytes, start, dataBytes.length);
        return bytes;
    }


    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        mVersion = version;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    public void setPacket(Packet packet) {
        setDataInfo(packet.toBytes(), false);
    }

    public void setHeartBeatData(byte[] bytes) {
        setDataInfo(bytes, true);
    }

    private void setDataInfo(byte[] bytes, boolean isHeartBeat) {
        if (mData == null) {
            mData = getDataByVersion(mVersion);
        }
        switch (mVersion) {
            case VERSION_FIRST:
                FirstVersionData data = (FirstVersionData) mData;
                data.setData(bytes);
                if (isHeartBeat) {
                    data.setType(DATA_TYPE_HEART_BEAT);
                } else {
                    data.setType(DATA_TYPE_NEW_PACKET);
                }
                break;
            default:
                Log.d("setPacket : unsupported version : " + mVersion);
                break;
        }
    }

    public static class Data implements Packet {

        @Override
        public void initialize(byte[] bytes, int start, int end) {

        }

        @Override
        public byte[] toBytes() {
            return new byte[0];
        }

    }

    public static class FirstVersionData extends Data {
        public static final int DATA_TYPE_HEART_BEAT = 0;
        public static final int DATA_TYPE_NEW_PACKET = 1;
        private int mType;// 1 byte
        private byte[] data;


        @Override
        public void initialize(byte[] bytes, int start, int end) {
            mType = bytes[start++] & 0xff;
            int len = end - start;
            data = new byte[len];
            System.arraycopy(bytes, start, data, 0, len);
        }

        @Override
        public byte[] toBytes() {
            int len = 1;
            if (data != null) {
                len += data.length;
            } else {
                data = new byte[0];
            }
            byte[] bytes = new byte[len];
            int start = 0;
            bytes[start++] = (byte) (mType & 0xff);
            System.arraycopy(data, 0, bytes, 1, data.length);
            return bytes;
        }

        public int getType() {
            return mType;
        }

        public void setType(int type) {
            mType = type;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }

}
