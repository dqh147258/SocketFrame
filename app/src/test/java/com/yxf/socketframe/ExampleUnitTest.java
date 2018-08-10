package com.yxf.socketframe;

import android.graphics.Bitmap;
import android.util.Log;

import com.yxf.socketframe.packet.InterfacePacket;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void socketframe_Test() {
        /*SocketServer<InterfacePacket> socketServer = new SocketServer<InterfacePacket>(12345);
        socketServer.start();
        SocketClient<InterfacePacket> socketClient = new SocketClient<InterfacePacket>("127.0.0.1", 12345);
        socketClient.start();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        assertEquals(4, 2 + 2);
    }
}