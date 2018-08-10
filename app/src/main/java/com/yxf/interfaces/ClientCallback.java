package com.yxf.interfaces;

import com.yxf.socketframe.annotation.SocketInterface;

import java.util.Map;

@SocketInterface
public interface ClientCallback {

    void reply(String string);

}
