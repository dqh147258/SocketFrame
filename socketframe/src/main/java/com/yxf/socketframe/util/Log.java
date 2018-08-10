package com.yxf.socketframe.util;

public class Log {

    public static void d(String TAG, Object message) {
        System.out.printf(message.toString());
    }

    public static void d(Object message) {
        System.out.println(message.toString());
    }

    public static void dump(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            d("dump : the bytes is null");
            return;
        }
        if (bytes.length == 0) {
            d("dump : the bytes is null");
        }
        StringBuilder builder = new StringBuilder("dump : ");
        builder.append(bytes[0] & 0xff);
        for (int i = 1; i < bytes.length; i++) {
            builder.append(" ").append(bytes[i] & 0xff);
        }
        d(builder.toString());
    }
}
