package com.bootdo.wx.controller;

import com.bootdo.common.redis.shiro.RedisManager;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.ShiroUtils;
import com.wx.httpHandler.HttpResult;
import com.wx.service.BaseService;
import com.wx.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
/*    @Autowired
    private RedisManager redisManager;
    */

    /**
     * 获取用户登录二维码
     */
    @ResponseBody
    @PostMapping(value = "/login_qrcode")
    public R getLoginQrcode(HttpServletRequest request,String account,String randomId,String softwareId,
                            Boolean autoLogin,String extraData) {
        logger.info("###### 开始获取户登录二维码 ######");
        long startTime = System.currentTimeMillis();
        String logPrefix = "[获取用登录二维码]";
        boolean isNew;
        randomId= UUID.randomUUID().toString();
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            isNew = true;
            BaseService baseService = ServiceManager.getInstance().createService(randomId, softwareId, autoLogin, extraData);
            baseService.setSoftwareId(softwareId);
            baseService.setNew(isNew);
            baseService.setAccount(ShiroUtils.getUserId().toString());
            return R.ok().put("uuid",randomId);
        }
        long endTime = System.currentTimeMillis();
        logger.info("{}randomId:{}, 耗时：{} ms", logPrefix, randomId, endTime - startTime);
        return R.ok().put("uuid",randomId);
    }

    /**
     * 获取用户登录状态
     */
    @ResponseBody
    @RequestMapping(value = "/login_status")
    public R getLoginState(HttpServletRequest request,String randomId) {
        logger.info("###### 获取用户登录状态 ######");
        String logPrefix = "[获取用登录状态]";
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return R.error(1002, "null");
        }
        return R.ok().put("status",service);
    }

    /**
     * 获取阅读需要信息
     */
    @ResponseBody
    @RequestMapping(value = "/getReadReady")
    public R getReadReady(HttpServletRequest request,String randomId,String reqUrl,int scene,String username) {
        logger.info("###### 获取用户登录状态 ######");
        String logPrefix = "[获取用登录状态]";
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return R.error(1002, "用户对应的线程不存在");
        }
        String paymentStr = service.getA8KeyService(reqUrl,scene,username);
        if (StringUtils.isEmpty(paymentStr)) {
            return R.error(1003, "获取阅读需要信息失败");
        }
        return R.ok().put("data",paymentStr);
    }

    /**
     * 获取用户登录状态
     */
    @ResponseBody
    @RequestMapping(value = "/getLoginStatus")
    public R getLoginStatus(HttpServletRequest request,String randomId) {
        logger.info("###### 获取用户登录状态 ######");
        String logPrefix = "[获取用登录状态]";

        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
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
