package com.wx.demo.wechatapi.distributed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wx.demo.common.BaseResponse;
import com.wx.demo.common.DataResponse;
import com.wx.demo.common.RetEnum;
import com.wx.demo.ctrl.BaseController;
import com.wx.demo.frameWork.protocol.CommonApi;
import com.wx.demo.frameWork.protocol.ServiceManagerDemo;
import com.wx.demo.frameWork.protocol.WechatServiceGrpc;
import com.wx.demo.wechatapi.model.*;
import com.wx.demo.util.MyLog;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.wx.demo.httpHandler.HttpResult;
import com.wx.demo.service.BaseService;
import com.wx.demo.service.ServiceManager;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @version V1.0
 * @Description: 微信接口
 * @date 2019-01-16
 */
@Api(tags = "[分布式] API 接口",position = 299)
@RequestMapping(value = "/api")
@RestController
public class DistributedApi extends BaseController {
    private final MyLog _log = MyLog.getLog(DistributedApi.class);
    private CommonApi commonApi = CommonApi.getInstance();


    @ApiVersion("1")
    @ApiOperation(value = "加载 Api",notes = "用二维码方式登录",position = 1)   //
    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public ResponseEntity<?> getModel(HttpServletRequest request, @RequestBody UtilBase UtilBaseinit
            , @RequestBody UtilUser UtilUserinit, @RequestBody WechatMsg WechatMsginit, @RequestBody WechatApi WechatApiinit) {
        //这里只是为了让Swagger model里加载到内容
        //使用swagger的 @ApiModel注解的时候有个坑 就是必须在controller 使用 @RequestBody 注解 否则无法显示models
        //而且不报错，此时swagger就和 spring 耦合了，而且问题难以排查
        return ResponseEntity.ok(DataResponse.buildSuccess(null));
    }

    @ApiVersion("1")
    @ApiOperation(value = "加载getcode",notes = "用二维码方式登录，获取二维码",position = 1)   //
    @RequestMapping(value = "/getcode", method = RequestMethod.POST)
    public ResponseEntity<?> c(HttpServletRequest request,@RequestBody WechatApi getLoginQrcode) {

//     String randomid, String account, String softwareId, boolean autoLogin, int protocolVer, String extraData
        /**
         * account=asd&softwareId=666&autoLogin=true&extraData=null
         */
        ModelReturn modelReturn = commonApi.execute(getLoginQrcode);

        System.out.println(modelReturn);
        return ResponseEntity.ok(DataResponse.buildSuccess(modelReturn));
    }

    @ApiVersion("1")
    @ApiOperation(value = "62数据登录",notes = "62数据登录",position = 1)   //
    @RequestMapping(value = "/getWxLogin", method = RequestMethod.POST)
    public ResponseEntity<?> getWxLogin(HttpServletRequest request,@ApiParam(name="account",value="用户名") @RequestParam String accountin,
    @ApiParam(name="pwd",value="密码")@RequestParam String pwd,@ApiParam(name="data",value="62")@RequestParam String data) {
        //这里只是为了让Swagger model里加载到内容
        //使用swagger的 @ApiModel注解的时候有个坑 就是必须在controller 使用 @RequestBody 注解 否则无法显示models
        //而且不报错，此时swagger就和 spring 耦合了，而且问题难以排查

        //login(String account,String password,String info)

        JSONArray.parseArray(null);

        String randomid = "123";
        String account = accountin;
        String softwareId = "666";
        Boolean autoLogin = true;
        int protocolVer = 5;
        String extraData = "";
//     String randomid, String account, String softwareId, boolean autoLogin, int protocolVer, String extraData
        /**
         * account=asd&softwareId=666&autoLogin=true&extraData=null
         */
        WechatServiceGrpc service = new WechatServiceGrpc(randomid,account,softwareId,autoLogin,protocolVer);
        service.login(account,pwd,data);

        int loginStatus = service.getLoginStatus();
        System.out.println(loginStatus);
        return ResponseEntity.ok(DataResponse.buildSuccess(null));
    }



