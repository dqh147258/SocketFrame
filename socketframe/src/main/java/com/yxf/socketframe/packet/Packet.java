package com.yxf.socketframe.packet;

public interface Packet {

    void initialize(byte[] bytes, int start, int end);

    byte[] toBytes();

}
