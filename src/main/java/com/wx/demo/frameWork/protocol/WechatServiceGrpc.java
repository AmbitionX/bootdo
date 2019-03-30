package com.wx.demo.frameWork.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.common.redis.shiro.RedisManager;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.SpringContextHolder;
import com.bootdo.util.HxHttpClient;
import com.bootdo.wx.service.ParseRecordDetailService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.wx.demo.bean.WxLongCallback;
import com.wx.demo.frameWork.client.grpcClient.IpadApplication;
import com.wx.demo.frameWork.proto.BaseMsg;
import com.wx.demo.frameWork.proto.User;
import com.wx.demo.frameWork.proto.WechatMsg;
import com.wx.demo.tools.*;
import com.wx.demo.wechatapi.grpcapi.WechatService;
import com.wx.demo.wechatapi.model.ModelReturn;
import com.wx.demo.wechatapi.model.UtilBase;
import com.wx.demo.wechatapi.model.UtilUser;
import com.wx.demo.wechatapi.model.WechatApi;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Component
public class WechatServiceGrpc implements WechatService {
    private static final Logger logger = LoggerFactory.getLogger(WechatServiceGrpc.class);
    protected ScheduledExecutorService heartBeatExe = Executors.newSingleThreadScheduledExecutor();
    protected ScheduledExecutorService isAlifeCheckSevice = Executors.newSingleThreadScheduledExecutor();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static IpadApplication ipadApplication = IpadApplication.getInstance();
    private String randomid;
    private byte[] sessionKey;
    private String version = "6.7.0";
    private String machineCode = WechatUtil.appTocken;
    private String addMachineCode = WechatUtil.apiTocken;
    private int protocolVer = Constant.protocolVer;
    private String account;
    private String shortServerHost = "short.weixin.qq.com";
    private String longServerHost = "long.weixin.qq.com";
    private List<String> shortServerList;
    private List<String> longServerList;
    private int shortServerIndex;
    private int shortServerCount;
    private String wechatDevideId;
    private String deviceUuid;
    private String deviceName;
    private String deviceType;
    private User loginedUser;
    private WechatApi wechatApi;
    private ModelReturn modelReturn;
    private WechatMsg tempWechatMsg;
    private byte[] notifyKey;
    private byte[] uuid;
    private WechatSocket wechatSocket;
    private int isLogin;
    private String secondUUid = UUID.randomUUID().toString().toUpperCase();

    private final String _prefix_62 ="62706c6973743030d4010203040506090a582476657273696f6e58246f626a65637473592461726368697665725424746f7012000186a0a2070855246e756c6c5f1020";
    private final String _postfix_62 = "5f100f4e534b657965644172636869766572d10b0c54726f6f74800108111a232d32373a406375787d0000000000000101000000000000000d0000000000000000000000000000007f";

