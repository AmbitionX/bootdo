package com.bootdo.wx.controller;

import com.bootdo.common.utils.FileUtils;
import com.bootdo.common.utils.HxFileUtils;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.wx.service.impl.WxOperationServiceImpl;
import com.wx.demo.frameWork.protocol.CommonApi;
import com.wx.demo.frameWork.protocol.ServiceManagerDemo;
import com.wx.demo.frameWork.protocol.WechatServiceGrpc;
import com.wx.demo.service.BaseService;
import com.wx.demo.service.ServiceManager;
import com.wx.demo.tools.Constant;
import com.wx.demo.tools.StringUtil;
import com.wx.demo.wechatapi.model.ModelReturn;
import com.wx.demo.wechatapi.model.WechatApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 微信任务信息
 *
 * @author zcg
 * @email 804188877@qq.com
 * @date 2018-12-27 17:36:15
 */

@Controller
@RequestMapping("/wx/wxoperation")
public class WxOperationController {

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(WxOperationController.class);
    private CommonApi commonApi = CommonApi.getInstance();

    @Autowired
    private WxOperationServiceImpl operationService;

/*    @Autowired
    private RedisManager redisManager;
    */

    /**
     * 获取用户登录二维码
     */
    @ResponseBody
    @PostMapping(value = "/login_qrcode")
    public R getLoginQrcode(HttpServletRequest request,@RequestBody WechatApi getLoginQrcode) {
        logger.info("###### 开始获取户登录二维码 ######");
        long startTime = System.currentTimeMillis();
        String logPrefix = "[获取用登录二维码]";
        boolean isNew;
        getLoginQrcode.setAccount(String.valueOf(ShiroUtils.getUserId()));
        ModelReturn modelReturn = commonApi.execute(getLoginQrcode);

        return R.ok().put("data",modelReturn).put("uuid",getLoginQrcode.getRandomId());
    }

    /**
     * 获取用户登录状态
     */
    @ResponseBody
    @RequestMapping(value = "/login_status")
    public R getLoginState(HttpServletRequest request,@RequestBody WechatApi getLoginQrcode) {
        logger.info("###### 获取用户登录状态 ######");
        String logPrefix = "[获取用登录状态]";
        ModelReturn modelReturn = commonApi.execute(getLoginQrcode);
        return R.ok().put("status",modelReturn.getRetdata());
    }

    /**
     * 获取阅读需要信息
     */
    @ResponseBody
    @RequestMapping(value = "/getReadReady")
    public R getReadReady(HttpServletRequest request,@RequestBody WechatApi getLoginQrcode) {
        logger.info("###### 获取用户登录状态 ######");
        String logPrefix = "[获取用登录状态]";
        ModelReturn modelReturn = commonApi.execute(getLoginQrcode);
//        if (service == null) {
//            return R.error(1002, "用户对应的线程不存在");
//        }
//        String paymentStr = service.getReadA8KeyAndRead(reqUrl,scene,username);
//        if (StringUtils.isEmpty(paymentStr)) {
//            return R.error(1003, "获取阅读需要信息失败");
//        }
        return R.ok().put("data",modelReturn);
    }

    /**
     *
     */
    @ResponseBody
    @RequestMapping(value = "/contactOperateService")
    public R contactOperateService(HttpServletRequest request,String randomId) {
        logger.info("###### 获取用户登录状态 ######");
        String logPrefix = "[获取用登录状态]";
        WechatServiceGrpc service = ServiceManagerDemo.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return R.error(1002, "用户对应的线程不存在");
        }
        try {
            String encrypUserName = "yindongli2018";
            String ticket = "";
            String content = "";
            int type = 1;
            int Scene = 3;
//            service.contactOperateService(encrypUserName, ticket, content, type, Scene);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok();
    }

    /**
     * 获取用户登录状态
     */
    @ResponseBody
    @RequestMapping(value = "/getLoginStatus")
    public R getLoginStatus(HttpServletRequest request,String randomId) {
        logger.info("###### 获取用户登录状态 ######");
        String logPrefix = "[获取用登录状态]";

        WechatServiceGrpc service = ServiceManagerDemo.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return R.error(-1,"未找到相应数据");
        }
        if (true) {
            return R.ok().put("state",service);
        }
//        } else if (uri.equals("getlogin")) {
//            if (service.getState().code == 2) {
//                service.login();
//                service.getState().code = 6;
//                service.getState().msg = "登录完成.";
//            } else {
//                service.getState().code = 6;
//                service.getState().msg = "已经登录,请勿重复调用接口.";
//            }
//            return service.getState();
//        } else if (uri.equals("getlogout")) {
//            logger.info("randomid:" + randomid + " ; 已退出ipad登陆");
//            service.setIsDead(true);
//            return new HttpResult(0, "操作成功");
//        } else {
//            return service.handleHttpRequest(uri, param);
//        }

        if (service == null) {
//            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
            return R.error(1002, "null");
        }
        return R.ok().put("status",service);
    }

//    @ResponseBody
//    @RequestMapping("/setQrCode")
//    public R setQrCode(HttpServletRequest request, HttpServletResponse response){
//        String randomid = request.getParameter("redisKey");
//        String randomids = request.getParameter("redisValue");
//
//        redisManager.set(randomid.getBytes(), randomids.getBytes());
//        return new R();
//
//    }

}