    /**
     * 获取用户登录二维码
     * <p>
     * 这个方法是以对象的形式传入
     */
    @ApiVersion("2")
    @ApiOperation(value = "获取登录二维码",notes = "获取一个登录用的二维码",position = 2)   //用来识别文档方法，需要注意的是，被swagger修饰的类里面需要声明每个http入口的method类型，例如post/get等，否则前面文档会乱
    @RequestMapping(value = "/getLoginQrcode", method = RequestMethod.POST)
    public ResponseEntity<?> getLoginQrcode(HttpServletRequest request, @RequestBody WechatApi getLoginQrcode) {
        _log.info("###### 开始获取户登录二维码 ######");
        long startTime = System.currentTimeMillis();
        String logPrefix = "[获取用登录二维码]";
        JSONObject po = getJsonParam(request);
        _log.info("{}请求参数:{}", logPrefix, po);
        String account = getStringRequired(po, "account");
        String randomId = getStringRequired(po, "randomId");
        String softwareId = getStringRequired(po, "softwareId");
        Boolean autoLogin = getBoolean(po, "autoLogin");
        String extraData = getString(po, "extraData");
        boolean isNew;
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            isNew = true;
            BaseService baseService = ServiceManager.getInstance().createService(randomId, softwareId, autoLogin, extraData);
            baseService.setSoftwareId(softwareId);
            baseService.setNew(isNew);
            baseService.setAccount(account);
            return ResponseEntity.ok(DataResponse.buildSuccess(baseService.getState()));
        }
        long endTime = System.currentTimeMillis();
        _log.info("{}randomId:{}, 耗时：{} ms", logPrefix, randomId, endTime - startTime);
        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
    }



    /**
     * 获取用户登录状态
     */
    @ApiVersion("3")
    @ApiOperation(value = "获取用户状态",notes = "查询微信号状态,必须传入 [randomId] ",position = 3)
    @RequestMapping(value = "/getstate", method = RequestMethod.POST)
    public ResponseEntity<?> getState(HttpServletRequest request, @RequestBody WechatApi getState) {
        _log.info("###### 获取用户登录状态 ######");
        String logPrefix = "[获取用登录状态]";
        JSONObject po = getJsonParam(request);
        _log.info("{}请求参数:{}", logPrefix, po);
        String randomId = getStringRequired(po, "randomId");
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
        }
        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
    }

    /**
     * 用户登录
     */

    @ApiVersion("4")
    @ApiOperation(value = "二维码登录",notes = "用二维码方式登录,必须传入 [randomId] ",position = 4)
    @RequestMapping(value = "/getlogin", method = RequestMethod.POST)
    public ResponseEntity<?> getLogin(HttpServletRequest request, @RequestBody WechatApi getLogin) {
        String logPrefix = "[用户登录]";
        JSONObject po = getJsonParam(request);
        _log.info("{}请求参数:{}", logPrefix, po);
        String randomId = getStringRequired(po, "randomId");
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

    /**
     * 用户退出
     */
    @ApiVersion("5")
    @ApiOperation(value = "用户退出",notes = "退出这个微信号,必须传入 [randomId] ",position = 5)
    @RequestMapping(value = "/getlogout", method = RequestMethod.POST)
    public ResponseEntity<?> getLogout(HttpServletRequest request, @RequestBody WechatApi getLogout) {
        String logPrefix = "[用户退出]";
        JSONObject po = getJsonParam(request);
        _log.info("{}请求参数:{}", logPrefix, po);
        String randomId = getStringRequired(po, "randomId");

        request.getParameter("dsfasdf");
        Map<String, String[]> parameterMap = request.getParameterMap();

        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
        }
        service.setIsDead(true);    // 退出
        return ResponseEntity.ok(DataResponse.buildSuccess(new HttpResult(0, "操作成功")));
    }

    /**
     * 微信用户二次登陆
     */
    @ApiVersion("6")
    @ApiOperation(value = "微信用户二次登陆",notes = "离线微信用户重新登录,恢复连接!",position = 6)
    @RequestMapping(value = "/getagainlogin", method = RequestMethod.POST)
    public ResponseEntity<?> getAgainLogin(HttpServletRequest request, @RequestBody WechatApi getAgainLogin) {
        _log.info("###### 微信用户二次登陆 ######");
        String logPrefix = "[发起二次登陆]";
        JSONObject po = getJsonParam(request);
        _log.info("{}请求参数:{}", logPrefix, po);
        String randomId = getStringRequired(po, "randomId");
        String account = getStringRequired(po, "account");
        String softwareId = getStringRequired(po, "softwareId");
        BaseService service = ServiceManager.getInstance().createServiceForReLogin(randomId, softwareId);
        service.setSoftwareId(softwareId);
        service.setAccount(account);
        HttpResult httpResult = service.loginAgain(toParam(po));
        return ResponseEntity.ok(DataResponse.buildSuccess(httpResult));
    }

    /**
     * 用户登录

    @RequestMapping(value = "/login_pwd", method = RequestMethod.POST)
    public ResponseEntity<?> loginPwd(HttpServletRequest request) {
        String logPrefix = "[用户登录]";
        JSONObject po = getJsonParam(request);
        _log.info("{}请求参数:{}", logPrefix, po);
        String randomId = getStringRequired(po, "randomId");
        String account = getStringRequired(po, "account");
        String softwareId = getStringRequired(po, "softwareId");
        String pwd = getStringRequired(po, "pwd");
        Boolean autoLogin = getBoolean(po, "autoLogin");
        String extraData = getString(po, "extraData");
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            service = ServiceManager.getInstance().loginQrCode(randomId, softwareId, autoLogin, extraData, account, pwd);
        } else if (service.getState().code == -106) {
            service.pwdLogin(account, pwd);
        }
        return ResponseEntity.ok(DataResponse.buildSuccess(service.getState()));
    }*/

    /**
     * 获取用户收款二维码
     */
    @ApiVersion("7")
    @ApiOperation(value = "获取用户收款二维码",notes = "请求一个收款二维码,订单号和金额,请写在附加参数里",position = 7)
    @RequestMapping(value = "/getpaymentqrcode", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentQrcode(HttpServletRequest request, @RequestBody WechatApi getPaymentQrcode) {
        _log.info("###### 获取收款二维码 ######");
        long startTime = System.currentTimeMillis();
        String logPrefix = "[获取收款二维码]";
        JSONObject po = getJsonParam(request);
        _log.info("{}请求参数:{}", logPrefix, po);
        String randomId = getStringRequired(po, "randomId");    // 唯一标识
        String orderId = getStringRequired(po, "orderId");      // 订单号,即微信收款备注
        Long amount = getLongRequired(po, "amount");            // 收款金额,单位分
        BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomId);
        if (service == null) {
            return ResponseEntity.ok(BaseResponse.build(RetEnum.RET_COMM_1002));
        }
        //pay_url
        // {"retcode":"0","retmsg":"ok","pay_url":"wxp:\/\/f2f1WKVSi1iQ4tIFZzirrSmC9eS1xm3wxWGh"}
        String paymentStr = service.getf2ffee(String.valueOf(amount), orderId);
        _log.info("{}randomId={}, paymentStr={}", logPrefix, randomId, paymentStr);
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
        _log.info("{}randomId:{}, 耗时：{} ms", logPrefix, randomId, endTime - startTime);

        JSONObject object = new JSONObject();
        object.put("payUrl", payUrl);

        return ResponseEntity.ok(DataResponse.buildSuccess(object));
    }



}
