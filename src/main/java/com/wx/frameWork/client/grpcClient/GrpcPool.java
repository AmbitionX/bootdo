package com.wx.frameWork.client.grpcClient;

import com.wx.frameWork.proto.WechatMsg;
import com.wx.tools.ConfigService;
import com.wx.tools.Settings;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GrpcPool {
    private static Logger logger = Logger.getLogger(GrpcPool.class);
    //重连线线程
    private ScheduledExecutorService checkService = Executors.newSingleThreadScheduledExecutor();
    //空闲链接
    private List<GrpcClient> freeConnection = new Vector<>();
    //断开的连接
    private List<GrpcClient> deadConnection = new Vector<>();
    private GrpcClient apiClient;


    //连接全部在使用时，重试获取链接的间隔
    private int watiTimeOut = 500;
    private int totalClientNum = 0;
    private int maxClientNum = 100;

    private static GrpcPool _instance = new GrpcPool();

    public static GrpcPool getInstance() {
        return _instance;
    }

    public int getTotalClientNum() {
        return totalClientNum;
    }
    public synchronized void addClient(int addNum) throws SSLException {
        String[] serverList = Settings.getSet().rpcServerList;
        for (int j = 0; j < addNum; j++) {
            for (int i = 0; i < serverList.length; i++) {
                GrpcClient client = new GrpcClient(serverList[i].split(":")[0], Integer.parseInt(serverList[i].split(":")[1]));
                client.create();
                freeConnection.add(client);
                totalClientNum++;
                apiClient = new GrpcClient("111.231.104.181",12580,true);
                client.create();
            }
        }
    }

    public void init() {
        try {
            addClient(10);
        } catch (SSLException e) {
            e.printStackTrace();
        }
        checkService.scheduleAtFixedRate(() -> {
            try {
                synchronized (deadConnection) {
                    for (GrpcClient client : deadConnection) {
                        client.create();
                        releaseClient(client);
                    }
                    deadConnection.clear();
                }
            } catch (Exception e) {
                logger.info("Grpc重连错误", e);
            }
        }, 2, 2, TimeUnit.SECONDS);

        checkService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.info("Grpc链接总数：" + totalClientNum + "    挂掉的Grpc总数：" + deadConnection.size() + "   空闲的总数：" + freeConnection.size());
            }
        }, 120, 120, TimeUnit.SECONDS);
    }

    public synchronized GrpcClient getClient() {
        GrpcClient client = null;
        try {
            if (freeConnection.size() > 0) {
                client = freeConnection.get(0);
                freeConnection.remove(0);
            } else {
                if (totalClientNum >= maxClientNum) {
                    wait(watiTimeOut);
                } else {
                    try {
                        addClient(10);
                    } catch (SSLException e) {
                        e.printStackTrace();
                    }
                }
                client = getClient();
            }
        } catch (InterruptedException e) {
            logger.info("获取grpc链接失败", e);
        }
        return client;
    }
    public WechatMsg helloapiWechat(WechatMsg msg) {
        return apiClient.getStub().helloWechat(msg);
    }
    public WechatMsg helloWechat(WechatMsg msg) {

        return helloWechat(msg, 0);
    }

    public WechatMsg helloWechat(WechatMsg msg, int tryTime) {
        if (tryTime >= 5) {
            logger.info("GRPC 调用异常!  cmd:" + msg.getBaseMsg().getCmd() + "    msg long head:" + Arrays.toString(msg.getBaseMsg().getLongHead().toByteArray()) + "    msg long payload:" + Arrays.toString(msg.getBaseMsg().getPayloads().toByteArray()));
            return null;
        }
        tryTime++;
        WechatMsg wechatMsg = null;
        GrpcClient client = getClient();
        try {
            wechatMsg = client.getStub().helloWechat(msg);
            releaseClient(client);
        } catch (Exception e) {
            addToDeadConnection(client);
            wechatMsg = helloWechat(msg, tryTime);
        }
        return wechatMsg;
    }

    public synchronized void releaseClient(GrpcClient clent) {
        freeConnection.add(clent);
        notifyAll();
    }

    private void addToDeadConnection(GrpcClient client) {
        synchronized (deadConnection) {
            deadConnection.add(client);
        }
    }

    public synchronized void destroy() {
        for (GrpcClient client : freeConnection) {
            client.close();
        }
    }
}