    com.bootdo.baseinfo.service.WechatService wechatService = SpringContextHolder.getBean(com.bootdo.baseinfo.service.WechatService.class);
    private ParseRecordDetailService parseRecordDetailService=SpringContextHolder.getBean(ParseRecordDetailService.class);

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    private boolean dead;
    private ConcurrentHashMap<Integer, CountDownLatch> latchMap = new ConcurrentHashMap<>();
    private WechatMsgCallback msgCallback = new WechatMsgCallback() {
        @Override
        public void onWechatMsg(WechatMsg wechatMsg) {
            if (wechatMsg == null) {
                return;
            }

            byte[] resData = wechatMsg.getBaseMsg().getPayloads().toByteArray();
            String result = new String(resData, StandardCharsets.UTF_8);
            if (result.length() <= 0 || result.equals("null")) {
                return;
            }

            List<Map> addContactList = new ArrayList<>();
            List<String> delContactList = new ArrayList<>();
            List<Map> messageList = new ArrayList<>();

            JSONArray jsonArray = JSONArray.parseArray(result);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int msgType = jsonObject.getInteger("MsgType");
                if (msgType == 1) {//文本
                    Map<String,String> map = new HashMap<>();
                    map.put("fromUser",jsonObject.getString("FromUserName"));
                    map.put("toUser",jsonObject.getString("ToUserName"));
                    map.put("msgType","1");
                    map.put("content",jsonObject.getString("Content"));
                    map.put("timeStamp",jsonObject.getString("CreateTime"));
                    map.put("details","");
                    messageList.add(map);
                }

                if (msgType == 3 || msgType == 34 || msgType == 43) {//图片、语音、视频
                    Map<String,String> map = new HashMap<>();
                    map.put("fromUser",jsonObject.getString("FromUserName"));
                    map.put("toUser",jsonObject.getString("ToUserName"));
                    map.put("msgType",String.valueOf(msgType));
                    map.put("content",jsonObject.getString("Content"));
                    map.put("timeStamp",jsonObject.getString("CreateTime"));
                    if (msgType == 3) {
                        map.put("details",downloadImg(jsonObject));
                    }

                    if (msgType == 34) {
                        map.put("details",downloadVoice(jsonObject));

                    }

                    messageList.add(map);
                }

                if (msgType == 2) {
                    Map<String,String> map = new HashMap<>();
                    map.put("alias",jsonObject.getString("Alias"));
                    map.put("userName",jsonObject.getString("UserName"));
                    map.put("nickName",jsonObject.getString("NickName"));
                    map.put("chatroomOwner",jsonObject.getString("ChatRoomOwner"));
                    map.put("remark",jsonObject.getString("Remark"));
                    map.put("signature",jsonObject.getString("Signature"));
                    map.put("provincia",jsonObject.getString("Province"));
                    map.put("city",jsonObject.getString("City"));
                    map.put("stranger",jsonObject.getString("Ticket"));
                    map.put("bigHead",jsonObject.getString("BigHeadImgUrl"));
                    map.put("sex",jsonObject.getIntValue("Sex") + "");
                    addContactList.add(map);
                }

                if (msgType == 4) {
                    delContactList.add(jsonObject.getString("Username"));
                }
            }

            if (addContactList.size() > 0) {
                String path = "/msg/device/syncContact";
                Map<String, String> httpParam = new HashMap<>();
                httpParam.put("randomid", String.valueOf(randomid));
                String strContact = new Gson().toJson(addContactList);
                httpParam.put("list", strContact);
                HttpService.httpRequest(path, httpParam);
            }

            if (delContactList.size() > 0) {
                String path = "/msg/device/delContact";
                Map<String, String> httpParam = new HashMap<>();
                httpParam.put("randomid", randomid + "");
                String strContact = new Gson().toJson(delContactList);
                httpParam.put("list", strContact);
                HttpService.httpRequest(path, httpParam);
            }

            if (messageList.size() > 0) {
                String path = "/msg/device/syncMessage";
                Map<String, String> param = new HashMap<>();
                param.put("randomid", randomid + "");
                String strList = new Gson().toJson(messageList);
                param.put("list", strList);
                HttpService.httpRequest(path, param);
            }
        }
    };
    private String myip = WechatUtil.getRealIp();
    private String softwareId;
    private boolean autoLogin;

    /**
     *
     * @param randomid     微信号唯一识别 id
     * @param account      微信号拥有者
     * @param softwareId   逻辑任务ID
     * @param autoLogin    自动登录开关
     * @param protocolVer  登录协议类型
     */
    public WechatServiceGrpc(String randomid, String account, String softwareId, boolean autoLogin, int protocolVer) {
        this.wechatApi = new WechatApi().randomId(randomid) . account(account) . softwareId(softwareId)
                . autoLogin(autoLogin) . protocolVer(protocolVer);
    init();
    }
    public WechatServiceGrpc(WechatApi wechatApi){
        this.wechatApi=wechatApi;
        init();
    }



    public void init (){
        modelReturn=new ModelReturn().code(-1);
        this.randomid = wechatApi.getRandomId();
        this.softwareId = wechatApi.getSoftwareId();
        this.autoLogin = wechatApi.isAutoLogin();
        this.protocolVer = wechatApi.getProtocolVer();
        this.account = wechatApi.getAccount();
        this.deviceName = WechatUtil.getname(account);
        this.deviceUuid = randomid;
        this.shortServerIndex = 0;
        this.wechatDevideId = WechatUtil.getDeviceid(randomid,0);
        this.deviceType = WechatUtil.getDeviceType(randomid,0);
        this.sessionKey = WechatUtil.sessionKey;
        this.version = WechatUtil.version;
        this.longServerHost = WechatUtil.longServerHost;
        this.shortServerHost = WechatUtil.shortServerHost;
        this.deviceType = WechatUtil.getDeviceType(wechatDevideId,1);
        this.wechatSocket = new WechatSocket(this);
        //附加参数不为空的话
        if (wechatApi.getGrpcBaseMsg()!=null &&wechatApi.getCmd()==702) {
            BaseMsg.Builder builder = BaseMsg.newBuilder();
            try {
                builder = BaseMsg.parseFrom(wechatApi.getGrpcBaseMsg()).toBuilder();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            loginedUser = builder.getUser();
            shortServerHost = builder.getShortHost();
            longServerHost = builder.getLongHost();
            wechatSocket.connect(longServerHost,80);

        }
    }

    /**
     * 发送微信socket心跳
     */
    public void sendWechatHeartbeat() {
        longServerRequest(205,null,false);
    }

    /**
     * 发送微信socket获取新消息
     */
    public void sendWechatNewMsg() {
        longServerRequest(138,null,msgCallback);
    }

    /**
     * grpc接口
     * @param wechatMsg
     * @return
     */
    private WechatMsg helloWechat(WechatMsg wechatMsg,int tryCount) {
        if (tryCount > 3) {
            return null;
        }
        WechatMsg msg;
        try {
            msg = ipadApplication.helloWechat(wechatMsg);
        } catch (Exception e) {
            e.printStackTrace();
            return helloWechat(wechatMsg,++tryCount);
        }

        return msg;
    }

    /**
     * 加人grpc接口
     * @param wechatMsg
     * @return
     */
    private WechatMsg addHelloWechat(WechatMsg wechatMsg,int tryCount) {
        if (tryCount > 3) {
            return null;
        }
        WechatMsg.Builder builder = WechatMsg.newBuilder(wechatMsg)
                .setToken(addMachineCode);
        try {
            builder.mergeFrom(ipadApplication.helloapiWechat(builder.build()));
        } catch (Exception e) {
            e.printStackTrace();
            return addHelloWechat(wechatMsg,++tryCount);
        }
        return builder.build();
    }

    private WechatMsg getWechatMsg(int code, HashMap<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("ProtocolVer",protocolVer);

        WechatMsg.Builder builder = WechatMsg.newBuilder();
        builder.setToken(machineCode);
        builder.setVersion(version);
        int timeStamp = (int)(System.currentTimeMillis()/1000);
        builder.setTimeStamp(timeStamp);
        builder.setIP(myip);
        builder.getBaseMsgBuilder().setCmd(code);
        builder.getBaseMsgBuilder().setShortHost(shortServerHost);
        builder.getBaseMsgBuilder().setLongHost(longServerHost);
        if (loginedUser == null) {
            builder.getBaseMsgBuilder().getUserBuilder().setSessionKey(ByteString.copyFrom(sessionKey));
            builder.getBaseMsgBuilder().getUserBuilder().setDeviceId(wechatDevideId);
            builder.getBaseMsgBuilder().getUserBuilder().setDeviceName(deviceName);
            builder.getBaseMsgBuilder().getUserBuilder().setDeviceType(deviceType);
            loginedUser = builder.getBaseMsgBuilder().getUser();
        } else {
            builder.getBaseMsgBuilder().setUser(loginedUser);
        }

        byte[] payLoad = new Gson().toJson(params).getBytes(StandardCharsets.UTF_8);
        builder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(payLoad));

        //检测二维码
        if (code == 503) {
            builder.getBaseMsgBuilder().setLongHead(tempWechatMsg.getBaseMsg().getLongHead());
            builder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(uuid));
            byte[] notifyKeyDecode = Base64.getDecoder().decode(notifyKey);
            builder.getBaseMsgBuilder().getUserBuilder().setMaxSyncKey(ByteString.copyFrom(notifyKeyDecode));
        }

        return builder.build();
    }

    private WechatMsg addGetWechatMsg(int code,HashMap<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("ProtocolVer",protocolVer);

        WechatMsg.Builder builder = WechatMsg.newBuilder();
        builder.setToken(addMachineCode);
        builder.setVersion(version);
        int timeStamp = (int)(System.currentTimeMillis()/1000);
        builder.setTimeStamp(timeStamp);
        builder.setIP(myip);
        builder.getBaseMsgBuilder().setCmd(code);
        builder.getBaseMsgBuilder().setUser(loginedUser);
        builder.getBaseMsgBuilder().setShortHost(shortServerHost);
        builder.getBaseMsgBuilder().setLongHost(longServerHost);

        byte[] payLoad = new Gson().toJson(params).getBytes(StandardCharsets.UTF_8);
        builder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(payLoad));
        return builder.build();
    }

    private WechatMsg addUserRequest(int code,HashMap<String, Object> params) {
        WechatMsg wechatMsg = addGetWechatMsg(code,params);
        WechatMsg requestMsg = addHelloWechat(wechatMsg,1);
        String url = "http://" + shortServerHost + requestMsg.getBaseMsg().getCmdUrl();
        byte[] resData = HttpService.wechatRequest(url,requestMsg.getBaseMsg().getPayloads().toByteArray());
        if (resData == null && shortServerList != null) {
            if (shortServerIndex >= shortServerList.size()) {
                shortServerIndex = 0;
            }

            if (shortServerCount >= shortServerList.size()) {
                shortServerCount = 0;
                return null;
            }

            shortServerHost = shortServerList.get(shortServerIndex);
            shortServerIndex++;
            shortServerCount++;
            return addUserRequest(code,params);
        }

        shortServerCount = 0;
        if (resData != null && resData[0] == -65) {
            WechatMsg.Builder msgbuilder = wechatMsg.toBuilder();
            msgbuilder.setToken(machineCode);
            msgbuilder.getBaseMsgBuilder().setCmd(-code);
            msgbuilder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(resData));
            return helloWechat(msgbuilder.build(),1);
        }

        return null;
    }

    /**
     * 微信长连接接口
     * @param code
     * @param params
     * @param callback
     */
    private void longServerRequest(int code, HashMap<String, Object> params, WechatMsgCallback callback) {
        WechatMsg wechatMsg = getWechatMsg(code,params);
        WechatMsg requestMsg = helloWechat(wechatMsg,1);
        if (requestMsg == null) {
            return;
        }

        if (code == 502 && tempWechatMsg == null) {
            tempWechatMsg = requestMsg;
        }

        byte[] data = WechatUtil.getBuffers(requestMsg);
        wechatSocket.sendData(data, resData-> {
            if (resData[16] == 126 && resData.length == 47) {//下线或者sessionkey过期
                autoLogin();
                return;
            }

            if (callback != null && resData[16] == -65) {
                int newCode = -code;
                if (code == 1111 || code == 2222) {
                    newCode = -1001;
                }

                if (code == 211) {
                    newCode = -212;
                }

                WechatMsg.Builder builder = requestMsg.toBuilder();
                builder.getBaseMsgBuilder().setCmd(newCode);
                builder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(resData));
                WechatMsg responseMsg = helloWechat(builder.build(),1);
                if (responseMsg == null) {
                    return;
                }

                if (responseMsg.getBaseMsg().getRet() == 0) {
                    User.Builder userBuilder = loginedUser.toBuilder();
                    userBuilder.mergeFrom(responseMsg.getBaseMsg().getUser());
                    loginedUser = userBuilder.build();
                }

                callback.onWechatMsg(responseMsg);
            }
        });
    }

    private String longServerRequest(int code, HashMap<String, Object> params,boolean needResult) {
        WechatMsg wechatMsg = getWechatMsg(code,params);
        WechatMsg requestMsg = helloWechat(wechatMsg,1);
        if (requestMsg == null) {
            return "";
        }

        byte[] data = WechatUtil.getBuffers(requestMsg);
        int reqSeq = WechatUtil.byteArrayToInt(data, 12);
        if (needResult) {
            latchMap.put(reqSeq,new CountDownLatch(1));
        }

        WxLongCallback adapter = new WxLongCallback() {
            @Override
            public void onData(byte[] resData) {
                int tmpReqSeq = WechatUtil.byteArrayToInt(resData, 12);
                if (resData[16] == 126 && resData.length == 47) {//
                    if (latchMap.containsKey(tmpReqSeq)) {
                        latchMap.get(tmpReqSeq).countDown();
                    }
                    return;
                }
                if (resData[16] == -65) {
                    int newCode = -code;
                    if (code == 211) {
                        newCode = -212;
                    }
                    WechatMsg.Builder builder = requestMsg.toBuilder();
                    builder.getBaseMsgBuilder().setCmd(newCode);
                    builder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(resData));
                    WechatMsg responseMsg = helloWechat(builder.build(),1);
                    if (responseMsg != null) {
                        //setResult(new String(responseMsg.getBaseMsg().getPayloads().toByteArray(), StandardCharsets.UTF_8));

                        if (responseMsg.getBaseMsg().getRet() == 0) {
                            User.Builder userBuilder = loginedUser.toBuilder();
                            userBuilder.mergeFrom(responseMsg.getBaseMsg().getUser());
                            loginedUser = userBuilder.build();
                        }
                    }


                    if (latchMap.containsKey(tmpReqSeq)) {
                        latchMap.get(tmpReqSeq).countDown();
                    }
                }
            }
        };

        wechatSocket.sendData(data,adapter);
        if (needResult) {
            if (latchMap.containsKey(reqSeq)) {
                try {
                    latchMap.get(reqSeq).await(3, TimeUnit.SECONDS);
                    return "";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latchMap.remove(reqSeq);
                }
            }
        }

        return "";

    }

    /**
     * 微信短连接接口
     * @param code
     * @param params
     * @return
     */
    private String shortServerRequest(int code, HashMap<String, Object> params) {
        WechatMsg wechatMsg = getWechatMsg(code,params);
        WechatMsg msg = helloWechat(wechatMsg,1);
        if (msg == null) {
            return "";
        }

        if (code == 502 && tempWechatMsg == null) {
            tempWechatMsg = msg;
        }

        String url = "http://" + shortServerHost + msg.getBaseMsg().getCmdUrl();
        byte[] data = HttpService.wechatRequest(url,msg.getBaseMsg().getPayloads().toByteArray());
        if (data == null && shortServerList != null) {
            if (shortServerIndex >= shortServerList.size()) {
                shortServerIndex = 0;
            }

            if (shortServerCount >= shortServerList.size()) {
                shortServerCount = 0;
                return "";
            }

            shortServerHost = shortServerList.get(shortServerIndex);
            shortServerIndex++;
            shortServerCount++;
            return shortServerRequest(code,params);
        }

        shortServerCount = 0;
        String result = "";
        if (data != null && data[0] == -65) {
            int newCode = -code;
            if (code == 1111 || code == 2222) {
                newCode = -1001;
            }

            if (code == 211) {
                newCode = -212;
            }

            WechatMsg.Builder builder = msg.toBuilder();
            builder.getBaseMsgBuilder().setCmd(newCode);
            builder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(data));
            WechatMsg responseMsg = helloWechat(builder.build(),1);
            if (responseMsg == null) {
                return "";
            }

            if (responseMsg.getBaseMsg().getRet() == 0) {
                User.Builder userBuilder = loginedUser.toBuilder();
                userBuilder.mergeFrom(responseMsg.getBaseMsg().getUser());
                loginedUser = userBuilder.build();
            }

            if (code == 109) {// 保存图片
                try {
                    File file = new File(IpadApplication.groupId);
                    if (!file.exists()) {
                        file.mkdir();
                    }

                    file = new File(IpadApplication.groupId + "/" + System.currentTimeMillis() + ".jpg");
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream outStream = new FileOutputStream(file);
                    outStream.write(responseMsg.getBaseMsg().getPayloads().toByteArray());
                    outStream.close();
                    return HttpService.uploadFile(HttpService.baseUrl + "/msg/common/file/upload",String.valueOf(randomid),file);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }

            if (code == 128) {// 保存语音
                try {
                    File file = new File(IpadApplication.groupId);
                    if (!file.exists()) {
                        file.mkdir();
                    }

                    file = new File(IpadApplication.groupId + "/" + System.currentTimeMillis() + ".silk");
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream outStream = new FileOutputStream(file);
                    byte[] bytes = responseMsg.getBaseMsg().getPayloads().toByteArray();
                    byte[] bytes1 = new byte[bytes.length - 1];
                    System.arraycopy(bytes,1,bytes1,0,bytes1.length);
                    outStream.write(bytes1);
                    outStream.close();
                    return HttpService.uploadFile(HttpService.audioUrl + "/audio/transfer/silkToMp3",String.valueOf(randomid),file);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }

            result = new String(responseMsg.getBaseMsg().getPayloads().toByteArray(),StandardCharsets.UTF_8);
        }

        return result;
    }
    public ModelReturn getState() {
        return modelReturn;
    }
    /**
     * 通讯录详情接口
     * @param resData
     * @return
     */
    private String batchContactRequest(byte[] resData) {
        WechatMsg.Builder builder = WechatMsg.newBuilder();
        builder.setToken(machineCode);
        builder.setVersion(version);
        int timeStamp = (int)(System.currentTimeMillis()/1000);
        builder.setTimeStamp(timeStamp);
        builder.setIP(myip);
        builder.getBaseMsgBuilder().setCmd(945);
        builder.getBaseMsgBuilder().setUser(loginedUser);
        builder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(resData));
        WechatMsg msg = helloWechat(builder.build(),1);
        if (msg == null) {
            return "";
        }

        String result = "";
        String url = "http://" + shortServerHost + msg.getBaseMsg().getCmdUrl();
        byte[] data = HttpService.wechatRequest(url,msg.getBaseMsg().getPayloads().toByteArray());
        if (data == null && shortServerList != null) {
            if (shortServerIndex >= shortServerList.size()) {
                shortServerIndex = 0;
            }

            if (shortServerCount >= shortServerList.size()) {
                shortServerCount = 0;
                return "";
            }

            shortServerHost = shortServerList.get(shortServerIndex);
            shortServerIndex++;
            shortServerCount++;
            return batchContactRequest(resData);
        }

        shortServerCount = 0;
        if (data != null && data[0] == -65) {
            WechatMsg.Builder msgBuilder = msg.toBuilder();
            msgBuilder.getBaseMsgBuilder().setCmd(-945);
            msgBuilder.getBaseMsgBuilder().setPayloads(ByteString.copyFrom(data));
            WechatMsg responseMsg = helloWechat(msgBuilder.build(),1);
            if (responseMsg == null) {
                return "";
            }

            if (responseMsg.getBaseMsgOrBuilder().getRet() == 0) {
                User.Builder userBuilder = loginedUser.toBuilder();
                userBuilder.mergeFrom(responseMsg.getBaseMsg().getUser());
                loginedUser = userBuilder.build();
            }

            result = new String(responseMsg.getBaseMsg().getPayloads().toByteArray(),StandardCharsets.UTF_8);
        }

        return result;
    }

    private void autoLoginSuccess(WechatMsg wechatMsg) {
        isLogin = 1;
        String data = new String(wechatMsg.getBaseMsg().getPlayloadextend().toByteArray(),StandardCharsets.UTF_8);
        Map map = new Gson().fromJson(data,Map.class);
        shortServerList = (List<String>)map.get("ShortServerList");
        shortServerList.remove("127.0.0.1");
        longServerList = (List<String>)map.get("LongServerList");

        byte[] bytes = loginedUser.toByteArray();
        String user = new String(bytes,StandardCharsets.ISO_8859_1);
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("user",user);
        dataMap.put("shortServerHost",shortServerHost);
        dataMap.put("longServerHost",longServerHost);
        dataMap.put("loginType",protocolVer + "");

        String path = "/msg/device/returnInfo";
        Map<String, String> httpParam = new HashMap<>();
        httpParam.put("randomid", randomid + "");
        String content = new Gson().toJson(dataMap);
        httpParam.put("content", content);
        HttpService.httpRequest(path, httpParam);

       /* syncToRedis();   // 先不更新db，看看数据有效时长
        syncToMysql();*/
        logger.info(dateFormat.format(new Date())+"-autoLoginSuccess-randomid:"+ randomid);
    }

    private void autoLoginFail(int ret) {
        if (isLogin == -1 && loginedUser == null) {
            return;
        }

        isLogin = -1;
        wechatSocket.close();
        loginedUser = null;
        shortServerList = null;
        longServerList = null;
        shortServerIndex = 0;
        shortServerHost = "short.weixin.qq.com";
        longServerHost = "long.weixin.qq.com";

        String path = "/msg/device/logout";
        Map<String, String> param = new HashMap<>();
        param.put("randomid", String.valueOf(randomid));
        HttpService.httpRequest(path, param);

        path = "/msg/device/returnInfo";
        Map<String, String> httpParam = new HashMap<>();
        httpParam.put("randomid", randomid + "");
        httpParam.put("content", "");
        HttpService.httpRequest(path, httpParam);
        setDead(true);
        logger.info(dateFormat.format(new Date())+"-autoLoginFail-ret:"+ret+"-randomid:"+ randomid);
    }

    public void syncToRedis() {
      //  WechatApi redisBean = wechatApi;
       // RedisUtils.set(loginedUser.getUserame(),randomid);
        RedisManager.hset((Constant.redisk_key_loinged_user + WechatUtil.serverId).getBytes(), wechatApi.getRandomId().getBytes(), WechatApi.serialise(wechatApi));
    }

    public void syncToMysql(){
        //微信数据写入到db
        Map<String,Object> param = Maps.newHashMap();
        param.put("wechat",loginedUser.getUserame());
      //  param.put("stauts",1);
        List<WechatDO> wechats = wechatService.list(param);
        if (wechats.size()<1) { // 新增
            WechatDO wechatDO = new WechatDO();
            wechatDO.setRandomid(this.randomid);
            wechatDO.setSessionkey(loginedUser.getSessionKey().toStringUtf8());
            wechatDO.setDeviceid(loginedUser.getDeviceId());
            wechatDO.setUid(Long.valueOf(this.account));
            wechatDO.setStauts(1);//启用
            wechatDO.setUin(String.valueOf(loginedUser.getUin()));
            wechatDO.setAutoauthkey(loginedUser.getAutoAuthKey().toStringUtf8());
            wechatDO.setCookies(loginedUser.getCookies().toStringUtf8());
            wechatDO.setCurrentsynckey(loginedUser.getCurrentsyncKey().toStringUtf8());
            wechatDO.setDevicename(loginedUser.getDeviceName());
            wechatDO.setDevicetype(loginedUser.getDeviceType());
            wechatDO.setNickname(loginedUser.getNickname().toStringUtf8());
            wechatDO.setWechat(loginedUser.getUserame());
            byte[] bytes = loginedUser.toByteArray();
            wechatDO.setUsername(Base64Utils.encodeToString(bytes));
            String data62 = _prefix_62+WechatUtil.strTo16(loginedUser.getDeviceId())+_postfix_62;
            wechatDO.setData62(data62);

            wechatDO.setUserext(String.valueOf(loginedUser.getUserExt()));
            wechatService.save(wechatDO);
        }else { // 更新 或者 新增
            WechatDO wechatDO=null;
            WechatDO wechatDO2=null;
            for(WechatDO wechatDOTemp1:wechats){ // 账户下有没有这个微信号
                if(Long.toString(wechatDOTemp1.getUid()).equals(this.account)) {
                    wechatDO = wechatDOTemp1;
                    break;
                }
            }
            if(wechatApi.getCmd()==2222) {// 62数据需要判断
                for (WechatDO wechatDOTemp2 : wechats) {// 更新时间最近的一条数据
                    if (wechatDO2 == null) {
                        wechatDO2 = wechatDOTemp2;
                    } else {
                        if (wechatDO2.getModifydate().compareTo(wechatDOTemp2.getModifydate()) < 0)
                            wechatDO2 = wechatDOTemp2;
                    }
                }
                Date now = new Date();
                long a = (now.getTime() - wechatDO2.getModifydate().getTime()) / (60 * 60 * 1000) % 24;
                if (a < 1) { // 如果更新时间最近的一条数据更新时间不到1小时，则认为该账号被挤下线，归属最后更新的那个账户
                    wechatDO = wechatDO2;
                }
            }

            if(wechatDO!=null) { //更新
                wechatDO.setRandomid(this.randomid);
                wechatDO.setSessionkey(loginedUser.getSessionKey().toStringUtf8());
                wechatDO.setDeviceid(loginedUser.getDeviceId());
                if(wechatApi.getCmd()!=2222) {
                    wechatDO.setUid(Long.valueOf(this.account));
                }
                wechatDO.setStauts(1);//启用
                wechatDO.setUin(String.valueOf(loginedUser.getUin()));
                wechatDO.setAutoauthkey(loginedUser.getAutoAuthKey().toStringUtf8());
                wechatDO.setCookies(String.valueOf(loginedUser.getCookies()));
                wechatDO.setCurrentsynckey(loginedUser.getCurrentsyncKey().toStringUtf8());
                wechatDO.setDevicename(loginedUser.getDeviceName());
                wechatDO.setDevicetype(loginedUser.getDeviceType());
                wechatDO.setNickname(loginedUser.getNickname().toStringUtf8());
                byte[] bytes = loginedUser.toByteArray();
                wechatDO.setUsername(Base64Utils.encodeToString(bytes));
                wechatDO.setUserext(String.valueOf(loginedUser.getUserExt()));

                wechatService.update(wechatDO);
            }else {
                wechatDO = new WechatDO();
                wechatDO.setRandomid(this.randomid);
                wechatDO.setSessionkey(loginedUser.getSessionKey().toStringUtf8());
                wechatDO.setDeviceid(loginedUser.getDeviceId());
                wechatDO.setUid(Long.valueOf(this.account));
                wechatDO.setStauts(1);//启用
                wechatDO.setUin(String.valueOf(loginedUser.getUin()));
                wechatDO.setAutoauthkey(loginedUser.getAutoAuthKey().toStringUtf8());
                wechatDO.setCookies(loginedUser.getCookies().toStringUtf8());
                wechatDO.setCurrentsynckey(loginedUser.getCurrentsyncKey().toStringUtf8());
                wechatDO.setDevicename(loginedUser.getDeviceName());
                wechatDO.setDevicetype(loginedUser.getDeviceType());
                wechatDO.setNickname(loginedUser.getNickname().toStringUtf8());
                wechatDO.setWechat(loginedUser.getUserame());
                byte[] bytes = loginedUser.toByteArray();
                wechatDO.setUsername(Base64Utils.encodeToString(bytes));
                String data62 = _prefix_62+WechatUtil.strTo16(loginedUser.getDeviceId())+_postfix_62;
                wechatDO.setData62(data62);
                wechatDO.setUserext(String.valueOf(loginedUser.getUserExt()));
                wechatService.save(wechatDO);
            }
        }
    }

    private void login62Callback(WechatMsg wechatMsg, Boolean isSuccess) {
        //拿到对应的值
        Map<String, Object> data = Maps.newHashMap();
        data.put("detailId", wechatApi.getInsideBusi());
        data.put("isSuccess", isSuccess);
        data.put("wxid", wechatMsg.getBaseMsg().getUser().getUserame());
        //执行回调
        parseRecordDetailService.callbackRecordDetail(data);
    }

    private void loginSuccess(WechatMsg wechatMsg) {

        isLogin = 1;
        String data = new String(wechatMsg.getBaseMsg().getPlayloadextend().toByteArray(),StandardCharsets.UTF_8);
        Map map = new Gson().fromJson(data,Map.class);
        shortServerList = (List<String>)map.get("ShortServerList");
        shortServerList.remove("127.0.0.1");
        longServerList = (List<String>)map.get("LongServerList");
        shortServerHost = wechatMsg.getBaseMsg().getShortHost();
        longServerHost = wechatMsg.getBaseMsg().getLongHost();

        logger.info("6262626262626262============"+loginedUser.getDeviceId());
        wechatApi.setWxDat(loginedUser.getDeviceId());
        byte[] bytes = wechatMsg.getBaseMsg().toByteArray();
        wechatApi.setGrpcBaseMsg(bytes);
        wechatApi.setRandomId(randomid);
        wechatApi.setSoftwareId(softwareId);
        wechatApi.setAutoLogin(autoLogin);
        wechatApi.setAccount(account);
        wechatApi.setProtocolVer(protocolVer);
        wechatApi.setWxId(loginedUser.getUserame());
//        try {
//            User a=User.parseFrom(retBytes);
//            ExtraData extraData=new ExtraData();
//            extraData.setLongServerHost(longServerHost);
//            extraData.setShortServerHost(shortServerHost);
//            extraData.setLoginType(protocolVer+"");
//            extraData.setData(user);
////            System.out.println(a);
////            System.out.println(a.toBuilder().toString());
//            WechatServiceGrpc serviceGrpc = new WechatServiceGrpc(randomid,account,softwareId,autoLogin,protocolVer,JSONUtils.beanToJson(extraData));
////            https://mp.weixin.qq.com/s/ExMUO0KuWwNyqdfgIwTw8A&scene=7&username=yindongli2018
//
//            System.out.println(serviceGrpc.getReadA8KeyAndRead("https://mp.weixin.qq.com/s/JaQ8NUw-GZM4_QgDeYqnUw", 7, "yindongli2018"));
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }

        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("shortServerHost",shortServerHost);
        dataMap.put("longServerHost",longServerHost);
        dataMap.put("loginType",protocolVer + "");
        String path = "/msg/device/returnInfo";
        Map<String, String> httpParam = new HashMap<>();
        httpParam.put("randomid", randomid + "");
        String content = new Gson().toJson(dataMap);
        httpParam.put("content", content);
        HttpService.httpRequest(path, httpParam);

        path = "/msg/device/login";
        Map<String, String> param = new HashMap<>();
        param.put("randomid", String.valueOf(randomid));
        HttpService.httpRequest(path, param);

        syncToRedis();
        syncToMysql();
        logger.info(dateFormat.format(new Date())+"-loginSuccess-randomid:"+ randomid);
    }

    private void loginFail(int ret) {
        isLogin = -1;
        wechatSocket.close();
        loginedUser = null;
        shortServerList = null;
        longServerList = null;
        shortServerIndex = 0;
        shortServerHost = "short.weixin.qq.com";
        longServerHost = "long.weixin.qq.com";

        String path = "/msg/device/loginFail";
        Map<String, String> param = new HashMap<>();
        param.put("randomid", String.valueOf(randomid));
        param.put("status", String.valueOf(ret));
        HttpService.httpRequest(path, param);

        logger.info(dateFormat.format(new Date())+"-loginFail-ret:"+ret+"-randomid:"+ randomid);
    }

    @Override
    public void getModel(UtilBase UtilBaseinit, UtilUser UtilUserinit, com.wx.demo.wechatapi.model.WechatMsg WechatMsginit, WechatApi WechatApiinit) {

    }

    @Override
    public void autoLogin() {

        if (loginedUser == null) {
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("UUid", deviceUuid);
        map.put("DeviceType", deviceType);
        map.put("DeviceName", deviceName);
        map.put("ProtocolVer",protocolVer);

        longServerRequest(702, map, wechatMsg ->  {
            if (wechatMsg.getBaseMsg().getRet() == 0) {
                User user = wechatMsg.getBaseMsgOrBuilder().getUser();
                if (user.getAutoAuthKey() == null || user.getSessionKey() == null) {//wx返回的数据不全
                    autoLogin();
                    return;
                }

                autoLoginSuccess(wechatMsg);
            } else if (wechatMsg.getBaseMsg().getRet() == -301) {
                longServerHost = wechatMsg.getBaseMsg().getLongHost();
                wechatSocket.close();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        wechatSocket.connect(longServerHost,80);
                    }
                }).start();
            } else {//登录失败
                logger.error("--------账号下线原因：》》 "+wechatMsg.getBaseMsg().getPayloads().toStringUtf8());
                setDead(true);
                autoLoginFail(wechatMsg.getBaseMsg().getRet());
            }
        });
    }

    @Override
    public void getLoginQrcode() {
        protocolVer = Constant.protocolVer;
        String result = shortServerRequest(502,null);
        if (result.length() <= 0) {
            return;
        }

        Map map = new Gson().fromJson(result,Map.class);
        int status = (int)Float.parseFloat(map.get("Status").toString());
        if (status == 0) {
            String qrcode = map.get("ImgBuf").toString();
            String path = "/msg/device/sendQrcode";
            Map<String, String> param = new HashMap<>();
            param.put("randomid", randomid + "");
            param.put("qrcode", "data:image/png;base64,"+qrcode);
            //param.put("Uuid", randomid + "");
            modelReturn.code(0)
                    .msg("获取二维码成功")
                    .account(account)
                    .cmd(502)
                    .result(true)
                    .retdata("data:image/png;base64,"+qrcode);
            HttpService.httpRequest(path, param);

            notifyKey = map.get("NotifyKey").toString().getBytes();
            uuid = map.get("Uuid").toString().getBytes();
            checkQrcode();
        }
    }

    @Override
    public void checkQrcode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 100;
                while (count > 0) {

                    count--;
                    try {
                        Thread.sleep(2000);

                        String result = shortServerRequest(503,null);
                        Map map = new Gson().fromJson(result,Map.class);
                        map.put("randomid", randomid + "");
                        int status = (int)Float.parseFloat(map.get("Status").toString());
                        modelReturn.code(status)
                                .retdata(GsonUtil.GsonString(map));

                        if (status == 2) {//扫码授权
                            String userName = map.get("Username").toString();
                            String password = map.get("Password").toString();
                            modelReturn.msg("扫码授权");

                            qrcodeLogin(userName,password,"");
                            break;
                        }

                        if (status == -2007) {//过期
                            String path = "/msg/device/expireQrcode";
                            Map<String, String> param = new HashMap<>();
                            param.put("randomid", randomid + "");
                            modelReturn.msg("过期");
                            setDead(true);
                            HttpService.httpRequest(path, param);
                            break;
                        }

                        if (status == 4) {//取消
                            String path = "/msg/device/cancelQrcode";
                            Map<String, String> param = new HashMap<>();
                            param.put("randomid", randomid + "");
                            modelReturn.msg("取消");
                            setDead(true);
                            HttpService.httpRequest(path, param);
                            break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        String qrCode = modelReturn.getRetdata();
    }

    @Override
    public void qrcodeLogin(String userName, String pass,String headUrl) {
        wechatSocket.connect(longServerHost,80);
        modelReturn.code(2);
        HashMap<String, Object> param = new HashMap<>();
        param.put("ProtocolVer",protocolVer);
        param.put("Username", userName);
        param.put("PassWord", pass);
        param.put("UUid", deviceUuid);
        param.put("DeviceType", deviceType);
        param.put("DeviceName", deviceName);
        param.put("language", "zh-cn");
        param.put("realCountry", "cn");

        longServerRequest(1111, param, new WechatMsgCallback() {
            @Override
            public void onWechatMsg(WechatMsg wechatMsg) {
                modelReturn.code(wechatMsg.getBaseMsg().getRet())
                .retdata(wechatMsg.getBaseMsg().getPayloads().toStringUtf8());
                if (wechatMsg.getBaseMsg().getRet() == 0) {//成功
                    modelReturn.code(3)
                            .retdata(wechatMsg.getBaseMsg().getPlayloadextend().toStringUtf8());
                    loginSuccess(wechatMsg);
                    initContact();
                } else if (wechatMsg.getBaseMsg().getRet() == -301) {//重定向
                    longServerHost = wechatMsg.getBaseMsg().getLongHost();
                    wechatSocket.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            qrcodeLogin(userName,pass,"");
                        }
                    }).start();
                } else {//登录失败
                    setDead(true);
                    loginFail(wechatMsg.getBaseMsg().getRet());
                }
            }
        });
    }

    @Override
    public void wxdataLogin(String user, String pass, String wxdata) {
        protocolVer = Constant.protocolVer;
        wechatSocket.connect(longServerHost,80);
        wechatDevideId = wxdata;

        HashMap<String, Object> param = new HashMap<>();
        param.put("Username", user);
        param.put("PassWord", pass);
        param.put("UUid", deviceUuid);
        param.put("DeviceType", deviceType);
        param.put("DeviceId", wxdata);
        param.put("DeviceName", deviceName);
        param.put("ProtocolVer",protocolVer);

        longServerRequest(2222, param, new WechatMsgCallback() {
            @Override
            public void onWechatMsg(WechatMsg wechatMsg) {

                logger.info("----->>62数据登录返回---->>user:{},msg:{}",user,wechatMsg.getBaseMsg().getPayloads().toStringUtf8());

                modelReturn.code(wechatMsg.getBaseMsg().getRet()).msg(wechatMsg.getBaseMsg().getPayloads().toStringUtf8());
                if (wechatMsg.getBaseMsg().getRet() == 0) {
                    login62Callback(wechatMsg,Boolean.TRUE);
                    loginSuccess(wechatMsg);
//                  initContact();
                } else if (wechatMsg.getBaseMsg().getRet() == -301) {//重定向
                    longServerHost = wechatMsg.getBaseMsg().getLongHost();
                    wechatSocket.close();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            wxdataLogin(user,pass,wxdata);
                        }
                    }).start();
                } else {//登录失败
                    login62Callback(wechatMsg,Boolean.FALSE);
                    loginFail(wechatMsg.getBaseMsg().getRet());
                }
            }
        });
    }

    @Override
    public void initContact() {
        String result = shortServerRequest(1002,null);
        if (!result.isEmpty()) {
            JSONArray jsonArray = JSONArray.parseArray(result);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int msgType = jsonObject.getInteger("MsgType");
                if (msgType == 101) {//登录的微信号信息
                    String alias = jsonObject.getString("Alias");
                    String userName = jsonObject.getString("UserName");
                    String nickName = jsonObject.getString("NickName");
                    String signature = jsonObject.getString("Signature");
                    String bindEmail = jsonObject.getString("BindEmail");
                    String bindMobile = jsonObject.getString("BindMobile");
                    String bindUin = jsonObject.getString("BindUin");

                    String path = "/msg/device/uploadUserName";
                    Map<String, String> param = new HashMap<>();
                    param.put("randomid", randomid + "");
                    param.put("email", bindEmail);
                    param.put("qq", "");
                    param.put("phoneNumber", bindMobile);
                    param.put("uin", bindUin);
                    param.put("userName", userName);
                    param.put("nickName", nickName);
                    param.put("alias", alias);
                    param.put("signature", signature);
                    param.put("province",jsonObject.getString("Province"));
                    param.put("city",jsonObject.getString("City"));
                    param.put("sex",jsonObject.getIntValue("Sex") + "");

                    HttpService.httpRequest(path, param);
                }

                if (msgType == 35) {//头像
                    int imgType = jsonObject.getInteger("ImgType");
                    if (imgType == 1) {
                        String headUrl = jsonObject.getString("BigHeadImgUrl");
                        String path = "/msg/device/uploadHeadUrl";
                        Map<String, String> param = new HashMap<>();
                        param.put("randomid", randomid + "");
                        param.put("headUrl", headUrl);
                        HttpService.httpRequest(path, param);
                    }
                }
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int continueFlag = 1;
                long currentWxcontactSeq = 0;
                long currentChatRoomContactSeq = 0;

                while (continueFlag != 0) {
                    HashMap<String, Object> param = new HashMap<>();
                    param.put("CurrentWxcontactSeq", currentWxcontactSeq);
                    param.put("CurrentChatRoomContactSeq", currentChatRoomContactSeq);

                    String result = shortServerRequest(851,param);
                    if (result.isEmpty()) {
                        break;
                    }

                    JSONObject resultObject = JSONObject.parseObject(result);
                    currentWxcontactSeq = resultObject.getIntValue("CurrentWxcontactSeq");
                    currentChatRoomContactSeq = resultObject.getIntValue("CurrentChatRoomContactSeq");
                    continueFlag = resultObject.getIntValue("ContinueFlag");

                    String nextResult = batchContactRequest(result.getBytes());
                    if (nextResult.length() > 0) {
                        List<Map> mapList = new ArrayList<>();
                        JSONArray jsonArray = JSONArray.parseArray(nextResult);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Map<String,String> map = new HashMap<>();
                            map.put("alias",jsonObject.getString("Alias"));
                            map.put("userName",jsonObject.getString("UserName"));
                            map.put("nickName",jsonObject.getString("NickName"));
                            map.put("chatroomOwner",jsonObject.getString("ChatRoomOwner"));
                            map.put("remark",jsonObject.getString("Remark"));
                            map.put("signature",jsonObject.getString("Signature"));
                            map.put("provincia",jsonObject.getString("Province"));
                            map.put("city",jsonObject.getString("City"));
                            map.put("stranger",jsonObject.getString("Ticket"));
                            map.put("bigHead",jsonObject.getString("BigHeadImgUrl"));
                            map.put("sex",jsonObject.getIntValue("Sex") + "");
                            mapList.add(map);
                        }

                        if (mapList.size() > 0) {
                            String path = "/msg/device/syncContact";
                            Map<String, String> httpParam = new HashMap<>();
                            httpParam.put("randomid", randomid + "");
                            String strContact = new Gson().toJson(mapList);
                            httpParam.put("list", strContact);
                            HttpService.httpRequest(path, httpParam);
                        }

                    }
                }
            }
        }).start();
    }

    @Override
    public void deleteDevice() {
        if (isLogin != 1) {
            return;
        }

        if (protocolVer == 1) {
            shortServerRequest(282,null);
        }

        if (protocolVer == 2) {
            autoLoginFail(0);
        }

        logger.info(dateFormat.format(new Date())+"-deleteDevice-randomid:"+ randomid);
    }

    @Override
    public void logout() {
        if (protocolVer == 1) {
            shortServerRequest(282,null);
        }

        if (protocolVer == 2) {
            autoLoginFail(0);
        }

        logger.info(dateFormat.format(new Date())+"-logout-randomid:"+ randomid);
    }

    @Override
    public void login(String account,String password,String info) {
        wxdataLogin(account,password,info);
    }

    @Override
    public int getLoginStatus() {
        return isLogin;
    }

    @Override
    public void resetPassword(String oldPass, String newPass) {
        if (protocolVer != 2) {
            return;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("Pwd1", WechatUtil.getMd5(oldPass));
        String result = shortServerRequest(384,params);
        HashMap<String,Object> map = new HashMap<>();
        map.put("Pwd", WechatUtil.getMd5(newPass));
        map.put("Ticket", result);
        map.put("TicketType", 1);
        longServerRequest(383, map, new WechatMsgCallback() {
            @Override
            public void onWechatMsg(WechatMsg wechatMsg) {
                if (wechatMsg.getBaseMsg().getRet() == 0) {
                    String path = "/msg/device/dataLoginReport";
                    Map<String, String> httpParam = new HashMap<>();
                    httpParam.put("randomid", randomid + "");
                    httpParam.put("newPass", newPass);
                    HttpService.httpRequest(path, httpParam);
                }
            }
        });
    }

    @Override
    public void uploadMobileContact(String mobile, String contactList) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("Opcode", 1);
        params.put("Mobile", mobile);
        params.put("MobileList", contactList);
        shortServerRequest(133,params);
    }

    @Override
    public String downloadImg(JSONObject jsonObject) {
        String content = jsonObject.getString("Content");
        int index = content.indexOf("<?xml");
        if (index < 0) {
            return "";
        }

        content = content.substring(index);
        try {
            Document document = DocumentHelper.parseText(content);
            Element root = document.getRootElement();
            Element element = root.element("img");
            Attribute attribute = element.attribute("length");
            if (attribute == null) {
                return "";
            }

            String length = attribute.getValue();
            HashMap<String, Object> params = new HashMap<>();
            params.put("MsgId", jsonObject.getLongValue("MsgId"));
            params.put("ToUsername", jsonObject.getString("ToUsername"));
            params.put("TotalLen", Integer.valueOf(length));
            params.put("StartPos", 0);
            params.put("DataLen", Integer.valueOf(length));
            params.put("CompressType", 0);
            String result = shortServerRequest(109,params);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public String downloadVoice(JSONObject jsonObject) {
        String content = jsonObject.getString("Content");
        try {
            Document document = DocumentHelper.parseText(content);
            Element root = document.getRootElement();
            Element element = root.element("voicemsg");
            Attribute attribute = element.attribute("length");
            if (attribute == null) {
                return "";
            }
            String length = attribute.getValue();

            attribute = element.attribute("clientmsgid");
            if (attribute == null) {
                return "";
            }
            String clientmsgid = attribute.getValue();

            HashMap<String, Object> params = new HashMap<>();
            params.put("MsgId", jsonObject.getLongValue("MsgId"));
            params.put("StartPos", 0);
            params.put("DataLen", Integer.valueOf(length));
            params.put("ClientMsgId", clientmsgid);
            String result = shortServerRequest(128,params);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private WechatMsg searchContact(String userName) {
        if (shortServerList != null) {
            shortServerIndex++;
            if (shortServerIndex >= shortServerList.size()) {
                shortServerIndex = 0;
            }
            shortServerHost = shortServerList.get(shortServerIndex);
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        return addUserRequest(106,params);
    }

    @Override
    public Map<String, String> addUser(String username, int type, String helloContent,int at) {
        Map<String, String> resultMap = new HashMap<>();
        if (type == 1) {
            WechatMsg wechatMsg = searchContact(username);
            if (wechatMsg == null) {
                resultMap.put("userName","");
                resultMap.put("status","1");
                resultMap.put("remaker","操作失败");
                return resultMap;
            }

            String searchResult = new String(wechatMsg.getBaseMsg().getPayloads().toByteArray(),StandardCharsets.UTF_8);
            if (wechatMsg.getBaseMsgOrBuilder().getRet() != 0) {
                resultMap.put("userName","");
                resultMap.put("remaker",searchResult);
                resultMap.put("status","1");
                if (searchResult.contains("频繁")) {
                    resultMap.put("status","2");
                }
                return resultMap;
            }

            Map map = new Gson().fromJson(searchResult, Map.class);
            username = " " + map.get("ExtInfo").toString();
        }

        if (type == 2) {
            username = " " + username;
        }

        WechatMsg wechatMsg = searchContact(username);
        if (wechatMsg == null) {
            resultMap.put("status","1");
            resultMap.put("userName","");
            resultMap.put("remaker","操作失败");
            return resultMap;
        }

        String searchResult = new String(wechatMsg.getBaseMsg().getPayloads().toByteArray(),StandardCharsets.UTF_8);
        if (wechatMsg.getBaseMsgOrBuilder().getRet() != 0) {
            resultMap.put("userName","");
            resultMap.put("status","1");
            resultMap.put("remaker",searchResult);
            if (searchResult.contains("频繁")) {
                resultMap.put("status","2");
            }
            return resultMap;
        }

        Map map = new Gson().fromJson(searchResult, Map.class);
        String nickName = map.get("NickName").toString();
        String headUrl = map.get("SmallHeadImgUrl").toString();
        String ticket = map.get("Ticket").toString();
        String wxID= map.get("ExtInfo").toString();
        if (type == 0 && !wxID.contains("wxid_")) {
            wxID = username;
        }

        resultMap.put("nickName",nickName);
        resultMap.put("headUrl",headUrl);

        if (ticket == null || ticket.isEmpty()) {
            resultMap.put("status","1");
            resultMap.put("remaker","此人已是好友");
            return resultMap;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("Encryptusername", wxID);
        params.put("Ticket", ticket);
        params.put("Type", 2);
        params.put("Sence", 3);
        if (at == 1) {
            params.put("Content", nickName + " " + helloContent);
        } else {
            params.put("Content", helloContent);
        }

        // 加人请求
         wechatMsg = addUserRequest(137,params);
         if (wechatMsg == null) {
             resultMap.put("remaker","操作失败");
             resultMap.put("status","1");
             return resultMap;
         }

        if (wechatMsg.getBaseMsg().getRet() != 0) {
            String result = new String(wechatMsg.getBaseMsg().getPayloads().toByteArray(),StandardCharsets.UTF_8);
            resultMap.put("status","1");
            resultMap.put("remaker",result);
            if (searchResult.contains("频繁")) {
                resultMap.put("status","2");
            }
            return resultMap;
        }

        resultMap.put("userName",wxID);
        resultMap.put("status","0");
        resultMap.put("remaker","已发送验证");
        return resultMap;
    }

    @Override
    public Map<String, String> contactOperate(String encrypUserName, String ticket, String content, int type, int Scene) {
        //1关注公众号2打招呼 主动添加好友3通过好友请求
        Map<String, String> resultMap = new HashMap<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("Encryptusername", encrypUserName);//v1_
        params.put("Ticket", ticket);//v2_
        params.put("Type", type);//1关注公众号2打招呼 主动添加好友3通过好友请求
        params.put("Content", content);//打招呼内容
        params.put("Sence", Scene);//1来源QQ2来源邮箱3来源微信号14群聊15手机号18附近的人25漂流瓶29摇一摇30二维码
        WechatMsg wechatMsg = addUserRequest(137, params);
        if (wechatMsg == null) {
            resultMap.put("remaker","操作失败");
            resultMap.put("status","1");
            return resultMap;
        }

        if (wechatMsg.getBaseMsg().getRet() != 0) {
            String result = new String(wechatMsg.getBaseMsg().getPayloads().toByteArray(),StandardCharsets.UTF_8);
            resultMap.put("status","1");
            resultMap.put("remaker",result);
            return resultMap;
        }
        resultMap.put("userName",encrypUserName);
        resultMap.put("status","0");
        resultMap.put("remaker","成功");
        return resultMap;
    }

    @Override
    public void sendMicroMsg(String toUsername,String content ) {

        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ToUserName",toUsername);
        params.put("Type",0);
        params.put("Content",content);

        longServerRequest(522,params,false);

        List<Map> messageList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("fromUser",loginedUser.getUserame());
        map.put("toUser",toUsername);
        map.put("msgType","1");
        map.put("content",content);
        map.put("timeStamp",String.valueOf(System.currentTimeMillis() / 1000));
        map.put("details","");
        messageList.add(map);

        String path = "/msg/device/syncMessage";
        Map<String, String> param = new HashMap<>();
        param.put("randomid", randomid + "");
        String strList = new Gson().toJson(messageList);
        param.put("list", strList);
        HttpService.httpRequest(path, param);

    }

    @Override
    public void sendAppMsg(String toUsername, String title, String content, String pointUrl, String thumburl) {

        HashMap<String, Object> params = new HashMap<>(16);
        params.put("FromUserName",loginedUser.getUserame());
        params.put("ToUserName",toUsername);
        params.put("appId","");
        params.put("Type",5);
        params.put("Content",WxUtil.getAppMsgXml(title,content,pointUrl,thumburl));

        longServerRequest(222,params,false);
    }

    @Override
    public void sendVoiceMsg(String toUsername, String voiceUrl,int length) {
        if (length <= 0) {
            return;
        }

        String path = HttpService.downLoadFile(voiceUrl);
        if (path.isEmpty()) {
            return;
        }

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            byte[] data = new byte[bytes.length + 1];
            data[0] = 2;
            System.arraycopy(bytes,0,data,1,bytes.length);

            HashMap<String, Object> params = new HashMap<>();
            params.put("ToUserName",toUsername);
            params.put("Offset",0);
            params.put("Length",data.length);
            params.put("VoiceLength",length);
            params.put("EndFlag",1);
            params.put("Data",WechatUtil.byte2Int(data));
            params.put("VoiceFormat",4);

            longServerRequest(127, params,false);

            List<Map> messageList = new ArrayList<>();
            Map<String,String> map = new HashMap<>();
            map.put("fromUser",loginedUser.getUserame());
            map.put("toUser",toUsername);
            map.put("msgType","34");
            map.put("content","");
            map.put("timeStamp",String.valueOf(System.currentTimeMillis() / 1000));
            map.put("details",voiceUrl);
            messageList.add(map);

            path = "/msg/device/syncMessage";
            Map<String, String> param = new HashMap<>();
            param.put("randomid", randomid + "");
            String strList = new Gson().toJson(messageList);
            param.put("list", strList);
            HttpService.httpRequest(path, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendImageMsg(String toUsername, String picUrl) {

        byte[] bytes = WxUtil.downFileBytes(picUrl);
        if (bytes == null) {
            return;
        }
        int startPosition = 0;//起始位置
        int dataLen = 65535;
        int dataTotalLength = bytes.length;
        int timeStamp = (int)(System.currentTimeMillis()/1000);
        String clientImgId = loginedUser.getUserame()+ "_" + timeStamp;
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ClientImgId",clientImgId);
        params.put("ToUserName",toUsername);
        params.put("TotalLen",dataTotalLength);

        while (startPosition != dataTotalLength) {

            int count = dataTotalLength - startPosition > dataLen ? dataLen : dataTotalLength - startPosition;
            byte[] data = new byte[count];
            System.arraycopy(bytes, startPosition, data, 0, data.length);
            params.put("StartPos",startPosition);
            params.put("DataLen",count);
            params.put("Data",WechatUtil.byte2Int(data));
            startPosition += count;

            longServerRequest(110,params,false);
        }

        List<Map> messageList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("fromUser",loginedUser.getUserame());
        map.put("toUser",toUsername);
        map.put("msgType","3");
        map.put("content","");
        map.put("timeStamp",String.valueOf(System.currentTimeMillis() / 1000));
        map.put("details",picUrl);
        messageList.add(map);

        String path = "/msg/device/syncMessage";
        Map<String, String> param = new HashMap<>();
        param.put("randomid", randomid + "");
        String strList = new Gson().toJson(messageList);
        param.put("list", strList);
        HttpService.httpRequest(path, param);

    }

    @Override
    public Map createChatRoom(String chatroomName, List<String> membernames) {
        String membernameStr = StringUtils.join(membernames,",");
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Membernames",membernameStr);

        String result = longServerRequest(119,params,true);
        return new Gson().fromJson(result,Map.class);

    }

    @Override
    public int modChatroomname(String chatRoom, String roomName) {
        int status = -1;
        try {
            HashMap<String, Object> params = new HashMap<>(16);
            params.put("Cmdid",27);
            params.put("ChatRoom",chatRoom);
            params.put("Roomname",roomName);
            shortServerRequest(681,params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    @Override
    public int modUserRemark(String username, String remark) {
        int status = -1;
        try {
            HashMap<String, Object> params = new HashMap<>(16);
            params.put("Cmdid",2);
            params.put("CmdBuf",username);
            params.put("BitVal",7);
            params.put("Remark",remark);
            String result = shortServerRequest(681,params);
            status = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    @Override
    public int delUser(String username) {
        int status = -1;
        try {
            HashMap<String, Object> params = new HashMap<>(16);
            params.put("Cmdid",7);
            params.put("CmdBuf",username);
            String result = shortServerRequest(681,params);
            status = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }


    @Override
    public void modUserInfo(String nickname, String signature, String country, String province, String city, int sex) {

        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Cmdid",1);
        params.put("NickName",nickname);
        params.put("Signature",signature);
        params.put("Country",country);
        params.put("Province",province);
        params.put("City",city);
        params.put("Sex",sex);

        shortServerRequest(681,params);

    }

    @Override
    public String snsUpload(byte[] data) {
        int timeStamp = (int)(System.currentTimeMillis()/1000);
        String clientImgId = loginedUser.getUserame()+ "_" + timeStamp;

        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ClientId",clientImgId);
        params.put("TotalLen",data.length);

        int block = 65535;
        int startPos = 0;
        while (startPos != data.length) {
            params.put("StartPos", startPos);
            boolean needReturn = false;
            if (data.length - startPos > block) {
                byte[] temp = new byte[block];
                System.arraycopy(data, startPos, temp, 0, temp.length);
                startPos += temp.length;
                params.put("Uploadbuf", WechatUtil.byte2Int(temp));
            } else {
                byte[] temp = new byte[data.length - startPos];
                System.arraycopy(data, startPos, temp, 0, temp.length);
                startPos += temp.length;
                params.put("Uploadbuf", WechatUtil.byte2Int(temp));
                needReturn = true;
            }
            if (needReturn) {
                return longServerRequest(207, params, true);
            } else {
                longServerRequest(207, params, true);
            }

        }

        return "";

    }

    @Override
    public void snsPost(String content) {
        if (content == null) {
            return;
        }

        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Content",WechatUtil.byte2Int(content.getBytes(StandardCharsets.UTF_8)));
        longServerRequest(209,params,false);
    }

    @Override
    public List<Map> getContact(List<String> usernameList, String chatroomid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserNameList",StringUtils.join(usernameList,","));
        params.put("Chatroomid",chatroomid);
        String result = shortServerRequest(182,params);

        return JSON.parseArray(result,Map.class);
    }

    @Override
    public int uploadHeadImg(byte[] data) {
        if (data == null) {
            return -1;
        }

        int timeStamp = (int)(System.currentTimeMillis()/1000);
        String imgHash = loginedUser.getUserame() + "_" + timeStamp;

        HashMap<String, Object> params = new HashMap<>(16);
        params.put("HeadImgType",1);
        params.put("Data",WechatUtil.byte2Int(data));
        params.put("TotalLen",data.length);
        params.put("ImgHash",imgHash);
        params.put("StartPos",0);

        shortServerRequest(157,params);
        return 0;
    }

    @Override
    public void lbsFind(String longitude, String latitude) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Type",1);
        params.put("Longitude",longitude);
        params.put("Latitude",latitude);
        longServerRequest(148, params,false);
    }

    @Override
    public Map getChatroomMemberDetail(String chatroom) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("Chatroom",chatroom);
        String result = shortServerRequest(551, params);
        return JSON.parseObject(result,Map.class);
    }

    @Override
    public Map getRoomQrcode(String chatroom) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Username",chatroom);
        String result = longServerRequest(168, params,true);
        return JSON.parseObject(result,Map.class);
    }

    @Override
    public void delChatRoomUser(String chatroom, String username) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ChatRoom",chatroom);
        params.put("Username",username);
        shortServerRequest(179, params);
    }

    @Override
    public void snsTimeLine(BigInteger clientLatestId) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ClientLatestId",clientLatestId);

        String result = longServerRequest(211,params,true);
        try {
            JSONObject jsonObj = JSON.parseObject(result);

            if (jsonObj.containsKey("SnsObjects")) {
                JSONArray list = jsonObj.getJSONArray("SnsObjects");
                for (int i = 0; i < list.size(); i++) {
                    JSONObject item = list.getJSONObject(i);
                    item.put("Id",item.get("Id").toString());

                }
                jsonObj.put("SnsObjects",list);

                String path = "/msg/device/syncMoment";
                Map<String, String> map = new HashMap<>();
                map.put("randomid", String.valueOf(randomid));
                map.put("info", JSON.toJSONString(jsonObj));
                HttpService.httpRequest(path, map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String snsUserPage(String firstPageMd5, String username, BigInteger maxId) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("FirstPageMd5",firstPageMd5);
        params.put("Username",username);
        params.put("MaxId",maxId);
        return longServerRequest(212, params,true);


    }

    @Override
    public void snsObjectOp(String ids, int commentId, int type) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Ids",ids);
        params.put("CommentId",commentId);
        params.put("Type",type);
        longServerRequest(218,params,false);
    }

    @Override
    public void snsComment(String id, String toUsername, int type, String content) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ID",id);
        params.put("ToUsername",toUsername);
        params.put("Type",type);
        params.put("Content",content);
        shortServerRequest(213,params);
    }

    public int getReadNum(String reqReadNumUrl,Map<String,String> map,int count){
        int readNum = -1;
        count--;
        try {
            String readNumRet=HxHttpClient.postRead(reqReadNumUrl, map);
            Map<String, Object> readNumMap = Maps.newHashMap();
            readNumMap = JSONUtils.jsonToMap(readNumRet);
            if (readNumMap.size() > 0) {
                //判断阅读数量
                String appmsgstat = readNumMap.get("appmsgstat").toString();
                Map<String, Object> finalMap = JSONUtils.jsonToMap(appmsgstat);
                readNum = Integer.parseInt(finalMap.get("read_num").toString());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logger.error("com.wx.demo.frameWork.protocol.WechatServiceGrpc.getReadNum_error：msg={},cause={}，detail={}",e.getMessage(),e.getCause(),e.toString());
        }
        if (readNum<0&&count>0) {
            getReadNum(reqReadNumUrl, map, count);
        }
        return readNum;
    }

    @Override
    public R getReadA8KeyAndRead(String reqUrl, int scene, String username) {
        R ret=new R();
        Boolean isReamNumSuccess=false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ReqUrl", reqUrl);
        params.put("Scene", scene);//Scene = 2 来源好友或群 必须设置来源的id 3 历史阅读 4 二维码连接 7 来源公众号 必须设置公众号的id
        params.put("Username", username);//来源 来源设置wxid 来源群id@chatroom 来源公众号gh_e09c57858a0c原始id
        params.put("ProtocolVer",protocolVer);//ProtocolVer 1-5

        String reqJson = "";
        try {
            String a8kJson=shortServerRequest(233, params);
            Map<String, Object> a8kMap = JSONUtils.jsonToMap(a8kJson);
            logger.info("-----a8kMap-------------->>"+JSONObject.toJSONString(a8kMap));
            if (a8kMap!=null && a8kMap.size() > 0) {
                Map<String,String> readReq=Maps.newHashMap();
                StringBuilder fullUrl=new StringBuilder();

                String a8kUrl=a8kMap.get("Url").toString();
                fullUrl.append(a8kUrl);

                String XWechatUin=a8kMap.get("XWechatUin").toString();
                String XWechatKey=a8kMap.get("XWechatKey").toString();

                //解析a8k返回的参数
//                String decodeA8kUrl = GsonUtil.unicodetoString(a8kUrl);

//                int paramDataIndex = decodeA8kUrl.indexOf("?");
                int paramDataIndex = a8kUrl.indexOf("?");
                if (paramDataIndex!=-1) {
                    String paramData = a8kUrl.substring(paramDataIndex + 1);
                    String[] readParam=paramData.split("&");
                    Map map=Maps.newHashMap();
                    map.put("is_only_read","1");
                    for (String str : readParam) {
                        int eqindex = str.indexOf("=");
                        if (eqindex != -1) {
                            String key = str.substring(0, eqindex);
                            String value = str.substring(eqindex+1);
                            map.put(key, value);
                        }
                    }
                    if (map.size()>0) {
                        //带数字比较的阅读
                        String reqReadNumUrl=Constant.WX_READ_NUM_URL+"uin="+XWechatUin+"&key="+XWechatKey;

                        int readNumFirst=getReadNum(reqReadNumUrl, map,2);

                        if (StringUtils.isNotBlank(XWechatUin)) {
                            readReq.put("uin", XWechatUin);
                        }
                        if (StringUtils.isNotBlank(XWechatKey)) {
                            readReq.put("key", XWechatKey);
                        }
                        reqJson=HttpUtil.sendPostRead(fullUrl.toString(), readReq);
                        Thread.sleep(200);
                        int readNumSecond=getReadNum(reqReadNumUrl, map,2);
                        if (readNumFirst != -1 && readNumSecond != -1) {
                            if (readNumSecond>=readNumFirst) {
                                ret = R.ok();
                            }else{
                                ret = R.error(3,"未能实际进行有效阅读");
                            }
                        }
                    }else{
                        //不带
                        if (StringUtils.isNotBlank(XWechatUin)) {
                            readReq.put("uin", XWechatUin);
                        }
                        if (StringUtils.isNotBlank(XWechatKey)) {
                            readReq.put("key", XWechatKey);
                        }
                        reqJson=HttpUtil.sendPostRead(fullUrl.toString(), readReq);
                    }
                }
            }else{
                ret = R.error(2, "a8k拉取为空,微信号系没有阅读功能");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = R.error();
        }
        return ret;
    }


    @Override
    public String getA8Key(String reqUrl, int scene, String username) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ReqUrl",reqUrl);
        params.put("Scene",scene);
        params.put("Username",username);
        return longServerRequest(233, params, true);
    }

    @Override
    public void joinChatRoomFormCode(String codeUrl,String taskId) {
        try {
            String result = getA8Key(codeUrl, 4, loginedUser.getUserame());


            if (result.contains("jump")) {
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("remark", "已入群");
                resultMap.put("chatroomName", "");
                String path = "/msg/device/joinChatRoomFormCode";
                resultMap.put("randomid", String.valueOf(randomid));
                resultMap.put("taskId",taskId);
                HttpService.httpRequest(path, resultMap);
                return;
            }

            Map map = JSON.parseObject(result,Map.class);
            String requestUrl = map.get("Url").toString();
            Map<String,String> requestMap = new HashMap<>();
            requestMap.put("forBlackberry","forceToUsePost");

            HttpService.wxHttpRequest(requestUrl,requestMap);

            result = getA8Key(codeUrl, 4, loginedUser.getUserame());

            map = JSON.parseObject(result,Map.class);
            String url = map.get("Url").toString();
            String chatroom = url.substring(url.lastIndexOf("/") + 1);

            List<Map> searchMapList = getContact(Collections.singletonList(chatroom),"");
            Map chatroomDetailMap = searchMapList.get(0);

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("chatroomName", chatroomDetailMap.get("NickName").toString());
            resultMap.put("remark", "成功入群");
            String path = "/msg/device/joinChatRoomFormCode";
            resultMap.put("taskId",taskId);
            resultMap.put("randomid", String.valueOf(randomid));
            HttpService.httpRequest(path, resultMap);


        } catch (Exception e) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("remark", "入群失败");

            if (e.getMessage() != null) {
                if (e.getMessage().contains("二维码已过期")) {
                    resultMap.put("remark", "二维码已过期");
                }
            }
            resultMap.put("chatroomName", "");
            String path = "/msg/device/joinChatRoomFormCode";
            resultMap.put("taskId",taskId);
            resultMap.put("randomid", String.valueOf(randomid));
            HttpService.httpRequest(path, resultMap);
        }


    }

    @Override
    public void addChatRoomMember(String chatroom, String username) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Roomeid",chatroom);
        params.put("Membernames",username);
        longServerRequest(120,params,false);
    }

    @Override
    public void setChatRoomAnnouncement(String chatroom, String announcement) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ChatRoomName",chatroom);
        params.put("Announcement",announcement);
        shortServerRequest(993,params);
    }

    @Override
    public void shakeGet(String longitude, String latitude) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Longitude",longitude);
        params.put("Latitude",latitude);
        String result = longServerRequest(161,params,true);

        byte[] shakeReportKeybuf = result.getBytes(StandardCharsets.UTF_8);
        params = new HashMap<>();
        params.put("Keybuf",WechatUtil.byte2Int(shakeReportKeybuf));
        longServerRequest(162,params,false);
    }

    @Override
    public void massMessage(String wxIds, byte[] data, int msgType) {
        int block = 65535;
        int startPos = 0;

        String clientId = loginedUser.getUserame() + "_" + System.currentTimeMillis() / 1000;
        HashMap<String, Object> params = new HashMap<>();
        params.put("ClientId", clientId);
        params.put("ToList", wxIds);
        params.put("ToListMd5", WechatUtil.getMd5(wxIds));
        params.put("VoiceFormat", 0);
        params.put("MsgType", msgType);
        params.put("DataTotalLen", data.length);

        while (startPos != data.length) {
            params.put("DataStartPos", startPos);

            if (data.length - startPos > block) {
                byte[] temp = new byte[block];
                System.arraycopy(data, startPos, temp, 0, temp.length);
                startPos += temp.length;
                params.put("DataBuffer", WechatUtil.byte2Int(temp));
            } else {
                byte[] temp = new byte[data.length - startPos];
                System.arraycopy(data, startPos, temp, 0, temp.length);
                startPos += temp.length;
                params.put("DataBuffer", WechatUtil.byte2Int(temp));
            }

            longServerRequest(193, params, null);
        }

    }

    @Override
    public void massMessage(String data) {
        JSONObject object = JSONObject.parseObject(data);
        String taskId = object.getString("taskId");

        try {
            List<String> usernameList = JSONArray.parseArray(object.getString("username"), String.class);

            JSONArray array = object.getJSONArray("content");
            int len = array.size();
            for (int i = 0; i < len; i++) {

                JSONObject content = array.getJSONObject(i);
                String msg = content.getString("content");
                int type = content.getIntValue("type");

                if (type == 2) {
                    for (String username : usernameList) {
                        sendAppMsg(username, content.getString("title"), content.getString("content"),
                                        content.getString("linkUrl"), content.getString("picUrl"));
                        Thread.sleep(1000);
                    }
                } else if (protocolVer == 1) {
                    for (String username : usernameList) {
                        switch (type) {
                            case 1: // 图片消息
                                sendImageMsg(username, msg);
                                break;
                            case 0: // 语音消息
                                int length = content.getIntValue("length");
                                sendVoiceMsg(username, msg,length);
                                break;
                            case 3: // 普通消息
                                sendMicroMsg(username, msg);
                                break;
                        }

                        Thread.sleep(1000);
                    }
                } else {
                    byte[] bytes;
                    switch (type) {
                        case 0:
                            bytes = WxUtil.downFileBytes(msg);
                            massMessage(StringUtils.join(usernameList,","),bytes,34);
                            break;
                        case 1:
                            bytes = WxUtil.downFileBytes(msg);
                            massMessage(StringUtils.join(usernameList,","),bytes,3);
                            break;
                        case 3:
                            bytes = msg.getBytes(StandardCharsets.UTF_8);
                            massMessage(StringUtils.join(usernameList,","),bytes,1);
                            break;
                    }

                }

                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Map<String, String> map = new HashMap<>();
            map.put("taskId", taskId);
            String path = "/msg/device/massMessage";
            map.put("randomid", String.valueOf(randomid));
            HttpService.httpRequest(path, map);
        }
    }

    protected void exit() {
        logger.info("------------用户" + wechatApi.getWxId() + "离线---------------");
        RedisManager.hrem((Constant.redisk_key_loinged_user + WechatUtil.serverId).getBytes(), randomid.getBytes());

        WechatDO wechatDO = new WechatDO();
        wechatDO.setRandomid(this.randomid);
        wechatDO.setStauts(2);
      //  wechatDO.setRemark(wechatApi.getGrpcPayLoads());
        wechatService.updateForWechatId(wechatDO);

        wechatSocket.close();
        heartBeatExe.shutdown();
        isAlifeCheckSevice.shutdown();
    }

    @Override
    public void autoAcceptUser(int status) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("Cmdid",23);
        params.put("AddFromMobile",2);
        params.put("AddFromWechat",2);
        params.put("AddFromChatroom",2);
        params.put("AddFromQrcode",2);
        params.put("AddFromCard",2);
        params.put("SnsOpenFlag",0);
        params.put("AddVerity",status == 0 ? 1 : 2);
        shortServerRequest(681,params);
    }


}
