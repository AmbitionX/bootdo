package com.wx.demo.frameWork.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wx.demo.frameWork.protocol.WechatServiceGrpc;
import com.wx.demo.tools.WechatUtil;
import com.wx.demo.bean.WxLongCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WechatSocket {
    private static final Logger logger = LoggerFactory.getLogger(WechatSocket.class);
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String host;

    private int port;

    private EventLoopGroup eventLoopGroup;

    private Bootstrap bootstrap;

    private ChannelFuture channelFuture;

    private ConcurrentHashMap<Integer, WxLongCallback> callbacks = new ConcurrentHashMap<Integer, WxLongCallback>();

    private WechatServiceGrpc wechatServiceGrpc;

    public WechatSocket(WechatServiceGrpc wechatServiceGrpc) {
        this.wechatServiceGrpc = wechatServiceGrpc;

        WechatClientHandler handler = new WechatClientHandler(this);
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(eventLoopGroup);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new IdleStateHandler(0, 90, 0));
                socketChannel.pipeline().addLast(handler);
            }
        });

    }

    public void connect(String host,int port) {
        if (host.length() > 0) {
            this.host = host;
            this.port = port;
        }

        if (this.host == null || this.host.length() <= 0) {
            return;
        }

        if (channelFuture != null) {
            logger.info(dateFormat.format(new Date())+"-connect-"+channelFuture.channel().isOpen()+"-"+channelFuture.channel().isActive());

            if (channelFuture.channel().isActive()) {
                return;
            }

            channelFuture.cancel(true);
            channelFuture = null;
        }

        channelFuture = bootstrap.connect(this.host, this.port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {//断线重连
                    wechatServiceGrpc.autoLogin();
                }

                if (!future.isSuccess()) {
                    EventLoop loop = future.channel().eventLoop();
                    loop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            connect("",0);
                        }
                    }, 1L, TimeUnit.SECONDS);

                }
            }
        });

        logger.info(dateFormat.format(new Date())+"-connect");

        try {
            channelFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(dateFormat.format(new Date())+"-connect-fail");
        }


    }

    public void close() {
        if (channelFuture != null) {
            logger.info(dateFormat.format(new Date())+"-close");
            host = "";
            port = 0;
            channelFuture.channel().close();
            channelFuture.cancel(true);
            channelFuture = null;
        }
    }

    public void sendData(byte[] data,WxLongCallback callback) {
        if (channelFuture == null || !channelFuture.channel().isActive()) {
            return;
        }

        int reqSeq = WechatUtil.byteArrayToInt(data, 12);
        callbacks.put(reqSeq,callback);

        ByteBuf buf = channelFuture.channel().alloc().buffer(data.length);
        buf.writeBytes(data);
        channelFuture.channel().writeAndFlush(buf);
    }

    public void handleCallback(byte[] data) {
        int reqSeq = WechatUtil.byteArrayToInt(data, 12);
        if (reqSeq == 0) {
            return;
        }

        WxLongCallback callback = callbacks.remove(reqSeq);
        if (callback != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    callback.onData(data);
                }
            }).start();
        }
    }

    public void sendWechatHeartbeat() {
        wechatServiceGrpc.sendWechatHeartbeat();
    }

    public void sendWechatNewMsg() {
        wechatServiceGrpc.sendWechatNewMsg();
    }

}
