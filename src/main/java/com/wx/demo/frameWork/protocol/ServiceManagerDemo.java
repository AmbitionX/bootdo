package com.wx.demo.frameWork.protocol;

import com.wx.demo.httpHandler.MyHttpHandler;
import com.wx.demo.wechatapi.model.WechatApi;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServiceManagerDemo {
    private static Logger logger = Logger.getLogger(MyHttpHandler.class);
    private static final ConcurrentHashMap<String, WechatServiceGrpc> serviceMap = new ConcurrentHashMap<String, WechatServiceGrpc>(10000, 0.90f, Runtime.getRuntime().availableProcessors() * 2);
    private static final ScheduledExecutorService SERVICE_MONITOR = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
    private static final ServiceManagerDemo INSTANCE = new ServiceManagerDemo();

    static {
        SERVICE_MONITOR.scheduleAtFixedRate(() -> {
            try {
                for (Iterator<Map.Entry<String, WechatServiceGrpc>> it = serviceMap.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, WechatServiceGrpc> item = it.next();
                    WechatServiceGrpc service = item.getValue();
                    if (service.isDead()) {
                        service.exit();
                        it.remove();
                    }
                }
            } catch (Exception e) {
                logger.info(e);
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    public static ServiceManagerDemo getInstance() {
        return INSTANCE;
    }


    public WechatServiceGrpc loginQrCode(WechatApi wechatApi) {
        WechatServiceGrpc service= getServiceByRandomId(wechatApi.getRandomId());
        if (service == null) {
            service = createGrpcService(wechatApi);
        }
        service.getLoginQrcode();
        return service;
    }

    public WechatServiceGrpc readA8KeyAndRead(WechatApi wechatApi) {
        WechatServiceGrpc service= getServiceByRandomId(wechatApi.getRandomId());
        if (service == null) {
            service = createGrpcService(wechatApi);
        }
        service.getLoginQrcode();
        return service;
    }

    public WechatServiceGrpc createGrpcService(WechatApi wechatApi) {
        WechatServiceGrpc service;
            if (wechatApi.getSoftwareId().equals("666")) {
                service = new WechatServiceGrpc(wechatApi);
            }else {
                service = new WechatServiceGrpc(wechatApi);
            }
            serviceMap.put(wechatApi.getRandomId(), service);
        return service;
    }



    public WechatServiceGrpc getServiceByRandomId(String randomId) {
        return serviceMap.get(randomId);
    }

    public Map<String, WechatServiceGrpc> getServiceByRandomMap() {
        return serviceMap;
    }


}
