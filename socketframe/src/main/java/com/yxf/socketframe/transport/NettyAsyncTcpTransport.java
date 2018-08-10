package com.yxf.socketframe.transport;

import com.yxf.socketframe.DataBuffer;
import com.yxf.socketframe.Profile;
import com.yxf.socketframe.packet.WrapPacket;
import com.yxf.socketframe.util.Log;
import com.yxf.socketframe.util.SocketUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyAsyncTcpTransport implements Transport {


    private ExecutorService mExecutorService;

    private final String mAddress;
    private final int mPort;

    private String mId;

    private volatile NettyTcpTask mNettyTcpTask;

    private TransportCallback mTransportCallback;

    public NettyAsyncTcpTransport(String address, int port) {
        this.mAddress = address;
        this.mPort = port;
    }

    @Override
    public boolean write(WrapPacket packet) {
        if (mNettyTcpTask != null) {
            ChannelFuture future = mNettyTcpTask.getChannelFuture();
            return SocketUtils.writeWrapPacket(packet, future.channel());
        }
        return false;
    }

    @Override
    public void setTransportCallback(TransportCallback callback) {
        mTransportCallback = callback;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void start() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
        mNettyTcpTask = new NettyTcpTask();
        mExecutorService.submit(mNettyTcpTask);
    }

    @Override
    public void stop() {
        mNettyTcpTask.stop();
    }

    private class NettyClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            mId = ctx.channel().remoteAddress().toString();
            mTransportCallback.onConnectSuccessfully(mId);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            WrapPacket packet = SocketUtils.decodeWrapPacketFromByteBufAfterSplit((ByteBuf) msg);
            mTransportCallback.onNewPacketReceived(NettyAsyncTcpTransport.this, packet);
        }
    }


    private class NettyTcpTask implements Runnable {

        private boolean mIsRunning = false;

        ChannelFuture mChannelFuture;


        public void stop() {
            if (mChannelFuture != null) {
                if (isRunning()) {
                    mChannelFuture.channel().close();
                }
            }
        }

        public boolean isRunning() {
            return mIsRunning;
        }

        public ChannelFuture getChannelFuture() {
            return mChannelFuture;
        }

        @Override
        public void run() {
            mIsRunning = true;
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.group(eventLoopGroup);
                bootstrap.remoteAddress(mAddress, mPort);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("framedecoder", new LengthFieldBasedFrameDecoder(1024 * 1024 * 10, 0, 4, 0, 4));
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
                mChannelFuture = bootstrap.connect(mAddress, mPort).sync();
                if (mChannelFuture.isSuccess()) {
                    Log.d("connect to mAddress : " + mAddress + ", mPort : " + mPort + " succeed");
                }
                mChannelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("can not connect to mAddress : " + mAddress + " ,mPort : " + mPort);
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
            mIsRunning = false;
        }
    }
}
