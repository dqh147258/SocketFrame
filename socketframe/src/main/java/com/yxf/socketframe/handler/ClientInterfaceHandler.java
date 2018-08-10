package com.yxf.socketframe.handler;


import com.yxf.socketframe.annotation.SocketInterface;
import com.yxf.socketframe.packet.InterfacePacket;

import java.lang.annotation.Annotation;

public class ClientInterfaceHandler<Interface, Callback> extends InterfaceHandler {

    private Class<Interface> mInterfaceClass;
    private Callback mCallback;
    private Callback mCallbackDecoder;

    private Interface mInterfaceEncoder;

    public ClientInterfaceHandler(Class<Interface> cla, Callback callback) {
        mInterfaceClass = cla;
        mCallback = callback;
        if (cla == null) {
            throw new NullPointerException("the class of interface can not be a null value");
        }
        initInterfaceEncoder();
        if (callback != null) {
            initCallbackDecoder();
        }
    }

    @Override
    public void handlePacket(String id, InterfacePacket packet) {
        super.handlePacket(id, packet);
        ((Decoder) mCallbackDecoder).dispatchInterfacePacket(packet, mCallback);
    }

    private void initCallbackDecoder() {
        Class<Callback> callbackClass = getSocketInterfaceAnnotationAttachedClass(mCallback);
        if (callbackClass == null) {
            throw new IllegalArgumentException("the interface of callback must attached with @SocketInterface");
        }
        String path = callbackClass.getCanonicalName() + DECODER_POSTFIX;
        mCallbackDecoder = (Callback) getNewInstanceByClassPath(path);
    }

    private void initInterfaceEncoder() {
        String path = mInterfaceClass.getCanonicalName() + ENCODER_POSTFIX;
        mInterfaceEncoder = (Interface) getNewInstanceByClassPath(path);
        ((Encoder) mInterfaceEncoder).setInterfaceHandler(this);
    }

    public Interface getInterface() {
        return mInterfaceEncoder;
    }

    @Override
    public void onConnectSuccessfully(String id) {
        super.onConnectSuccessfully(id);

    }
}
