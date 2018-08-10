package com.yxf.socketframe.handler;

import com.yxf.socketframe.PacketSender;
import com.yxf.socketframe.annotation.SocketInterface;
import com.yxf.socketframe.packet.InterfacePacket;
import com.yxf.socketframe.util.Log;
import com.yxf.socketframe.util.SocketUtils;

import java.lang.annotation.Annotation;

public class InterfaceHandler implements PacketHandler<InterfacePacket> {

    protected static final String ENCODER_POSTFIX = "_Encoder";
    protected static final String DECODER_POSTFIX = "_Decoder";

    protected PacketSender mPacketSender;

    @Override
    public void setPacketSender(PacketSender packetSender) {
        this.mPacketSender = packetSender;

    }

    @Override
    public InterfacePacket generateEmptyPacket() {
        return new InterfacePacket();
    }

    @Override
    public void handlePacket(String id, InterfacePacket packet) {
        SocketUtils.setId(id);
    }

    @Override
    public void onDisconnected(String id) {

    }

    @Override
    public void onConnectSuccessfully(String id) {
        Log.d("connect successfully : " + id);
    }

    @Override
    public void onConnectFailed(String id) {

    }

    @Override
    public void onConnectionClosed(String id) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public boolean sendPacket(String id, InterfacePacket packet) {
        return mPacketSender.sendPacketById(id, packet);
    }

    public boolean sendPacket(InterfacePacket packet) {
        return sendPacket(SocketUtils.getId(), packet);
    }

    protected Class getSocketInterfaceAnnotationAttachedClass(Object instance) {
        if (instance == null) {
            return null;
        }
        Class cla = instance.getClass();
        while (true) {
            Annotation annotation = cla.getAnnotation(SocketInterface.class);
            if (annotation != null) {
                return cla;
            }
            Class[] classes = cla.getInterfaces();
            if (classes != null) {
                for (Class c : classes) {
                    annotation = c.getAnnotation(SocketInterface.class);
                    if (annotation != null) {
                        return c;
                    }
                }
            }
            if (cla == Object.class && cla.getSuperclass() == null) {
                break;
            }
            cla = cla.getSuperclass();
        }
        return null;
    }

    protected Object getNewInstanceByClassPath(String path) {
        try {
            Class c = Class.forName(path);
            return c.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface Encoder {
        void setInterfaceHandler(InterfaceHandler handler);
    }

    public interface Decoder {
        boolean dispatchInterfacePacket(InterfacePacket packet, Object instance);
    }
}
