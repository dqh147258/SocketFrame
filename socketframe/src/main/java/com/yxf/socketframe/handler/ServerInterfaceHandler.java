package com.yxf.socketframe.handler;

import com.yxf.socketframe.packet.InterfacePacket;

public class ServerInterfaceHandler<Interface, Callback> extends InterfaceHandler {

    private Interface mInterface;
    private Interface mInterfaceDecoder;

    private Class<Callback> mCallbackClass;
    private Callback mCallbackEncoder;

    public ServerInterfaceHandler(Class<Callback> cla, Interface in) {
        mCallbackClass = cla;
        mInterface = in;
        if (mInterface == null) {
            throw new NullPointerException("interface can not be a null value");
        }
        if (cla != null) {
            initCallbackEncoder();
        }
        initInterfaceDecoder();

    }

    @Override
    public void handlePacket(String id, InterfacePacket packet) {
        super.handlePacket(id, packet);
        ((Decoder) mInterfaceDecoder).dispatchInterfacePacket(packet, mInterface);
    }

    private void initInterfaceDecoder() {
        Class<Interface> interfaceClass = getSocketInterfaceAnnotationAttachedClass(mInterface);
        if (interfaceClass == null) {
            throw new IllegalArgumentException("the interface must attached with @SocketInterface");
        }
        String path = interfaceClass.getCanonicalName() + DECODER_POSTFIX;
        mInterfaceDecoder = (Interface) getNewInstanceByClassPath(path);

    }


    private void initCallbackEncoder() {
        String path = mCallbackClass.getCanonicalName() + ENCODER_POSTFIX;
        mCallbackEncoder = (Callback) getNewInstanceByClassPath(path);
        ((Encoder) mCallbackEncoder).setInterfaceHandler(this);
    }

    public Callback getCallback() {
        return mCallbackEncoder;
    }


    @Override
    public void onConnectSuccessfully(String id) {
        super.onConnectSuccessfully(id);
    }

}
