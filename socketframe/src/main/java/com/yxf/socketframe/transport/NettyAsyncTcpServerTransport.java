package com.yxf.socketframe.transport;

import com.yxf.socketframe.packet.WrapPacket;
import com.yxf.socketframe.util.SocketUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import static com.yxf.socketframe.ConnectionStateChangedListener.*;

public class NettyAsyncTcpServerTransport implements ServerTransport {


    private final int mPort;

    private ExecutorService mExecutorService;

    private volatile NettyTcpTask mNettyTcpTask;

    private ServerTransportCallback mServerTransportCallback;

    private Map<String, SubTransport> mChannelTransportMap = new ConcurrentHashMap<String, SubTransport>();

    public NettyAsyncTcpServerTransport(int port) {
        this.mPort = port;

    }

    @Override
    public void setServerTransportCallback(ServerTransportCallback callback) {
        mServerTransportCallback = callback;
    }

    @Override
    public void start() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
        mNettyTcpTask = new NettyTcpTask();
        Future future = mExecutorService.submit(mNettyTcpTask);
    }

    @Override
    public void stop() {
        mNettyTcpTask.stop();
    }

    private class SubTransport implements Transport {

        private Channel mChannel;
        private String mId;

        private int mState = CONNECTION_STATE_DISCONNECTED;

        private TransportCallback mTransportCallback;

        public SubTransport(Channel channel) {
            mChannel = channel;
            mState = CONNECTION_STATE_CONNECT_SUCCESSFULLY;
            mId = channel.remoteAddress().toString();
        }

        @Override
        public boolean write(WrapPacket packet) {
            return SocketUtils.writeWrapPacket(packet, mChannel);
        }

        @Override
        public void setTransportCallback(TransportCallback callback) {
            mTransportCallback = callback;
            SocketUtils.dispatchConnectionStateChanged(mTransportCallback, mState, mId);
        }

        @Override
        public String getId() {
            return mId;
        }

        @Override
        public void start() {
            //do nothing
        }

        @Override
        public void stop() {
            mChannel.close();
        }

        public void onNewPacketReceived(WrapPacket packet) {
            if (mTransportCallback != null) {
                mTransportCallback.onNewPacketReceived(this, packet);
            }
        }
    }

    private class NettyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            initTransport(ctx.channel());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            WrapPacket packet = SocketUtils.decodeWrapPacketFromByteBufAfterSplit((ByteBuf) msg);
            String channelId = ctx.channel().id().asLongText();
            SubTransport transport = mChannelTransportMap.get(channelId);
            if (transport == null) {
                throw new RuntimeException("can not get transport form map , create a transport and put to map");
            }
            transport.onNewPacketReceived(packet);
        }

        private SubTransport initTransport(Channel channel) {
            String channelId = channel.id().asLongText();
            SubTransport transport = new SubTransport(channel);
            mChannelTransportMap.put(channelId, transport);
            mServerTransportCallback.onTransportAccept(transport);
            return transport;
        }
    }

    private class NettyTcpTask implements Runnable {

        private boolean mIsRunning = false;

        ChannelFuture mChannelFuture;


        public void stop() {
            if (mChannelFuture != null) {
                if (isRunning()) {
                    mChannelFuture.addListener(ChannelFutureListener.CLOSE);
                }
            }
        }

        public boolean isRunning() {
            return mIsRunning;
        }

        @Override
        public void run() {
            mIsRunning = true;
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker);
                bootstrap.channel(NioServerSocketChannel.class);
                bootstrap.option(ChannelOption.TCP_NODELAY, true);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast("framedecoder", new LengthFieldBasedFrameDecoder(1024 * 1024 * 10, 0, 4, 0, 4));
                        p.addLast(new NettyServerHandler());
                    }
                });
                mChannelFuture = bootstrap.bind(mPort).sync();
                if (mChannelFuture.isSuccess()) {
                    System.out.printf("start tcp server succeed,mPort : " + mPort);
                }
                mChannelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                System.out.printf("a exception happened while tcp server running");
                e.printStackTrace();
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
                mIsRunning = false;
            }
        }
    }
}
