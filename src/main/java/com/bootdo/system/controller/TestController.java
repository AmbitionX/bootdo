package com.bootdo.system.controller;

import com.bootdo.common.controller.BaseController;


import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.controller.BaseController;
import com.bootdo.common.utils.R;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
//
//
@Controller
@RequestMapping("/system/test")
@Api(tags={"WebSellerBorrowController"},description = "WEB(B端)-借贷管理")
public class TestController extends BaseController {
//
//    private static Logger _log = LoggerFactory.getLogger(TestController.class);
//
//    /**
//     * 获取用户登录二维码
//     */
//    @ResponseBody
//    @PostMapping(value = "/login_qrcode")
//    public R getLoginQrcode(HttpServletRequest request,String account,String randomId,String softwareId,
//                            Boolean autoLogin,String extraData) {
//        _log.info("###### 开始获取户登录二维码 ######");
//        long startTime = System.currentTimeMillis();
//        String logPrefix = "[获取用登录二维码]";
////        JSONObject po = getJsonParam(request);
////        _log.info("{}请求参数:{}", logPrefix, po);
////        String account = getStringRequired(po,"account");
////        String randomId = getStringRequired(po,"randomId");
////        String softwareId = getStringRequired(po,"softwareId");
////        Boolean autoLogin = getBoolean(po,"autoLogin");
////        String extraData = getString(po,"extraData");
//        boolean isNew;
//        randomId= UUID.randomUUID().toString();
//        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
//        if (service == null) {
//
//            isNew = true;
//            BaseService baseService = ServiceManager.getInstance().createService(randomId, softwareId, autoLogin, extraData);
//            baseService.setSoftwareId(softwareId);
//            baseService.setNew(isNew);
//            baseService.setAccount(account);
//            return R.ok().put("uuid",randomId);
//        }
//        long endTime = System.currentTimeMillis();
//        _log.info("{}randomId:{}, 耗时：{} ms", logPrefix, randomId, endTime - startTime);
//
//        return R.ok().put("uuid",randomId);
////        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
//    }
//
//    /**
//     * 获取用户登录状态
//     */
//    @ResponseBody
//    @RequestMapping(value = "/login_status")
//    public R getLoginState(HttpServletRequest request,String randomId) {
//        _log.info("###### 获取用户登录状态 ######");
//        String logPrefix = "[获取用登录状态]";
////        JSONObject po = getJsonParam(request);
////        _log.info("{}请求参数:{}", logPrefix, po);
////        String randomId = getStringRequired(po, "randomId");
//        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
//        if (service == null) {
////            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
//            return R.error(1002, "null");
//        }
////        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
//        return R.ok().put("status",service.getState());
//    }
//
//    /**
//     * 用户登录
//     */
//    @RequestMapping(value = "/login")
//    public R login(HttpServletRequest request,String randomId) {
//        String logPrefix = "[用户登录]";
////        JSONObject po = getJsonParam(request);
////        _log.info("{}请求参数:{}", logPrefix, po);
////        String randomId = getStringRequired(po, "randomId");
//        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
//        if (service == null) {
////            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
//            return R.error(1002, "null");
//
//        }
//        if (service.getState().code == 2) {
//            service.login();
//            service.getState().code = 6;
//            service.getState().msg = "登录完成.";
//        } else {
//            service.getState().code = 6;
//            service.getState().msg = "已经登录,请勿重复调用接口.";
//        }
////        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
//        return R.ok().put("status",service.getState());
//    }
//
////    /**
////     * 用户退出
////     */
////    @RequestMapping(value = "/logout")
////    public R logout(HttpServletRequest request,String randomId) {
////        String logPrefix = "[用户退出]";
//////        JSONObject po = getJsonParam(request);
//////        _log.info("{}请求参数:{}", logPrefix, po);
//////        String randomId = getStringRequired(po, "randomId");
////        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
////        if (service == null) {
////            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
////        }
////        service.setIsDead(true);    // 退出
//////        return ResponseEntity.ok(DataResponse.buildSuccess(new HttpResult(0, "操作成功")));
////        return R.ok();
////    }
//
////    /**
////     * 微信用户二次登陆
////     */
////    @RequestMapping(value = "/login_again")
////    public ResponseEntity<?> againLogin(HttpServletRequest request) {
////        _log.info("###### 微信用户二次登陆 ######");
////        String logPrefix = "[发起二次登陆]";
////        JSONObject po = getJsonParam(request);
////        _log.info("{}请求参数:{}", logPrefix, po);
////        String randomId = getStringRequired(po, "randomId");
////        String account = getStringRequired(po,"account");
////        String softwareId = getStringRequired(po,"softwareId");
////        BaseService service = ServiceManager.getInstance().loginQrCode(randomId, softwareId);
////        service.setSoftwareId(softwareId);
////        service.setAccount(account);
////        HttpResult httpResult = service.loginAgain(toParam(po));
////        return ResponseEntity.ok(DataResponse.buildSuccess(httpResult));
////    }
//
////    /**
////     * 用户登录
////     */
////    @RequestMapping(value = "/login_pwd")
////    public ResponseEntity<?> loginPwd(HttpServletRequest request) {
////        String logPrefix = "[用户登录]";
////        JSONObject po = getJsonParam(request);
////        _log.info("{}请求参数:{}", logPrefix, po);
////        String randomId = getStringRequired(po, "randomId");
////        String account = getStringRequired(po,"account");
////        String softwareId = getStringRequired(po,"softwareId");
////        String pwd = getStringRequired(po, "pwd");
////        Boolean autoLogin = getBoolean(po,"autoLogin");
////        String extraData = getString(po,"extraData");
////        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
////        if (service == null) {
////            service = ServiceManager.getInstance().createService4pwd(randomId, softwareId, autoLogin, extraData, account, pwd);
////        } else if(service.getState().code == -106) {
////            service.pwdLogin(account, pwd);
////        }
////        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
////    }
//
//    @RequestMapping({"/getQRCode"})
//    @ResponseBody
//    R getQRCode(HttpServletRequest request, HttpServletResponse response) {// 这里是我http请求过来
//        String randomid = request.getParameter("randomid");
//        String account = request.getParameter("account");
//        String softwareId = request.getParameter("softwareId");
//        boolean autoLogin = true;
//        String extraData = request.getParameter("extraData");
//        boolean isNew = false;
//
//        // 这里调用 getQRcode ，拿到二维码， 通过调用这个方法想拿到
//        BaseService baseService = ServiceManager.getInstance().createService(randomid, softwareId, autoLogin, extraData);
//
//        return R.ok().put("data", baseService);
//    }
//
}
