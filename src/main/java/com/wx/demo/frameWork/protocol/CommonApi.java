package com.wx.demo.frameWork.protocol;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.enums.EnumWxCmdType;
import com.bootdo.common.utils.R;
import com.wx.demo.common.RetEnum;
import com.wx.demo.ctrl.BaseController;
import com.wx.demo.util.MyLog;
import com.wx.demo.wechatapi.model.ModelReturn;
import com.wx.demo.wechatapi.model.WechatApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class CommonApi extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CommonApi.class);
    private ServiceManagerDemo grpvcserver = ServiceManagerDemo.getInstance();
    private static final CommonApi INSTANCE = new CommonApi();
    public static CommonApi getInstance() {
        return INSTANCE;
    }
    public ModelReturn execute(WechatApi wechatApi) {
        int cmd=wechatApi.getCmd();
        String account = wechatApi.getAccount();

        String randomid=wechatApi.getRandomId();
        if (randomid == null) {
            randomid = UUID.randomUUID().toString();
        }
        ModelReturn modelReturn = new ModelReturn();
        WechatServiceGrpc service = grpvcserver.getServiceByRandomId(randomid);
        if (service == null){
            if (cmd == 502){
                service = grpvcserver.loginQrCode(wechatApi);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                modelReturn = service.getState();
            }
            if (cmd == 702){
                service = grpvcserver.loginQrCode(wechatApi);

                modelReturn = service.getState();
            }
            if (cmd== 2222){
                service = grpvcserver.wx62Login(wechatApi);
                modelReturn = service.getState();
            }
        }else {
            if (cmd == 6666){// 获取状态
                modelReturn = service.getState();
            }
            if (cmd == 777){// 阅读

                 R ret = service.getReadA8KeyAndRead(wechatApi.getReqUrl(),Integer.parseInt(wechatApi.getScene()),wechatApi.getUsername());
                 String code = ret.get("code").toString();
                 String msg = ret.get("msg").toString();
                 if ("0".equals(code)) {
                     modelReturn.code(RetEnum.RET_COMM_SUCCESS.getCode()).msg(RetEnum.RET_COMM_SUCCESS.getMessage());
                 } else if ("2".equals(code)) {
                     modelReturn.code(RetEnum.RET_COMM_2001.getCode()).msg(RetEnum.RET_COMM_2001.getMessage());
                 } else if ("3".equals(code)) {
                     modelReturn.code(RetEnum.RET_COMM_2002.getCode()).msg(RetEnum.RET_COMM_2002.getMessage());
                 } else {
                     modelReturn.code(RetEnum.RET_COMM_1000.getCode()).msg(RetEnum.RET_COMM_1000.getMessage());
                 }
            }
            if (cmd == 888) {

            }
            if (cmd == 999){ // 关注
                logger.info("999开始关注-------》》"+JSONObject.toJSONString(wechatApi));
                Map<String, String> map = service.contactOperate(wechatApi.getGzwxId(),null,null,1,3);
                logger.info("999关注返回信息------》》" + JSONObject.toJSONString(map));
                if(map!=null){
                    if(map.get("status").equals("0")){ // 成功
                        modelReturn.code(RetEnum.RET_COMM_SUCCESS.getCode()).msg(RetEnum.RET_COMM_SUCCESS.getMessage());

                    }else{
                        modelReturn.code(RetEnum.RET_COMM_3001.getCode()).msg(map.get("remaker"));
                    }
                }
            }
        }
        return modelReturn;
    }


}
