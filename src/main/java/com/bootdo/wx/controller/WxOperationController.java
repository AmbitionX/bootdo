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
     * 上传62数据文件-txt
     *
     * @return
     */
    @SuppressWarnings("finally")
    @RequestMapping(value = "/upload62", method = RequestMethod.POST)
    @ResponseBody
    public String uploadPhoto(HttpServletRequest request, MultipartFile myfile) {
        String result = "";
        if (myfile!=null && myfile.getSize() > 0) {
            try {
                //String realPath = request.getSession().getServletContext().getRealPath("");
//                PropertiesUtils propertiesUtils = new PropertiesUtils();
//                String realPath = propertiesUtils.getConfig("", "commonFiles");
                result = FileUtils.uploadFile(myfile, "62data", "");
            } catch (Exception e) {
                logger.error("上传62数据失败:" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 解析并处理62数据文件-txt
     *
     * @return
     */
    @SuppressWarnings("finally")
    @RequestMapping(value = "/parse62Data", method = RequestMethod.POST)
    @ResponseBody
    public R parse62Data(HttpServletRequest request, String url) {
        R ret=new R();
        try {
            if (!StringUtils.isEmpty(url)) {
                byte[] b=FileUtils.downloadFile_NoRootPath(url);
                if (b.length != 0) {
                    String data62 = new String(b, Constant.DEFAULT_DECODE);
                    BufferedReader rdr = new BufferedReader(new StringReader(data62));
                    List<String> lines = new ArrayList<String>();
                    try {
                        for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
                            lines.add(line);
                        }
                        rdr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (lines.size()>0) {
                        ret=operationService.batch62DataBusi(lines);
                    }
                }
            }
            //String realPath = request.getSession().getServletContext().getRealPath("");
//                PropertiesUtils propertiesUtils = new PropertiesUtils();
//                String realPath = propertiesUtils.getConfig("", "commonFiles");
        } catch (Exception e) {
            logger.error("解析62数据失败:" + e.getMessage());
            return R.error();
        }
        return ret;
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
