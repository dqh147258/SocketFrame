package com.yxf.socketframe.heartbeat;

public class SimplePermissionCheck implements PermissionCheck<Integer> {
    @Override
    public byte[] encode(Integer integer) {
        byte[] bytes = new byte[4];
        int value = integer;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (value << (i + 1) * 8 >> 24);
        }
        return bytes;
    }

    @Override
    public Integer decode(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) + (bytes[i] & 0xff);
        }
        return value;
    }

    @Override
    public Integer generateNewData() {
        return (int) (Math.random() * 100);
    }

    @Override
    public Integer change(Integer integer) {
        return (int) (integer * Math.sin(integer / 10.0 * Math.PI) * 2);
    }

    @Override
    public boolean equals(Integer d1, Integer d2) {
        return d1 == d2;
    }
}
