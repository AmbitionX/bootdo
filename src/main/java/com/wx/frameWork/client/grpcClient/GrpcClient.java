package com.wx.frameWork.client.grpcClient;

import com.wx.frameWork.proto.WechatGrpc;
import com.wx.server;
import com.wx.tools.ConfigService;
import com.wx.tools.Settings;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.log4j.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;

public class GrpcClient {

    private static Logger logger = Logger.getLogger(GrpcClient.class);
    private String host;
    private String appId = Settings.getSet().appId;
    private String appKey = Settings.getSet().appKey;
    private String apiappId = Settings.getSet().apiappId;
    private String apiappKey = Settings.getSet().apiappKey;
    private boolean api;
    private int port;
    private File CerFile = null;
    private boolean ssl = false;
    private WechatGrpc.WechatBlockingStub stub;
    private ManagedChannel channel;
    public GrpcClient(String ip, int port,boolean api) {
        this.host = ip;
        this.port = port;
        this.api = api;
        CerFile = null;
    }
    public GrpcClient(String ip, int port) {
        this.host = ip;
        this.port = port;
        this.CerFile = ConfigService.getCrtFile();
    }

    public void create() throws SSLException {
        close();
        SslContext sslContext;
        if(CerFile != null){
            logger.info("使用 SSL/TLS 创建 [安全] 链接成功！当前有效连接数:[" + (GrpcPool.getInstance().getTotalClientNum() +1) + "]个  SSL/TLS 路径:[" + CerFile + "]" );
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
