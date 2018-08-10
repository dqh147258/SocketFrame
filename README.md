# SocketFrame
基于Netty的网络通信框架

## 基本使用
添加引用
```
    implementation project(':socketframe')
    annotationProcessor project(':socketframeprocessor')
```
创建服务端接口和客户端回调接口

举例:
服务端接口
```
@SocketInterface
public interface ServerInterface {

    void helloWorld(int time);
}
```
客户端接口
```
@SocketInterface
public interface ClientCallback {

    void interesting(String string);

}
```
服务端和客户端的接口方法都是可以自定义的,以上只是举个简单的例子,然后有个必须要有的是需要在接口类上添加`@SocketInterface`注解

启动服务端
```
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
        SocketServer socketServer = new SocketServer.Builder(12345)
                .setPacketHandler(serverInterfaceHandler)
                .create();
        socketServer.start();
```

启动客户端
```
        ClientInterfaceHandler<ServerInterface, ClientCallback> clientInterfaceHandler = new ClientInterfaceHandler<ServerInterface, ClientCallback>(ServerInterface.class, new ClientCallback() {
            @Override
            public void reply(String string) {
                Log.d(TAG, string);
            }
        });
        mServerInterface = clientInterfaceHandler.getInterface();
        SocketClient socketClient = new SocketClient.Builder("127.0.0.1", 12345)
                .setPacketHandler(clientInterfaceHandler)
                .create();
        socketClient.start();
```

然后在需要时调用客户端接口
```
        mServerInterface.helloWorld(5);
```

服务端便会打印5次"Hello world !",然后给客户端回一句"Nice to meet you !"

![](http://resource-1255703580.cossh.myqcloud.com/SocketFrame/Example/hello_word_and_reply.png)

服务端的关闭
```
        socketServer.stop();
```

客户端的关闭
```
        socketClient.stop();
```

## 持续开发中 ......
