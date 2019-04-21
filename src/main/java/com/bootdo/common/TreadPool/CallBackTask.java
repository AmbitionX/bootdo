package com.bootdo.common.TreadPool;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.baseinfo.dao.WechatDao;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.common.enums.EnumParseRecordDetailType;
import com.bootdo.common.enums.EnumWxCmdType;
import com.bootdo.common.redis.shiro.RedisManager;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.common.utils.SpringContextHolder;
import com.bootdo.wx.dao.TaskdetailDao;
import com.bootdo.wx.dao.TaskinfoDao;
import com.bootdo.wx.domain.ParseRecordDetailDO;
import com.bootdo.wx.domain.TaskdetailDO;
import com.bootdo.wx.domain.TaskinfoDO;
import com.bootdo.wx.service.ParseRecordDetailService;
import com.bootdo.wx.service.ParseRecordService;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.wx.demo.common.RetEnum;
import com.wx.demo.frameWork.protocol.CommonApi;
import com.wx.demo.tools.Constant;
import com.wx.demo.wechatapi.model.ModelReturn;
import com.wx.demo.wechatapi.model.WechatApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author chenbo-QQ381756915
 * @version 1.0.0
 * @date 创建时间：2018年7月9日 下午6:30:14
 * @parameter
 * @throws
 * @return
 */
