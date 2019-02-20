package com.wx.service;

import com.google.common.base.Strings;
import com.wx.httpHandler.MyHttpHandler;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceManager {
    private static Logger logger = Logger.getLogger(MyHttpHandler.class);
    private static final ConcurrentHashMap<String, BaseService> serviceMap = new ConcurrentHashMap<String, BaseService>(10000, 0.90f, Runtime.getRuntime().availableProcessors() * 2);
    private static final ScheduledExecutorService serviceMonitor = Executors.newScheduledThreadPool(5);
    private static final ServiceManager instance = new ServiceManager();

    static {
        serviceMonitor.scheduleAtFixedRate(() -> {
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
        return instance;
    }

    public BaseService createService(String randomid, String softwareId, boolean autoLogin, String extraData) {
        BaseService service = serviceMap.get(randomid);
        if (service == null) {
            if (!Strings.isNullOrEmpty(softwareId) && softwareId.equals(ServiceSoftwareId.SOFTWARE_YY.softwareId)) {
                service = new Service_Demo(randomid);
            } else {
                service = new Service_Demo(randomid);
            }

            BaseService finalService = service;
            service.connectToWx(data -> {
                finalService.getQRcode();
            });
            serviceMap.put(randomid, service);
        } else if (service.getState().getCode() <= 0) {
            service.getQRcode();
        }
        service.setAutoLogin(autoLogin);
        service.setExtraData(extraData);
        return service;
    }

    public BaseService createServiceForReLogin(String randomid, String softwareId) {
        BaseService service = serviceMap.get(randomid);
        if (service == null && softwareId != null) {
            if (softwareId.equals(ServiceSoftwareId.SOFTWARE_YY.softwareId)) {
                service = new Service_Demo(randomid);
            } else if (softwareId.equals(ServiceSoftwareId.QLJSF.softwareId)) {
                service = new Service_Demo(randomid);
            }  else if (softwareId.equals(ServiceSoftwareId.MENE.softwareId)) {
                service = new Service_Demo(randomid);
            } else {
                service = new Service_Demo(randomid);
            }
            serviceMap.put(randomid, service);
        }
        return service;
    }

    public BaseService getServiceByRandomId(String randomId) {
        return serviceMap.get(randomId);
    }
}
