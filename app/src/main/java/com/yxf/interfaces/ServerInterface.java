package com.yxf.interfaces;

import com.yxf.socketframe.annotation.SocketInterface;
import com.yxf.socketframe.packet.Packet;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SocketInterface
public interface ServerInterface {

    void helloWorld(int time);
}
