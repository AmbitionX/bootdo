package com;

import com.bootdo.common.redis.shiro.RedisManager;
import com.wx.demo.frameWork.protocol.ServiceManagerDemo;
import com.wx.demo.frameWork.protocol.WechatServiceGrpc;
import com.wx.demo.tools.Constant;
import com.wx.demo.wechatapi.model.WechatApi;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.net.ssl.SSLException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @EnableAutoConfiguration(exclude = {
 *         org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
 * })
 * @EnableTransactionManagement
 * @ServletComponentScan
 * @MapperScan("com.bootdo.*.dao")
 * @SpringBootApplication
 * @EnableCaching
 *
 */

@EnableAutoConfiguration(exclude = {
         org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync
@ServletComponentScan
@ComponentScan(basePackages = {"com.bootdo"})
@MapperScan("com.bootdo.*.dao")
@ConditionalOnClass(server.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class server extends SpringBootServletInitializer {
    private static org.slf4j.Logger logger= LoggerFactory.getLogger(server.class);
    private static int serverport;
    private static String serverip;
    private static String hostAddress;
    private static String serverhost;
    private static String serverid;

    public static ConfigurableApplicationContext ac;

    //private static ExecutorService executorServiceS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    //org.apache.commons.lang3.concurrent.BasicThreadFactory
    private static  ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5,
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
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
        return builder.sources(server.class);
    }

    public static void main(String[] args) throws SSLException, UnknownHostException {
        ac=SpringApplication.run(server.class, args);
//        Environment env = ac.getEnvironment();
//        serverport = Integer.parseInt(env.getProperty("server.port"));
//        hostAddress = InetAddress.getLocalHost().getHostAddress();
//        WechatUtil.init();
//        if (args.length > 0) {
//            String str = args[0];
//            serverport = Integer.parseInt(str);
//            if (serverport > 1000 && serverport < 65000) {
//                WechatUtil.server_port = serverport;
//            }
//        }
//        else if(serverport > 1000 && serverport < 65000){
//            WechatUtil.server_port = serverport;
//        } else {
//            serverport = WechatUtil.server_port;
//        }
//        serverip = WechatUtil.serverIp;
//        serverhost = serverip + ":" + serverport;
//        serverid = getMd5(serverhost);
//        WechatUtil.serverId = serverid;
//        // 启动Grpc客户端
//        IpadApplication.getInstance().init();// uploginedUsers(serverid);
//        uploginedUsers();
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                //IpadApplication.getInstance().destroy();
//                logger.info("-------服务器已关闭,host:[" + serverhost + "] 服务器ID:[" + serverid + "]-------");
//            }
//        });
//        logger.info(logo.logo, env.getProperty("spring.application.name"),
//                serverport, hostAddress, serverport, hostAddress, serverport,
//                serverport, serverip, serverport, serverid);
    }
}