public class CallBackTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(CallBackTask.class);

    private static CommonApi commonApi = CommonApi.getInstance();
    // 执行任务的微信号
    private List<WechatDO> wechatList;
    // 准备执行的任务
    private TaskinfoDO taskinfo;

    //链接超时时间
    private static final int CONN_TIME_OUT = 5000;
    //读取超时时间
    private static final int READ_TIME_OUT = 5000;

    TaskdetailDao taskdetailDao = SpringContextHolder.getBean(TaskdetailDao.class);
    WechatDao wechatDao = SpringContextHolder.getBean(WechatDao.class);
    TaskinfoDao taskinfoDao  = SpringContextHolder.getBean(TaskinfoDao.class);

    // 间隔时间 s
    private int time = 60;

    public CallBackTask(List<WechatDO> wechatList, TaskinfoDO taskinfo) {
        this.wechatList = wechatList;
        this.taskinfo = taskinfo;

    }

    @Override
    public void run() {
        int count = taskinfo.getFinishnum(); //成功次数
        int status = taskinfo.getStauts();
        TaskinfoDO tempinfo = new TaskinfoDO();
        tempinfo.setFinishnum(count);
        tempinfo.setId(taskinfo.getId());
            try {
                logger.info("开始执行任务CallBackTask--------->>", JSONObject.toJSONString(taskinfo));
                WechatApi wechatApi = new WechatApi();
                int readNum = 0;
                if (taskinfo.getTasktype().equals(1)) {//阅读
                    for (WechatDO wxid : wechatList) {
                        wechatApi.setRandomId(wxid.getRandomid());
                        wechatApi.setAccount(wxid.getUid().toString());
                        wechatApi.setSoftwareId(Constant.softwareId);
                        wechatApi.setAutoLogin(Constant.autoLogin);
                        wechatApi.setProtocolVer(Constant.protocolVer);
                        wechatApi.setReqUrl(taskinfo.getUrl().trim());
                        wechatApi.setScene(Constant.scene);
                        wechatApi.setUserName(taskinfo.getWxname());
                        wechatApi.setCmd(777);
                        wechatApi.setReadNum(readNum);

                        ModelReturn modelReturn = commonApi.execute(wechatApi);
                        logger.info("---任务返回信息：{}",JSONObject.toJSONString(modelReturn));
                        int flag = 1;
                        if (modelReturn.getCode() != RetEnum.RET_COMM_SUCCESS.getCode()) {
                            flag = 2;
                            readNum = 0;
                        }
                        TaskdetailDO taskdetailDO = new TaskdetailDO();
                        taskdetailDO.setTaskid(taskinfo.getId());
                        taskdetailDO.setUid(wxid.getUid());
                        taskdetailDO.setWxid(wxid.getId());
                        taskdetailDO.setPrice(taskinfo.getPrice());
                        taskdetailDO.setTasktype(taskinfo.getTasktype());
                        taskdetailDO.setStauts(flag); //根据任务执行情况设定
                        taskdetailDO.setParentid(wxid.getParentid());
                        taskdetailDao.save(taskdetailDO);
                        //释放微信号，根据执行成功失败传参
                        relieveStatus(wxid, modelReturn);
                        if (modelReturn.getCode() == RetEnum.RET_COMM_SUCCESS.getCode()) {//记录成功次数
                            count = count + 1;
                            String readNumStr=modelReturn.getRetdata();
                            readNum = Integer.parseInt(readNumStr);
                        }
                        tempinfo.setFinishnum(count);
                        taskinfoDao.update(tempinfo);
                        Thread.sleep(taskinfo.getTaskperiod());
                    }
                } else if (taskinfo.getTasktype().equals(3)) {//关注
                    for (WechatDO wxid : wechatList) {
                        wechatApi.setRandomId(wxid.getRandomid());
                        wechatApi.setAccount(wxid.getUid().toString());
                        wechatApi.setSoftwareId(Constant.softwareId);
                        wechatApi.setAutoLogin(Constant.autoLogin);
                        wechatApi.setProtocolVer(Constant.protocolVer);
                        wechatApi.setScene(Constant.scene30);
                        wechatApi.setUserName(taskinfo.getWxname());
                        wechatApi.setGzwxId(taskinfo.getWxid().trim());
                        wechatApi.setCmd(999);

                        ModelReturn modelReturn = commonApi.execute(wechatApi);
                        int flag = 1;
                        if (modelReturn.getCode() != RetEnum.RET_COMM_SUCCESS.getCode()) {
                            flag = 2;
                        }
                        TaskdetailDO taskdetailDO = new TaskdetailDO();
                        taskdetailDO.setTaskid(taskinfo.getId());
                        taskdetailDO.setUid(wxid.getUid());
                        taskdetailDO.setWxid(wxid.getId());
                        taskdetailDO.setPrice(taskinfo.getPrice());
                        taskdetailDO.setTasktype(taskinfo.getTasktype());
                        taskdetailDO.setStauts(flag); //根据任务执行情况设定
                        taskdetailDO.setParentid(wxid.getParentid());
                        taskdetailDao.save(taskdetailDO);
                        //释放微信号，根据执行成功失败传参
                        relieveStatus(wxid, modelReturn);
                        if (modelReturn != null && modelReturn.getCode() == 0) {//记录成功次数
                            count = count + 1;
                        }
                        tempinfo.setFinishnum(count);
                        taskinfoDao.update(tempinfo);
                        Thread.sleep(taskinfo.getTaskperiod());
                    }
                }
                    //------------------------------ 任务结束，执行任务数量累计、任务状态 -----------------------------
               /*     taskinfo.setFinishnum(count);
                    if (taskinfo.getNum() <= count) {
                        taskinfo.setStauts(5); //已完成
                    } else {
                        taskinfo.setStauts(3); // 未完成
                    }
                    taskinfoDao.update(taskinfo);*/

            } catch (Exception e) {
               /* //更新任务
                if (count > taskinfo.getFinishnum()) { // 有执行任务
                    taskinfo.setFinishnum(count);
                    if (taskinfo.getNum() <= count) { // 已经完成任务
                        taskinfo.setStauts(5);
                    }else {
                        taskinfo.setStauts(3); // 未完成
                    }
                    taskinfoDao.update(taskinfo);
                }
                //释放微信号
                relieveAllForTaskId(taskinfo.getId().toString());*/
                e.printStackTrace();
                logger.error("执行任务失败，任务详情：{}，异常：{}，返回：{}", new Object[]{JSONObject.toJSONString(taskinfo), e});
            } finally {
                //更新任务
                if (count > taskinfo.getFinishnum()) { // 有执行任务
                    taskinfo.setFinishnum(count);
                    if (taskinfo.getNum() <= count) { // 已经完成任务
                        taskinfo.setStauts(5);
                    }else {
                        taskinfo.setStauts(3); // 未完成
                    }
                }else{
                    taskinfo.setStauts(status);
                }
                taskinfoDao.update(taskinfo);
                // 释放任务锁
                RedisManager.del(Constant.prefix_task + taskinfo.getId());
            }
    }


    public void relieveStatus(WechatDO wechatDO, ModelReturn ret){
        Date now = new Date();
        wechatDO.setTaskid(null);  //解除任务绑定
        if(ret.getCode()==RetEnum.RET_COMM_SUCCESS.getCode()) {
            if(!isToday(wechatDO.getLastdate())){// 如果最后一次执行任务不是当天，释放当日执行任务数量
                wechatDO.setTodaytaskquantity(1); //更新当日累计执行任务数量
            }else {
                wechatDO.setTodaytaskquantity(wechatDO.getTodaytaskquantity() + 1); //更新当日累计执行任务数量
            }
            wechatDO.setLastdate(now);  //更新最后一次执行任务时间
            wechatDO.setTotaltaskquantity(wechatDO.getTotaltaskquantity() + 1); //更新累计执行任务数量

        }else{
            if(ret.getCode()==RetEnum.RET_COMM_2001.getCode()){
                wechatDO.setStauts(4);
                wechatDO.setRemark(RetEnum.RET_COMM_2001.getMessage());
            }else if(ret.getCode()==RetEnum.RET_COMM_2002.getCode()){
                wechatDO.setStauts(5);
                wechatDO.setRemark(RetEnum.RET_COMM_2002.getMessage());
            }else if (ret.getCode() == RetEnum.RET_COMM_4444.getCode()){
                wechatDO.setStauts(2);
                wechatDO.setRemark(RetEnum.RET_COMM_4444.getMessage());
            }
            // wechatDO.setStauts(3);
        }
        wechatDao.relieveStatus(wechatDO);
    }

    private static boolean isToday(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if(param.equals(now)){
            return true;
        }
        return false;
    }

    public void relieveAllForTaskId(String taskid){
        wechatDao.relieveAllForTaskId(taskid);
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
