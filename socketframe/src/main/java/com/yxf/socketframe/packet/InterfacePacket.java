package com.yxf.socketframe.packet;

import com.sun.org.apache.bcel.internal.generic.FLOAD;
import com.yxf.socketframe.DataBuffer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class InterfacePacket implements Packet {

    private static final int TYPE_BOOLEAN = 0;
    private static final int TYPE_BYTE = TYPE_BOOLEAN + 1;
    private static final int TYPE_CHAR = TYPE_BYTE + 1;
    private static final int TYPE_SHORT = TYPE_CHAR + 1;
    private static final int TYPE_INT = TYPE_SHORT + 1;
    private static final int TYPE_LONG = TYPE_INT + 1;
    private static final int TYPE_FLOAT = TYPE_LONG + 1;
    private static final int TYPE_DOUBLE = TYPE_FLOAT + 1;
    private static final int TYPE_PACKET = TYPE_DOUBLE + 10;
    private static final int TYPE_STRING = TYPE_PACKET + 1;

    private String method;
    private List<Map.Entry<String, Object>> params;

    @Override
    public void initialize(byte[] bytes, int start, int end) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes, start, end - start);
        DataBuffer dataBuffer = new DataBuffer(byteBuf);
        method = dataBuffer.readUtf8String();
        decodeParams(dataBuffer);

    }

    private void decodeParams(DataBuffer dataBuffer) {
        if (params == null) {
            params = new ArrayList<Map.Entry<String, Object>>();
        }
        int size = dataBuffer.readInt();
        for (int i = 0; i < size; i++) {
            String key = dataBuffer.readAsciiString();
            Object o = decodeObjectByKey(key, dataBuffer);
            params.add(new AbstractMap.SimpleEntry<String, Object>(key, o));
        }
    }

    private Object decodeObjectByKey(String key, DataBuffer dataBuffer) {
        if (key.contains("[]")) {
            if (boolean[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                boolean[] a = new boolean[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (boolean) decodeObjectByType(key, TYPE_BOOLEAN, dataBuffer);
                }
                return a;
            } else if (byte[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                byte[] a = new byte[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (byte) decodeObjectByType(key, TYPE_BYTE, dataBuffer);
                }
                return a;
            } else if (char[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                char[] a = new char[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (char) decodeObjectByType(key, TYPE_CHAR, dataBuffer);
                }
                return a;
            } else if (short[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                short[] a = new short[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (short) decodeObjectByType(key, TYPE_SHORT, dataBuffer);
                }
                return a;
            } else if (int[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                int[] a = new int[len];
                for (int i = 0; i < len; i++) {
                    decodeObjectByType(key, TYPE_INT, dataBuffer);
                }
                return a;
            } else if (long[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                long[] a = new long[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (long) decodeObjectByType(key, TYPE_LONG, dataBuffer);
                }
                return a;
            } else if (float[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                float[] a = new float[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (float) decodeObjectByType(key, TYPE_FLOAT, dataBuffer);
                }
                return a;
            } else if (double[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                double[] a = new double[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (double) decodeObjectByType(key, TYPE_DOUBLE, dataBuffer);
                }
                return a;
            } else if (String[].class.getCanonicalName().equals(key)) {
                int len = dataBuffer.readInt();
                String[] a = new String[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (String) decodeObjectByType(key, TYPE_STRING, dataBuffer);
                }
                return a;
            } else {
                int len = dataBuffer.readInt();
                Packet[] a = new Packet[len];
                for (int i = 0; i < len; i++) {
                    a[i] = (Packet) decodeObjectByType(key, TYPE_PACKET, dataBuffer);
                }
                return a;
            }
        } else {
            if (boolean.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_BOOLEAN, dataBuffer);
            } else if (byte.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_BYTE, dataBuffer);
            } else if (char.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_CHAR, dataBuffer);
            } else if (short.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_SHORT, dataBuffer);
            } else if (int.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_INT, dataBuffer);
            } else if (long.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_LONG, dataBuffer);
            } else if (float.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_FLOAT, dataBuffer);
            } else if (long.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_DOUBLE, dataBuffer);
            } else if (String.class.getCanonicalName().equals(key)) {
                return decodeObjectByType(key, TYPE_STRING, dataBuffer);
            } else {
                return decodeObjectByType(key, TYPE_PACKET, dataBuffer);
            }
        }
    }

    private Object decodeObjectByType(String key, int type, DataBuffer dataBuffer) {
        switch (type) {
            case TYPE_BOOLEAN:
                return (dataBuffer.readByte() & 0x01) == 1;
            case TYPE_BYTE:
                return dataBuffer.readByte();
            case TYPE_CHAR:
                return dataBuffer.readChar();
            case TYPE_SHORT:
                return dataBuffer.readShort();
            case TYPE_INT:
                return dataBuffer.readInt();
            case TYPE_LONG:
                return dataBuffer.readLong();
            case TYPE_FLOAT:
                return dataBuffer.readDouble();
            case TYPE_DOUBLE:
                return dataBuffer.readDouble();
            case TYPE_STRING:
                return dataBuffer.readUtf8String();
            case TYPE_PACKET:
                int length = dataBuffer.readInt();
                try {
                    Class cla = Class.forName(key);
                    Packet packet = (Packet) cla.newInstance();
                    byte[] bytes = dataBuffer.readBytes(length);
                    packet.initialize(bytes, 0, bytes.length);
                    return packet;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException("can not found the class of packet , class path : " + key);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException("can not access packet : " + key);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    throw new RuntimeException("can not generate a new instance of packet : " + key);
                }
            default:
                throw new RuntimeException("unsupported params type");
        }
    }

    @Override
    public byte[] toBytes() {
        DataBuffer dataBuffer = new DataBuffer();
        dataBuffer.writeUtf8String(method);
        int size = params.size();
        dataBuffer.writeInt(size);
        for (int i = 0; i < size; i++) {
            String key = params.get(i).getKey();
            Object value = params.get(i).getValue();
            dataBuffer.writeAsciiString(key);
            encodeObject(key, value, dataBuffer);
        }
        byte[] bytes = new byte[dataBuffer.readableBytes()];
        dataBuffer.getOriginalBuffer().getBytes(0, bytes);
        return bytes;
    }

    private void encodeObject(String key, Object value, DataBuffer dataBuffer) {
        if (key.contains("[]")) {
            if (value instanceof boolean[]) {
                boolean[] a = (boolean[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeByte((byte) (a[i] ? 0x01 : 0x00));
                }
            } else if (value instanceof byte[]) {
                byte[] a = (byte[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeDouble(a[i]);
                }
            } else if (value instanceof char[]) {
                char[] a = (char[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeDouble(a[i]);
                }
            } else if (value instanceof short[]) {
                short[] a = (short[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeDouble(a[i]);
                }
            } else if (value instanceof int[]) {
                int[] a = (int[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeDouble(a[i]);
                }
            } else if (value instanceof long[]) {
                long[] a = (long[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeDouble(a[i]);
                }
            } else if (value instanceof float[]) {
                float[] a = (float[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeDouble(a[i]);
                }
            } else if (value instanceof double[]) {
                double[] a = (double[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeDouble(a[i]);
                }
            } else if (value instanceof String[]) {
                String[] a = (String[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    dataBuffer.writeUtf8String(a[i]);
                }
            } else if (value instanceof Packet[]) {
                Packet[] a = (Packet[]) value;
                int len = a.length;
                dataBuffer.writeInt(len);
                for (int i = 0; i < len; i++) {
                    byte[] bytes = a[i].toBytes();
                    int length = bytes.length;
                    dataBuffer.writeInt(length);
                    dataBuffer.writeBytes(bytes);
                }
            } else {
                throw new RuntimeException("unsupported parameter type : " + value.getClass().getCanonicalName());
            }
        } else {
            if (value instanceof Boolean) {
                dataBuffer.writeByte((byte) ((Boolean) value ? 0x01 : 0x00));
            } else if (value instanceof Byte) {
                dataBuffer.writeByte((Byte) value);
            } else if (value instanceof Character) {
                dataBuffer.writeChar((Character) value);
            } else if (value instanceof Short) {
                dataBuffer.writeShort((Short) value);
            } else if (value instanceof Integer) {
                dataBuffer.writeInt((Integer) value);
            } else if (value instanceof Long) {
                dataBuffer.writeLong((Long) value);
            } else if (value instanceof Float) {
                dataBuffer.writeDouble((Float) value);
            } else if (value instanceof Double) {
                dataBuffer.writeDouble((Double) value);
            } else if (value instanceof String) {
                dataBuffer.writeUtf8String((String) value);
            } else if (value instanceof Packet) {
                byte[] bytes = ((Packet) value).toBytes();
                dataBuffer.writeInt(bytes.length);
                dataBuffer.writeBytes(bytes);
            } else {
                throw new RuntimeException("unsupported parameter type : " + value.getClass().getCanonicalName());
            }
        }
    }


    public void setMethod(String method) {
        this.method = method;
    }

    public void setParams(List<Map.Entry<String, Object>> params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public List<Map.Entry<String, Object>> getParams() {
        return params;
    }

}
