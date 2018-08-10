package com.yxf.socketframe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yxf.interfaces.ClientCallback;
import com.yxf.interfaces.ServerInterface;
import com.yxf.socketframe.handler.ClientInterfaceHandler;
import com.yxf.socketframe.handler.ServerInterfaceHandler;
import com.yxf.socketframe.handler.PacketHandler;

public class MainActivity extends AppCompatActivity implements ServerInterface {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ClientCallback mClientCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServerInterfaceHandler<ServerInterface, ClientCallback> serverInterfaceHandler = new ServerInterfaceHandler<ServerInterface, ClientCallback>(ClientCallback.class, this);
        mClientCallback = serverInterfaceHandler.getCallback();
        SocketServer socketServer = new SocketServer.Builder(12345)
                .setPacketHandler(serverInterfaceHandler)
                .create();
        socketServer.start();

        ClientInterfaceHandler<ServerInterface, ClientCallback> clientInterfaceHandler = new ClientInterfaceHandler<ServerInterface, ClientCallback>(ServerInterface.class, new ClientCallback() {
            @Override
            public void interesting(String string) {
                Log.d(TAG, string + " interesting");
            }
        });
        final ServerInterface serverInterface = clientInterfaceHandler.getInterface();
        SocketClient socketClient = new SocketClient.Builder("127.0.0.1", 12345)
                .setPacketHandler(clientInterfaceHandler)
                .create();
        socketClient.start();

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                serverInterface.helloWorld(5);
            }
        }, 4000);

    }

    @Override
    public void helloWorld(int count) {
        for (int i = 0; i < count; i++) {
            Log.d(TAG, "hello world !");
        }
        mClientCallback.interesting("you are");
    }
}
