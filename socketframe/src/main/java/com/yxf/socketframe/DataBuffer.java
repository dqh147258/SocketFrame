package com.yxf.socketframe;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * ByteBuf
 * 
 * @author Nana
 */
public class DataBuffer {
    public ByteBuf mByteBuf;

    public DataBuffer() {
        mByteBuf = Unpooled.buffer();
    }

    public DataBuffer(int initialCapacity) {
        mByteBuf = Unpooled.buffer(initialCapacity);
    }

    public DataBuffer(ByteBuf byteBuf) {
        mByteBuf = byteBuf;
    }

    public DataBuffer(DataBuffer buffer1, DataBuffer buffer2) {
        mByteBuf = Unpooled.buffer();
        writeDataBuffer(buffer1);
        writeDataBuffer(buffer2);

        // TODO:use api method later
        //mByteBuf = Unpooled.wrappedBuffer(buffer1.getOriginalBuffer(), buffer2.getOriginalBuffer());
    }

    ///////////////////////////////////////////////////////////////////
    public boolean isReadable() {
        return mByteBuf.isReadable();
    }

    public int readableBytes() {
        return mByteBuf.readableBytes();
    }

    public void writeDataBuffer(DataBuffer inputBuffer) {
        if (null == inputBuffer || inputBuffer.readableBytes() == 0) {
            return;
        }
        mByteBuf.writeBytes(inputBuffer.mByteBuf);
    }

    public void resetReaderIndex() {
        mByteBuf.resetReaderIndex();
    }

    public void resetWriterIndex() {
        mByteBuf.resetWriterIndex();
    }
    
    ///////////////////////////////////////////////////////////////////
    public byte[] array() {
        return mByteBuf.array();
    }

    public void setOrignalBuffer(ByteBuf buffer) {
        mByteBuf = buffer;
    }

    public ByteBuf getOriginalBuffer() {
        return mByteBuf;
    }   
    
    ///////////////////////////////////////////////////////////////////
    public byte readByte() {
        return mByteBuf.readByte();
    }

    public void writeByte(int value) {
        mByteBuf.writeByte(value);
    }

    public char readChar() {
        return mByteBuf.readChar();
    }

    public void writeChar(int c) {
        mByteBuf.writeChar(c);
    }

    public short readShort() {
        return mByteBuf.readShort();
    }

    public int readUnsignedShort() {
        return mByteBuf.readUnsignedShort();
    }

    public void writeShort(int value) {
        mByteBuf.writeShort(value);
    }

    public int readInt() {
        return mByteBuf.readInt();
    }

    public long readUnsignedInt() {
        return mByteBuf.readUnsignedInt();
    }

    public void writeInt(int value) {
        mByteBuf.writeInt(value);
    }

    public long readLong() {
        return mByteBuf.readLong();
    }

    public void writeLong(long value) {
        mByteBuf.writeLong(value);
    }

    public double readDouble() {
        return mByteBuf.readDouble();
    }

    public void writeDouble(double value) {
        mByteBuf.writeDouble(value);
    }

    ///////////////////////////////////////////////////////////////////
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        mByteBuf.readBytes(bytes);
        return bytes;
    }

    public byte[] readBytes() {
        int length = readUnsignedShort();
        return readBytes(length);
    }

    public void writeBytes(byte[] bytes) {
        mByteBuf.writeBytes(bytes);
    }

    public void writeBytes(byte[] bytes, int index, int length) {
        mByteBuf.writeBytes(bytes, index, length);
    }

    public void writeSourceBytes(byte[] bytes) {
        writeShort(bytes.length);
        mByteBuf.writeBytes(bytes);
    }

    ///////////////////////////////////////////////////////////////////
    public String readAsciiString() {
        int length = readUnsignedShort();
        return readAsciiString(length);
    }

    public String readAsciiString(int length) {
        byte[] bytes = readBytes(length);
        return new String(bytes, Charset.forName("ascii"));
    }

    public void writeAsciiString(String str) {
        if (str == null || str.equals("")) {
            writeShort(0);
        } else {
            byte[] bytes = str.getBytes(Charset.forName("ascii"));
            writeShort(bytes.length);
            writeBytes(bytes);
        }
    }
    ///////////////////////////////////////////////////////////////////

    public String readUtf8String() {
        int length = readUnsignedShort();
        return readUtf8String(length);
    }
    
    public String readUtf8String(int length) {
        byte[] bytes = readBytes(length);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public String readUtf16String() {
        int length = readUnsignedShort();
        return readUtf16String(length);
    }

    public String readUtf16String(int length) {
        byte[] bytes = readBytes(length);
        return new String(bytes, Charset.forName("UTF-16LE"));
    }

    public void writeUtf8String(String str) {
        if (str == null) {
            writeShort(0);
        } else {
            byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
            writeShort(bytes.length);
            writeBytes(bytes);
        }
    }

    public void writeUtf16String(String str) {
        if (str == null) {
            writeShort(0);
        } else {
            byte[] bytes = str.getBytes(Charset.forName("UTF-16LE"));
            writeShort(bytes.length);
            writeBytes(bytes);
        }
    }
    ///////////////////////////////////////////////////////////////////

    public int[] readIntArray() {
        int count = readUnsignedShort();
        int[] intArray = new int[count];
        for (int i = 0; i < count; i++) {
            intArray[i] = readInt();
        }
        return intArray;
    }

    public void writeIntArray(int[] intArray) {
        int count = intArray.length;
        writeShort(count);
        for (int i = 0; i < count; i++) {
            writeInt(intArray[i]);
        }
    }
    
    ///////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return mByteBuf.toString();
    }
}
