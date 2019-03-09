/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.wx.demo.wechatapi.criterion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wx.demo.common.BaseResponse;
import com.wx.demo.common.DataResponse;
import com.wx.demo.common.RetEnum;
import com.wx.demo.httpHandler.HttpResult;
import com.wx.demo.service.BaseService;
import com.wx.demo.service.ServiceManager;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
/***
 *
 * @since:swagger-bootstrap-ui 1.0
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2018/08/06 15:27
 */


public class ImageController {





    @GetMapping(value = "/preview",produces = "image/jpeg")
    public void preview( HttpServletRequest request, HttpServletResponse response,
                         @RequestParam @ApiParam(name = "account",  required = true,value = "账户") String account,
                         @RequestParam @ApiParam(name = "randomId", required = false, value = "UUID") String randomId,
                         @RequestParam @ApiParam(name = "autoLogin",  required = false,value = "是否自动登录") Boolean autoLogin,
                         @RequestParam @ApiParam(name = "softwareId", required = true, value = "逻辑ID") String softwareId,
                         @RequestParam @ApiParam(name = "protocolVer",  required = true,value = "协议类型") int protocolVer,
                         @RequestParam @ApiParam(name = "cmd", required = true,value = "接口ID") int cmd,
                         @RequestParam @ApiParam(name = "extraData", required = false, value = "拓展字段") String extraData) throws IOException {



    }

    public ResponseEntity<?> getQrcode(String account,String randomId,Boolean autoLogin,String softwareId,int protocolVer, String extraData) {
        String randomid = randomId;
        if (randomid == null || randomid.equals("")) {
            randomid = UUID.randomUUID().toString();
        }
        BaseService baseService = ServiceManager.getInstance().createService(randomid, softwareId, autoLogin, extraData);
        baseService.setSoftwareId(softwareId);
        baseService.setprotocolVer(protocolVer);
        baseService.setNew(true);
        baseService.setAccount(account);
        return ResponseEntity.ok(DataResponse.buildSuccess(baseService.getState()));
    }
    public ResponseEntity<?> getState(String randomId){
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
        }
        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
    }
    public ResponseEntity<?> getLogin(String randomId){
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
        }
        if (service.getState().code == 2) {
            service.login();
            service.getState().code = 6;
            service.getState().msg = "登录完成.";
        } else {
            service.getState().code = 6;
            service.getState().msg = "已经登录,请勿重复调用接口.";
        }
        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
    }
    public ResponseEntity<?> getLogout(String randomId){
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
        }
        service.setIsDead(true);
        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
    }
    public ResponseEntity<?> getPaymentQrcode(String randomId,String amount,String orderId){
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
        }
        String paymentStr = service.getf2ffee(String.valueOf(amount), orderId);
        if (StringUtils.isEmpty(paymentStr)) {
            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1003));
        }
        JSONObject jsonObject = JSON.parseObject(paymentStr);
        String code = jsonObject.getString("retcode");
        String payUrl = "";
        if ("0".equals(code)) {
            payUrl = jsonObject.getString("pay_url");
        }
        long endTime = System.currentTimeMillis();
        JSONObject object = new JSONObject();
        object.put("payUrl", payUrl);
        return ResponseEntity.ok(DataResponse.buildSuccess(object));
    }
    public ResponseEntity<?> getAgainLogin(HttpServletRequest request,String account,String randomId,Boolean autoLogin,String softwareId,int protocolVer, String extraData) {
        BaseService service = ServiceManager.getInstance().createServiceForReLogin(randomId, softwareId);
        service.setSoftwareId(softwareId);
        service.setAccount(account);
        JSONObject po = getJsonParam(request);
        HttpResult httpResult = service.loginAgain(toParam(po));
        return ResponseEntity.ok(DataResponse.buildSuccess(httpResult));
    }
    public ResponseEntity<?> getUserLogin(int cmd,String account,String randomId,Boolean autoLogin,String softwareId,int protocolVer, String extraData,String username,String paw ,String wxdat) {
        String randomid = randomId;
        if (randomid == null || randomid.equals("")) {
            randomid = UUID.randomUUID().toString();
        }
        BaseService baseService = ServiceManager.getInstance().createServiceForReLogin(randomid, softwareId);
        baseService.setSoftwareId(softwareId);
        baseService.setprotocolVer(protocolVer);
        baseService.setNew(true);
        baseService.setAccount(account);
        baseService.setAutoLogin(autoLogin);
        baseService.setExtraData(extraData);
        return ResponseEntity.ok(DataResponse.buildSuccess(baseService.getState()));
    }

    public HashMap<String, String> toParam(JSONObject jsonObject) {
        HashMap<String, String> param = new HashMap<>();
        for (String k : jsonObject.keySet()) {
            param.put(k, jsonObject.getString(k));
        }
        return param;
    }



    protected JSONObject getJsonParam(HttpServletRequest request) {
        String params = request.getParameter("params");
        if (StringUtils.isNotBlank(params)) {
            return JSON.parseObject(params);
        }
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        JSONObject returnObject = new JSONObject();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name;
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnObject.put(name, value);
        }
        return returnObject;
    }

}



