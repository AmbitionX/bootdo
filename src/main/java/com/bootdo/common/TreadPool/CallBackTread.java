package com.bootdo.common.TreadPool;

import com.bootdo.common.enums.EnumParseRecordDetailType;
import com.bootdo.common.enums.EnumWxCmdType;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.common.utils.SpringContextHolder;
import com.bootdo.wx.domain.ParseRecordDO;
import com.bootdo.wx.domain.ParseRecordDetailDO;
import com.bootdo.wx.service.ParseRecordDetailService;
import com.bootdo.wx.service.ParseRecordService;
import com.bootdo.wx.service.impl.ParseRecordDetailServiceImpl;
import com.google.common.collect.Lists;
import com.wx.demo.common.SpringUtil;
import com.wx.demo.frameWork.protocol.CommonApi;
import com.wx.demo.frameWork.protocol.WechatServiceGrpc;
import com.wx.demo.tools.Constant;
import com.wx.demo.tools.StringUtil;
import com.wx.demo.wechatapi.model.ModelReturn;
import com.wx.demo.wechatapi.model.WechatApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author chenbo-QQ381756915
 * @version 1.0.0
 * @date 创建时间：2018年7月9日 下午6:30:14
 * @parameter
 * @throws
 * @return
 */
public class CallBackTread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(CallBackTread.class);

    // 62数据数组
    private List<String> wxdatas;
    // 解析记录code
    private String parseCode;

    //链接超时时间
    private static final int CONN_TIME_OUT = 5000;
    //读取超时时间
    private static final int READ_TIME_OUT = 5000;

    ParseRecordDetailService parseRecordDetailService = SpringContextHolder.getBean(ParseRecordDetailService.class);
    ParseRecordService parseRecordService = SpringContextHolder.getBean(ParseRecordService.class);

    // 间隔时间 s
    private int time = 60;

    public CallBackTread(List<String> wxdatas, String parseCode) {
        this.wxdatas = wxdatas;
        this.parseCode = parseCode;

    }

    @Override
    public void run() {
        try {
            String respose = "";
            logger.info("CallBackTread---------开始执行批量62登录，62数据组：{}", new Object[]{wxdatas});

            String account = String.valueOf(ShiroUtils.getUserId());
            List<ParseRecordDetailDO> parseRecordDetailDOList = Lists.newArrayList();
            try {
                for (String wxStr : wxdatas) {
                    logger.info("CallBackTread---------执行循环，{}", new Object[]{wxdatas});
                    if (StringUtils.isNotBlank(wxStr)) {
                        String[] wxs = wxStr.split("----");
                        if (wxs.length == 3) {
                            String code = wxs[0];
                            String pwd = wxs[1];
                            String data = wxs[2];

                            //保存解析任务明细
                            ParseRecordDetailDO parseRecordDetailDO = new ParseRecordDetailDO();
                            parseRecordDetailDO.setParseCode(this.parseCode);
                            parseRecordDetailDO.setUsername(code);
                            parseRecordDetailDO.setPassword(pwd);
                            parseRecordDetailDO.setWxdata(data);
                            parseRecordDetailDO.setCtime(new Date());
                            parseRecordDetailDO.setState(EnumParseRecordDetailType.NUM_TYPE_ONE.getCode());
                            parseRecordDetailService.save(parseRecordDetailDO);

                            parseRecordDetailDOList.add(parseRecordDetailDO);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                parseRecordService.removeByCode(this.parseCode);
                parseRecordDetailService.removeByCode(this.parseCode);
                logger.error("执行批量62数据新建明细失败，62数据组：{}，异常：{}", new Object[]{wxdatas, e});
            }
            //执行微信功能
            for (ParseRecordDetailDO parseRecordDetailDO : parseRecordDetailDOList) {
                //组装62数据登录数据
                WechatApi wechatApi = new WechatApi();
                wechatApi.setRandomId(UUID.randomUUID().toString());
                wechatApi.setAccount(account);
                wechatApi.setSoftwareId("666");
                wechatApi.setAutoLogin(true);
                wechatApi.setProtocolVer(Constant.DEFAULT_PROTOCOLVER);
                wechatApi.setUserName(parseRecordDetailDO.getUsername());
                wechatApi.setUserPassWord(parseRecordDetailDO.getPassword());
                wechatApi.setWxDat(parseRecordDetailDO.getWxdata());
                wechatApi.setCmd(EnumWxCmdType.NUM_TYPE_2222.getCode());

                //传入微信功能中,用以结束后修改状态
                wechatApi.setInsideBusi(parseRecordDetailDO.getId().toString());

                //执行62数据登录
                CommonApi.getInstance().execute(wechatApi);
            }

            logger.info("CallBackTread---------结束执行批量62登录，62数据组：{}", new Object[]{wxdatas});
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("执行批量62登录失败，62数据组：{}，异常：{}", new Object[]{wxdatas, e});

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
