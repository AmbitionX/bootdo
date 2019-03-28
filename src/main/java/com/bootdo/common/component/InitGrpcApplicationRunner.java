package com.bootdo.common.component;

import com.bootdo.common.redis.shiro.RedisManager;
import com.server;
import com.wx.demo.frameWork.client.grpcClient.IpadApplication;
import com.wx.demo.frameWork.protocol.ServiceManagerDemo;
import com.wx.demo.frameWork.protocol.WechatServiceGrpc;
import com.wx.demo.tools.Constant;
import com.wx.demo.tools.WechatUtil;
import com.wx.demo.wechatapi.model.WechatApi;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.wx.demo.tools.WechatUtil.getMd5;

/**
 * Created by l2h on 18-4-16.
 * Desc: 系统启动完可以做一些业务操作
 * @author l2h
 */
@Component
//如果有多个runner需要指定一些顺序
@Order(1)
    public class InitGrpcApplicationRunner implements ApplicationRunner {
//    @Autowired
//    SystemInitService systemInitService;
    private static org.slf4j.Logger logger= LoggerFactory.getLogger(server.class);

    @Value("${server.port}")
    private String proPort;

    private static int serverport;
    private static String serverip;
    private static String hostAddress;
    private static String serverhost;
    private static String serverid;

    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
    private static void uploginedUsers() {
        Map<byte[], byte[]> loginedUsers = RedisManager.hGetAll((Constant.redisk_key_loinged_user + serverid).getBytes());
        Set<byte[]> keySet = loginedUsers.keySet();
        for (byte[] key : keySet) {
            WechatApi bean = WechatApi.unserizlize(loginedUsers.get(key));
            executorService.submit(() -> {
                bean.cmd(702);
                WechatServiceGrpc service = ServiceManagerDemo.getInstance().createGrpcService(bean);
                service.autoLogin();
            });
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        Environment env = ac.getEnvironment();
//        WechatUtil.init();
        serverport = Integer.parseInt(proPort);
        hostAddress = InetAddress.getLocalHost().getHostAddress();
        if(serverport > 1000 && serverport < 65000){
            WechatUtil.server_port = serverport;
        } else {
            serverport = WechatUtil.server_port;
        }
        serverip = WechatUtil.serverIp;
        serverhost = serverip + ":" + serverport;
        serverid = getMd5(serverhost);
        WechatUtil.serverId = serverid;
        // 启动Grpc客户端
        IpadApplication.getInstance().init();// uploginedUsers(serverid);
        uploginedUsers();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                //IpadApplication.getInstance().destroy();
                logger.info("-------服务器已关闭,host:[" + serverhost + "] 服务器ID:[" + serverid + "]-------");
            }
        });
//        logger.info(logo.logo, env.getProperty("spring.application.name"),
//                serverport, hostAddress, serverport, hostAddress, serverport,
//                serverport, serverip, serverport, serverid);
    }
}