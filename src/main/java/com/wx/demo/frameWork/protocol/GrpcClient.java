package com.wx.demo.frameWork.protocol;

import com.wx.demo.frameWork.proto.WechatGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import com.wx.demo.frameWork.proto.WechatMsg;


public class GrpcClient {

    private String ip;

    private int port;

    private ManagedChannel channel;

    private WechatGrpc.WechatBlockingStub stub;

    public GrpcClient(String ip,int port) {
        this.ip = ip;
        this.port = port;
        create();
    }


    private void create() {
        try {
            SslContextBuilder builder = GrpcSslContexts.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE);
            SslContext sslContext = builder.build();

            channel = NettyChannelBuilder.forAddress(ip, port)
                    .sslContext(sslContext)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Metadata data = new Metadata();
        data.put(Metadata.Key.of("appid", Metadata.ASCII_STRING_MARSHALLER), "v1_wxidandy26_CodeVip");
        data.put(Metadata.Key.of("appkey", Metadata.ASCII_STRING_MARSHALLER), "v2_1576df1307c0400d2f99ff18b254cf17");
        stub = WechatGrpc.newBlockingStub(channel);
        stub = MetadataUtils.attachHeaders(stub, data);
    }

    public void close() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdownNow();
            channel = null;
        }
    }

    public WechatMsg helloWechat(WechatMsg wechatMsg) {
        return stub.helloWechat(wechatMsg);
    }

}
