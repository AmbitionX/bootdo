package com.bootdo.common.TreadPool;

import com.bootdo.common.utils.ShiroUtils;
import com.wx.demo.frameWork.protocol.CommonApi;
import com.wx.demo.frameWork.protocol.WechatServiceGrpc;
import com.wx.demo.tools.StringUtil;
import com.wx.demo.wechatapi.model.ModelReturn;
import com.wx.demo.wechatapi.model.WechatApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author chenbo-QQ381756915
 * @date 创建时间：2018年7月9日 下午6:30:14
 * @version 1.0.0
 * @parameter
 * @throws
 * @return
 */
public class CallBackTread implements Runnable {
    private static Logger logger= LoggerFactory.getLogger(CallBackTread.class);

    // 62数据数组
    private List<String> wxdatas;

    //链接超时时间
    private static final int CONN_TIME_OUT=5000;
    //读取超时时间
    private static final int READ_TIME_OUT=5000;

    // 间隔时间 s
    private int time = 60;

    public  CallBackTread(List<String> wxdatas) {
        this.wxdatas = wxdatas;
    }

    @Override
    public void run() {
        try {
            String respose = "";
            logger.info("CallBackTread---------开始执行批量62登录，62数据组：{}", new Object[]{wxdatas});


            String account = String.valueOf(ShiroUtils.getUserId());
            for(String wxStr:wxdatas){
                logger.info("CallBackTread---------执行循环，{}", new Object[]{wxdatas});
                try{
                    if (StringUtils.isNotBlank(wxStr)) {
                        String[] wxs=wxStr.split("----");
                        if (wxs.length == 3) {
                            String code = wxs[0];
                            String pwd = wxs[1];
                            String data = wxs[2];

//                            WechatServiceGrpc service = new WechatServiceGrpc(UUID.randomUUID().toString(),account,"666",true,3);
//                            service.login(code,pwd,data);

                            WechatApi wechatApi=new WechatApi();
                            wechatApi.setRandomId(UUID.randomUUID().toString());
                            wechatApi.setAccount(account);
                            wechatApi.setSoftwareId("666");
                            wechatApi.setAutoLogin(true);
                            wechatApi.setProtocolVer(3);
                            wechatApi.setUserName(code);
                            wechatApi.setUserPassWord(pwd);
                            wechatApi.setWxDat(data);
                            wechatApi.setCmd(2222);

                            ModelReturn execute = CommonApi.getInstance().execute(wechatApi);
                            System.out.println(execute.getRetdata());
                        }
                    }

//                    respose = HttpClientUtils.postFormBytes(url,JSONObject.toJSONString(map),null, CONN_TIME_OUT, READ_TIME_OUT);
                }catch(Exception e){
                    e.printStackTrace();
                    logger.error("执行批量62登录失败，62数据组：{}，异常：{}", new Object[]{wxdatas,e});

                }

                if("".equals(respose) || respose==null){
                    logger.info("CallBackTread---------执行失败，62数据组：{}", new Object[]{wxdatas});
                }else{
                    break;
                }
            }
            logger.info("CallBackTread---------结束执行批量62登录，62数据组：{}", new Object[]{wxdatas});
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("执行批量62登录失败，62数据组：{}，异常：{}", new Object[]{wxdatas,e});
        }
    }

//    public static void main(String[] args) {
//        Map<String,String> noticeParam = Maps.newHashMap();
//        noticeParam.put("appId","1");
//        noticeParam.put("partnerId" , "1");
//        noticeParam.put("apiVersion" , "1.0");
//        noticeParam.put("returnType",SinaCallbackEnum.WITHDRAW.getCode());
//        noticeParam.put("userId" , "6222550006109001");
//        noticeParam.put("orderId" , "111111111111");
//        noticeParam.put("code" , "S");
//        noticeParam.put("desc" , "成功");
//
//        // 参数排序
//        LinkedHashMap<String, Object> linkedMap = StringUtils.mapSortByKey(noticeParam);
//        //把参数加密md5
//        String md5= MD5(Md5Utils.appendParam(linkedMap)).toLowerCase();
//        noticeParam.put("md5Sign",md5);
//
//        String borrowCallbackUrl="http://58.250.250.46:9999/suixdCallBack";
//        try {
//            System.out.println(JSONObject.toJSON(noticeParam));
//            String response=HttpClientUtils.postFormBytes(borrowCallbackUrl,JSONObject.toJSON(noticeParam).toString(),null, 2000, 10000);
//
//
//            System.out.println(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
