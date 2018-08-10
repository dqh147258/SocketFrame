package com.yxf.socketframe;


import com.yxf.interfaces.ClientCallback;
import com.yxf.interfaces.ServerInterface;
import com.yxf.socketframe.handler.ClientInterfaceHandler;
import com.yxf.socketframe.handler.ServerInterfaceHandler;
import com.yxf.socketframe.heartbeat.SimplePermissionCheck;
import com.yxf.socketframe.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String TAG = Main.class.getSimpleName();

    private static ClientCallback mClientCallback;

    public static void main(String[] args) {
        /*ServerInterfaceHandler<ServerInterface, ClientCallback> serverInterfaceHandler = new ServerInterfaceHandler(ClientCallback.class, new ServerInterface() {
            @Override
            public void helloWorld() {
                Log.d("hello world !");
                mClientCallback.reply();
            }
        });
        mClientCallback = serverInterfaceHandler.getCallback();
        SocketServer socketServer = new SocketServer.Builder(12345)
                .setPacketHandler(serverInterfaceHandler)
                .create();
        socketServer.start();

        ClientInterfaceHandler<ServerInterface, ClientCallback> clientInterfaceHandler = new ClientInterfaceHandler(ServerInterface.class, new ClientCallback() {
            @Override
            public void reply() {
                Log.d(TAG, "reply");
            }
        });
        final ServerInterface serverInterface = clientInterfaceHandler.getInterface();
        SocketClient socketClient = new SocketClient.Builder("127.0.0.1", 12345)
                .setPacketHandler(clientInterfaceHandler)
                .create();
        socketClient.start();*/

        ArrayList[] lists = new ArrayList[4];
        Log.d(lists instanceof List[]);

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*socketClient.stop();
        socketServer.stop();*/
        System.exit(0);
    }

    public static class Test {


        public static class Internal {

        }
    }

}
