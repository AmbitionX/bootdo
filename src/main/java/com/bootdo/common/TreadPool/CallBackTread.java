package com.bootdo.common.TreadPool;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.enums.EnumParseRecordDetailType;
import com.bootdo.common.enums.EnumWxCmdType;
import com.bootdo.common.utils.JSONUtils;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.common.utils.SpringContextHolder;
import com.bootdo.util.HxHttpClient;
import com.bootdo.wx.domain.ParseRecordDO;
import com.bootdo.wx.domain.ParseRecordDetailDO;
import com.bootdo.wx.service.ParseRecordDetailService;
import com.bootdo.wx.service.ParseRecordService;
import com.bootdo.wx.service.impl.ParseRecordDetailServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wx.demo.common.SpringUtil;
import com.wx.demo.frameWork.protocol.CommonApi;
import com.wx.demo.frameWork.protocol.WechatServiceGrpc;
import com.wx.demo.tools.Constant;
import com.wx.demo.tools.StringUtil;
import com.wx.demo.wechatapi.model.ModelReturn;
import com.wx.demo.wechatapi.model.WechatApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

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
    // 当前执行的用户id
    private String account;


    //链接超时时间
    private static final int CONN_TIME_OUT = 5000;
    //读取超时时间
    private static final int READ_TIME_OUT = 5000;

    ParseRecordDetailService parseRecordDetailService = SpringContextHolder.getBean(ParseRecordDetailService.class);
    ParseRecordService parseRecordService = SpringContextHolder.getBean(ParseRecordService.class);

    // 间隔时间 s
    private int time = 60;

    public CallBackTread(List<String> wxdatas, String parseCode,String account) {
        this.wxdatas = wxdatas;
        this.parseCode = parseCode;
        this.account = account;

    }

    @Override
    public void run() {
        long l=System.currentTimeMillis();
        try {
            String respose = "";
            logger.info("l:{}_com.bootdo.common.TreadPool.CallBackTread.run_CallBackTread---------开始执行批量62登录",l);

            List<ParseRecordDetailDO> parseRecordDetailDOList = Lists.newArrayList();
            try {
                for (String wxStr : wxdatas) {
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
                logger.error("l:{}_com.bootdo.common.TreadPool.CallBackTread.run_CallBackTread_执行批量62数据新建明细失败异常，cause:{},message:{},detail:{}",l,e.getCause(),e.getMessage(),e.toString());
            }
            //执行微信功能
            for (ParseRecordDetailDO parseRecordDetailDO : parseRecordDetailDOList) {
                //组装62数据登录数据
                WechatApi wechatApi = new WechatApi();
                wechatApi.setRandomId(UUID.randomUUID().toString());
                wechatApi.setAccount(this.account);
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

            logger.info("l:{}_com.bootdo.common.TreadPool.CallBackTread.run_CallBackTread---------结束执行批量62登录",l );
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("l:{}_com.bootdo.common.TreadPool.CallBackTread.run_CallBackTread_执行批量62登录失败，cause:{},message:{},detail:{}",l,e.getCause(),e.getMessage(),e.toString());

        }
    }

//    public static void main(String[] args) {
//        Map<String,String> noticeParam = Maps.newHashMap();
//        noticeParam.put("mphoneareaold","0086");
//        noticeParam.put("mphonearea" , "0086");
//        noticeParam.put("mphone","18003711149");
//        noticeParam.put("validateType" , "1");
//        noticeParam.put("pincode" , "731692");
//        noticeParam.put("newbuttonmp" , "确定");
//
//        String borrowCallbackUrl="https://account.chsi.com.cn/account/domodifymobilephone.action";
//
//        try {
//
//            for (int i = 0; i<9999;i++) {
//                String str = "189"+String.format("%04d", i)+"2402";
//                noticeParam.put("oldMobilePhone" , str);
//                String response=post(borrowCallbackUrl, noticeParam);
//            }
//
////            System.out.println(JSONObject.toJSON(noticeParam));
////            System.out.println(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 表单方式请求
//     * @param url            请求的全路径 例如：http://localhost:8088/json.html
//     * @param postParameters 使用post的方式请求的参数，此参数为一个map
//     * @return 返回json字符串
//     */
//    public static String post(String url, Map<String, String> postParameters) {
//        String returnStr = "";
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        try {
//            HttpPost httpPost = new HttpPost(url);
//            /**
//             * setConnectTimeout(20000)：设置连接超时时间，单位毫秒。
//             * setConnectionRequestTimeout(20000) 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的
//             * setSocketTimeout(20000) 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
//             */
//            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectionRequestTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
//            httpPost.setConfig(requestConfig);
//            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//            httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
//            httpPost.setHeader("Accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
//            httpPost.setHeader("Cache-Control", "max-age=0");
//            httpPost.setHeader("Connection", "keep-alive");
//            httpPost.setHeader("Cookie", "JSESSIONID=F7D950CA38448E35E01F5016C13F50B6; Secure; _ga=GA1.3.115431369.1551964763; _gid=GA1.3.1675008777.1553140025; aliyungf_tc=AQAAAFGxVEu3EwAADArDt+6x87vfVSsm; acw_tc=276082a815531400420232991ebdf77d9378e2409b8edb56f6f098813cb8a3; __utmc=39553075; __utmz=39553075.1553140043.2.2.utmcsr=my.chsi.com.cn|utmccn=(referral)|utmcmd=referral|utmcct=/archive/index.jsp; _ga=GA1.4.115431369.1551964763; _gid=GA1.4.1675008777.1553140025; __utma=39553075.115431369.1551964763.1553140043.1553144276.3");
//            httpPost.setHeader("Host", "account.chsi.com.cn");
//            httpPost.setHeader("Origin", "https://account.chsi.com.cn");
//            httpPost.setHeader("Referer", "https://account.chsi.com.cn/account/domodifymobilephone.action");
//            httpPost.setHeader("Upgrade-Insecure-Requests", "1");
//            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
//            /*
//             * 解析传递过来的map参数，将参数解析为键值对(f=xxx)的格式放入到List
//             */
//            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//            Set<String> keySet = postParameters.keySet();
//            for (String key : keySet) {
//                nvps.add(new BasicNameValuePair(key, postParameters.get(key)));
//            }
//            // 此处设置请求参数的编码为 utf-8
//            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
//            HttpResponse response = httpClient.execute(httpPost);
//            /*
//             * 判断HTTP的返回状态码，如果正确继续解析返回的数据
//             */
//            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//HttpStatus.SC_OK=200
//                returnStr = EntityUtils.toString(response.getEntity());
//            } else {
//                returnStr = "{\"code\":\"500\",\"msg\":\"请求地址异常("+response.getStatusLine().getStatusCode()+")\",\"content\":\"\",\"extendData\":\"\"}";
//                logger.info(">>>>>>>httpClient Post 请求地址异常---》url:" + url+",data:"+ JSONUtils.beanToJson(postParameters));
//            }
//        } catch (ClientProtocolException e) {
//            logger.error(">>>>>>>httpClient Post ClientProtocolException 异常---》" + e.getMessage());
//            returnStr = "{\"code\":\"601\",\"msg\":\"请求地址异常\",\"content\":\"\",\"extendData\":\"\"}";
//            logger.info(">>>>>>>httpClient Post ClientProtocolException 异常---》url:" + url+",data:"+JSONUtils.beanToJson(postParameters));
//        } catch (IOException e) {
//            logger.error(">>>>>>>httpClient Post IOException 异常---》" + e.getMessage());
//            returnStr = "{\"code\":\"602\",\"msg\":\"IO异常\",\"content\":\"\",\"extendData\":\"\"}";
//            logger.info(">>>>>>>httpClient Post IOException 异常---》url:" + url+",data:"+JSONUtils.beanToJson(postParameters));
//        }
//        return returnStr;
//    }
}
