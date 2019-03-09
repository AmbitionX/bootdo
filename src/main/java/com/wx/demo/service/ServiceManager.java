package com.wx.demo.service;

import com.wx.demo.httpHandler.MyHttpHandler;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

public class ServiceManager {
    private static Logger logger = Logger.getLogger(MyHttpHandler.class);
    private static final ConcurrentHashMap<String, BaseService> serviceMap = new ConcurrentHashMap<String, BaseService>(10000, 0.90f, Runtime.getRuntime().availableProcessors() * 2);
    private static final ScheduledExecutorService SERVICE_MONITOR = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
    private static final ServiceManager INSTANCE = new ServiceManager();

    static {
        SERVICE_MONITOR.scheduleAtFixedRate(() -> {
            try {
                for (Iterator<Map.Entry<String, BaseService>> it = serviceMap.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, BaseService> item = it.next();
                    BaseService service = item.getValue();
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

    public static ServiceManager getInstance() {
        return INSTANCE;
    }

    public BaseService createService(String randomid, String softwareId, boolean autoLogin, String extraData) {
        BaseService service = createServiceForReLogin(randomid,softwareId);
        BaseService finalService = service;
        service.connectToWx(data -> {
            finalService.getQRcode();
        });
        service.setAutoLogin(autoLogin);
        service.setExtraData(extraData);
        return service;
    }
    public BaseService createuserService(String randomid, String softwareId, boolean autoLogin, String extraData) {
        BaseService service = createServiceForReLogin(randomid,softwareId);
        BaseService finalService = service;
        service.connectToWx(data -> {
            finalService.getQRcode();
        });
        service.setAutoLogin(autoLogin);
        service.setExtraData(extraData);
        return service;
    }
    public BaseService createServiceForReLogin(String randomid, String softwareId) {
        BaseService service = serviceMap.get(randomid);
        if (service == null && softwareId != null) {
            if (softwareId.equals(ServiceSoftwareId.SOFTWARE_YY.softwareId)) {
                service = new Service_YY(randomid);
            } else if (softwareId.equals(ServiceSoftwareId.QLJSF.softwareId)) {
                service = new ServiceQLJSF(randomid);
            } else {
                service = new ServiceDemo(randomid);
            }
            serviceMap.put(randomid, service);
        }
        return service;
    }

    public BaseService getServiceByRandomId(String randomId) {
        return serviceMap.get(randomId);
    }
}
