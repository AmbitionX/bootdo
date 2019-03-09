package com.wx.demo.frameWork.client.grpcClient;

import com.wx.demo.frameWork.proto.WechatGrpc;
import com.wx.demo.tools.WechatUtil;
import com.wx.demo.tools.Settings;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.File;

public class GrpcClient {

    private static Logger logger = Logger.getLogger(GrpcClient.class);

    public String getHost() {
        return host;
    }

    private String host;
    private String appId = WechatUtil.AppId;
    private String appKey = WechatUtil.AppKey;
    private String apiappId =WechatUtil.ApiId;
    private String apiappKey = WechatUtil.ApiKey;
    private boolean SSL = WechatUtil.SSL;
    private boolean api;
    private int port;
    private File CerFile;
    private WechatGrpc.WechatBlockingStub stub;
    private ManagedChannel channel;
    public GrpcClient(){
        this.api = false;
        this.SSL = false;
    }
    public GrpcClient(boolean SSL){
        this.api = false;
        this.SSL = SSL;
    }
    public GrpcClient(boolean api,boolean SSL){
        this.api = api;
        this.SSL = SSL;
    }

    public void create(String ip, int port) {
        this.host = ip;
        this.port = port;
        create();
    }


    public SslContext getSslContext(boolean ssl) throws SSLException {
        SslContext sslContext;
        if(ssl){
            CerFile = new File(WechatUtil.getCrtFile());
            logger.info("使用 SSL/TLS 创建 [安全] 链接成功！当前有效连接数:[" + (IpadApplication.getInstance().getTotalClientNum() +1) + "]个  SSL/TLS 路径:[" + CerFile + "]" );
            SslProvider provider = SslContext.defaultClientProvider();
            switch (provider) {
                case JDK:
                    sslContext = new JdkSslClientContext(
                            CerFile,
                            InsecureTrustManagerFactory.INSTANCE,
                            null,
                            null,
                            null,
                            null,
                            null,
                            IdentityCipherSuiteFilter.INSTANCE,
                            new ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol.ALPN,
                                    ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL,
                                    ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT, "h2", "TLSv1.2"),
                            0, 0);
                    break;
                case OPENSSL:
                default:
                    sslContext = new OpenSslClientContext(
                            CerFile,
                            InsecureTrustManagerFactory.INSTANCE,
                            null, new ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol.ALPN,
                            ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL,
                            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT, "h2", "TLSv1.2"),
                            0,0);
                    break;
            }
        } else {
            logger.info("不使用 SSL/TLS 创建链接！[不安全]");
            sslContext = GrpcSslContexts.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        }
        return sslContext;
    }
    public void create()  {
        close();
        SslContext sslContext = null;
        try {
            sslContext = getSslContext(SSL);
        } catch (SSLException e) {
            logger.info("加载证书失败!",e);
            e.printStackTrace();
        }
        channel = NettyChannelBuilder.forAddress(host, port)
                .overrideAuthority("root")
                .sslContext(sslContext)
                .build();
        Metadata data = new Metadata();
        if(api){
            data.put(Metadata.Key.of("appid", Metadata.ASCII_STRING_MARSHALLER), apiappId);
            data.put(Metadata.Key.of("appkey", Metadata.ASCII_STRING_MARSHALLER), apiappKey);
        }else {
            data.put(Metadata.Key.of("appid", Metadata.ASCII_STRING_MARSHALLER), appId);
            data.put(Metadata.Key.of("appkey", Metadata.ASCII_STRING_MARSHALLER), appKey);
        }
        stub = WechatGrpc.newBlockingStub(channel);
        stub = MetadataUtils.attachHeaders(stub, data);
    }

    public void close() {
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    public String getIp() {
        return host;
    }

    public void setIp(String ip) {
        this.host = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public WechatGrpc.WechatBlockingStub getStub() {
        return stub;
    }

    public void setStub(WechatGrpc.WechatBlockingStub stub) {
        this.stub = stub;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }
}
