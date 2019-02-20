package com.wx;

import com.sun.net.httpserver.HttpServer;
import com.wx.bean.RedisBean;
import com.wx.frameWork.client.grpcClient.GrpcPool;
import com.wx.httpHandler.HttpResult;
import com.wx.httpHandler.MyHttpHandler;
import com.wx.service.BaseService;
import com.wx.service.ServiceManager;
import com.wx.tools.ConfigService;
import com.wx.tools.Constant;
import com.wx.tools.RedisUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wx.tools.ConfigService.getMd5;

public class server {
    static Logger logger;
    private static int serverport;
    private static String serverip;
    private static String serverhost;
    private static String serverid;
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);

    private static void uploginedUsers (){
        Map<byte[], byte[]> loginedUsers = RedisUtils.hGetAll((Constant.redisk_key_loinged_user + serverid).getBytes());
        Set<byte[]> keySet = loginedUsers.keySet();
        for (byte[] key : keySet) {
            RedisBean bean = RedisBean.unserizlize(loginedUsers.get(key));
            executorService.submit(() -> {
                BaseService service = ServiceManager.getInstance().createServiceForReLogin(bean.randomid, bean.softwareId);
                service.loadLoginedUser(bean);
            });
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println("开始启动wxServer...");
        ConfigService.init();
        logger = Logger.getLogger(server.class);
        if (args.length > 0) {
            String str = args[0];
            serverport = Integer.parseInt(str);
            if (serverport > 1000 & serverport < 65000) {
                ConfigService.server_port = serverport;
            }
        } else {
            serverport = ConfigService.server_port;
        }
        serverip = ConfigService.ServerIp;
        serverhost = serverip + ":" + serverport;
        serverid = getMd5(serverhost);
        ConfigService.serverid =serverid;
        // 启动Grpc客户端
        GrpcPool.getInstance().init();// uploginedUsers(serverid);
        uploginedUsers();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                //GrpcPool.getInstance().destroy();
                logger.info("-------服务器已关闭,host:[" + serverhost + "] 服务器ID:[" + serverid +"]-------");
            }
        });
        logger.info ( "-------服务器已启动,host:[" + serverhost + "] 服务器ID:[" + serverid + "]-------" );
        System.out.println("-------服务器已启动,host:[" + serverhost + "] 服务器ID:[" + serverid + "]-------");
    }

}



