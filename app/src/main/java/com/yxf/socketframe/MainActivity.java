package com.yxf.socketframe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yxf.interfaces.ClientCallback;
import com.yxf.interfaces.ServerInterface;
import com.yxf.socketframe.handler.ClientInterfaceHandler;
import com.yxf.socketframe.handler.ServerInterfaceHandler;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ClientCallback mClientCallback;
    private ServerInterface mServerInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServerInterfaceHandler<ServerInterface, ClientCallback> serverInterfaceHandler = new ServerInterfaceHandler<ServerInterface, ClientCallback>(ClientCallback.class, new ServerInterface() {
            @Override
            public void helloWorld(int time) {
                for (int i = 0; i < time; i++) {
                    Log.d(TAG, "Hello world !");
                }
                mClientCallback.reply("Nice to meet you !");
            }
        });
        mClientCallback = serverInterfaceHandler.getCallback();
        final SocketServer socketServer = new SocketServer.Builder(12345)
                .setPacketHandler(serverInterfaceHandler)
                .create();
        socketServer.start();

        ClientInterfaceHandler<ServerInterface, ClientCallback> clientInterfaceHandler = new ClientInterfaceHandler<ServerInterface, ClientCallback>(ServerInterface.class, new ClientCallback() {
            @Override
            public void reply(String string) {
                Log.d(TAG, string);
            }
        });
        mServerInterface = clientInterfaceHandler.getInterface();
        final SocketClient socketClient = new SocketClient.Builder("127.0.0.1", 12345)
                .setPacketHandler(clientInterfaceHandler)
                .create();
        socketClient.start();

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                mServerInterface.helloWorld(5);
            }
        }, 4000);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                socketServer.stop();
                socketClient.stop();
            }
        }, 8000);
        socketServer.stop();
        socketClient.stop();
    }
}
