package com.wx.demo.tools;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.redis.shiro.RedisManager;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.wx.demo.bean.CallBack;
import com.wx.demo.bean.RedisBean;
import com.wx.demo.frameWork.client.grpcClient.IpadApplication;
import com.wx.demo.frameWork.client.wxClient.Response;
import com.wx.demo.frameWork.client.wxClient.WechatSocket;
import com.wx.demo.frameWork.proto.BaseMsg;
import com.wx.demo.frameWork.proto.User;
import com.wx.demo.frameWork.proto.WechatMsg;
import com.wx.demo.frameWork.proto.WechatMsg.Builder;
import com.wx.demo.service.BaseService;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.wx.demo.httpHandler.HttpResult.getMd5;

public class WxLongUtil {
    private WechatSocket vxClient;
    public static byte[] sessionKey = new byte[]{80, 117, -128, 85, 2, 55, -76, 126, -115, 93, -71, -36, 112, -114, 15, -128};

    private String secondUUid = UUID.randomUUID().toString().toUpperCase();
    private String devideId = WechatUtil.getMd5(secondUUid.getBytes());
    private String ip;
    /**
     * 登录成功后返回的user对象
     */
    private UtilMsg.UtilUser loginedUser = null;

    String notifyKey = null;
    byte[] uuid = null;
    protected String shortServer;
    protected String longServer;
    private CallBack dataBack;
    // 发送获取二维码封包的返回结果(第一次请求)
    private WechatMsg temWechatMsg;

    protected BaseService baseService;

    private static Logger logger = Logger.getLogger(WxLongUtil.class);
    private boolean userlogin;

    public WxLongUtil(BaseService baseService) {
        try {
            this.baseService = baseService;
            this.dataBack = data -> {
                baseService.onData(data);
            };
            ip = CommonUtil.getPublicIpAddress();
            longServer = Settings.getSet().longServer;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectToWx(CallBack callBack) {
        if (longServer == null || longServer.equals("null") || longServer.equals("")) {
            longServer = Settings.getSet().longServer;
        }
        if (vxClient != null) {
            releaseVxClent();
        }
        if (devideId == null || devideId.equals("")) {
            devideId= WechatUtil.getMd5(secondUUid.getBytes());
        }
        vxClient = new WechatSocket(longServer, 80, () -> secondLogin(), () -> async(), callBack);
    }

    public void sendAppMsg(String userName, String content) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ToUserName", userName);
        params.put("Content", content);
        params.put("Type", 5);
        params.put("appId", "");
        longServerRequest(222, params, data -> {
            logger.info(Arrays.toString(data));
        });
    }





    public void getQrcode(CallBack callBack) {
        HashMap<String, Object> params = new HashMap<>(16);
        params.put("ProtocolVer", 1);
        userlogin = false;
        longServerRequest(502, params, callBack);
    }

    public void checkLogin(Response qrRes, CallBack callBack) {
        if (notifyKey == null) {
            notifyKey = qrRes.NotifyKey;
        }
        if (uuid == null) {
            uuid = qrRes.Uuid.getBytes();
        }
        longServerRequest(503, null, callBack);
    }
    public void login2(String Username, String Password,String wxdat, final WxLongUtilCallBack back) {
        if ( longServer == null || longServer.equals("") || shortServer == null || shortServer.equals("")) {
            longServer = WechatUtil.longServerHost;
            shortServer = WechatUtil.shortServerHost;
        }
        UtilMsg  loginedRes =    login2(Username,Password,wxdat);
        if (loginedRes.baseMsg.Ret == 0) {
            shortServer = loginedRes.baseMsg.ShortHost;
            longServer = loginedRes.baseMsg.LongHost;
            if (back != null) {
                String sers = new Gson().toJson(loginedRes);
                userlogin=true;
                logger.info(sers);
                back.onData(sers);
            }
            syncToRedis();
        } else if (loginedRes.baseMsg.Ret == -301) {
            // 重定向
            logger.info("重定向");
            longServer = loginedRes.baseMsg.LongHost;
            shortServer = loginedRes.baseMsg.ShortHost;
            login2(Username,Password,wxdat, back);
            return;
        } else {
            logger.info("登录失败:"+new Gson().toJson(loginedRes));
        }
    }


    /**
     * 发起登录
     *
     * @param
     */
    public UtilMsg login2(String Username, String Password,String wxdat) {
        // TODO:设置登录信息
        String deviceType = "<k21>TP_lINKS_5G</k21><k22>中国移动</k22><k24>" + CommonUtil.getMac(devideId) + "</k24>";
        HashMap<String, Object> loginData = new HashMap<String, Object>();
        loginData.put("Username", Username);
        loginData.put("PassWord", Password);
        loginData.put("UUid", secondUUid);
        loginData.put("DeviceType", deviceType);
        loginData.put("DeviceName", "xxx 的 ipad");
        loginData.put("ProtocolVer", 1);
        UtilMsg reqMsg = new UtilMsg();
        reqMsg.Version = Settings.getSet().version;
        reqMsg.TimeStamp = System.currentTimeMillis() / 1000;
        reqMsg.ip = ip;
        reqMsg.Token = Settings.getSet().machineCode;
        reqMsg.baseMsg.Cmd = 2222;
        reqMsg.baseMsg.LongHost = longServer;
        reqMsg.baseMsg.ShortHost = shortServer;
        reqMsg.baseMsg.PayLoads = new Gson().toJson(loginData).getBytes(StandardCharsets.UTF_8);
        reqMsg.baseMsg.user.SessionKey = sessionKey;
        reqMsg.baseMsg.user.DeviceId = wxdat;
        WechatMsg convert = convert(reqMsg);
        WechatMsg logRes = helloWechat(convert);
        UtilMsg  utilMsg = convert(logRes);
        URL url = null;
        try {
            url = new URL("http://" + shortServer + utilMsg.baseMsg.CmdUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            OutputStream os = con.getOutputStream();
            os.write(utilMsg.baseMsg.PayLoads);
            os.flush();
            InputStream is = con.getInputStream();
            int x = 0;
            byte[] bys = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((x = is.read(bys)) != -1) {
                bos.write(bys, 0, x);
                bos.flush();
            }
            bos.close();
            os.close();
            is.close();
            byte[] vxRes = bos.toByteArray();
            utilMsg.baseMsg.Cmd = -1001;
            utilMsg.baseMsg.PayLoads = vxRes;
            WechatMsg res = helloWechat(convert(utilMsg));
           int ret = res.getBaseMsgOrBuilder().getRet();
           loginedUser =  convert(res).baseMsg.user;
           return convert(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发起登录
     *
     * @param back 登录成功后的回调
     */
    public void login(Response qrRes, final WxLongUtilCallBack back) {
        // TODO:设置登录信息
        String deviceType = "<k21>TP_lINKS_5G</k21><k22>中国移动</k22><k24>" + CommonUtil.getMac(devideId) + "</k24>";
        HashMap<String, Object> loginData = new HashMap<String, Object>();
        loginData.put("Username", qrRes.Username);
        loginData.put("PassWord", qrRes.Password);
        loginData.put("UUid", secondUUid);
        loginData.put("DeviceType", deviceType);
        loginData.put("DeviceName", "xxx 的 ipad");
        loginData.put("ProtocolVer", 1);

        UtilMsg reqMsg = new UtilMsg();
        reqMsg.Version = Settings.getSet().version;
        reqMsg.TimeStamp = System.currentTimeMillis() / 1000;
        reqMsg.ip = ip;
        reqMsg.Token = Settings.getSet().machineCode;
        reqMsg.baseMsg.Cmd = qrRes.cmd;
        if ( longServer == null || longServer.equals("") || shortServer == null || shortServer.equals("")) {
            longServer = WechatUtil.longServerHost;
            shortServer = WechatUtil.shortServerHost;
        }
        reqMsg.baseMsg.LongHost = longServer;
        reqMsg.baseMsg.ShortHost = shortServer;
        reqMsg.baseMsg.PayLoads = new Gson().toJson(loginData).getBytes(StandardCharsets.UTF_8);
        reqMsg.baseMsg.user.SessionKey = sessionKey;
        reqMsg.baseMsg.user.DeviceId = qrRes.DeviceId;
        WechatMsg convert = convert(reqMsg);
        WechatMsg logRes = helloWechat(convert);
        final UtilMsg resMsg = convert(logRes);
        // 发送请求给腾讯
        byte[] bys = WechatUtil.getBuffers(logRes);
        vxClient.sendData(bys, data -> {
            resMsg.baseMsg.Cmd = -1001;
            resMsg.baseMsg.PayLoads = data;
            WechatMsg res = helloWechat(convert(resMsg));
            UtilMsg loginedRes = convert(res);
            if (loginedRes.baseMsg.Ret == 0) {
                loginedUser = convert(res).baseMsg.user;
                shortServer = loginedRes.baseMsg.ShortHost;
                if (back != null) {
                    back.onData(new Gson().toJson(loginedRes));
                }
                syncToRedis();
            } else if (loginedRes.baseMsg.Ret == -301) {
                // 重定向
                logger.info("重定向");
                longServer = loginedRes.baseMsg.LongHost;
                connectToWx(data1 -> {
                    login(qrRes, back);
                });
                return;
            } else {
                logger.info("登录失败");
            }
        });
    }

    private WechatMsg helloWechat(WechatMsg msg) {
        int cmd =msg.getBaseMsg().getCmd();
        WechatMsg.Builder builder = WechatMsg.newBuilder(msg);
        if(cmd == 137){
            builder.mergeFrom(IpadApplication.getInstance().helloapiWechat(msg));
        }else {
            builder.mergeFrom(IpadApplication.getInstance().helloWechat(msg));
        }
        if(builder.getBaseMsg()!=null && builder.getBaseMsg().getUser()!=null){
            loginedUser = convert(builder.build()).baseMsg.user;
        }
        return builder.build();
    }


    /**
     * 心跳
     */
    public void async() {
        longServerRequest(138, null, dataBack);
    }

    /**
     * 收到转账
     * 1、收到转账的消息体， 转账消息,在json里的类型,是49,xml里类型是<type>2000</type> ，确定是转账
     * 2、解析消息体 获取Invalidtime、Transferid
     *
     * @param Invalidtime
     * @param Transferid
     * @param FromUsername
     */
    public String getZhuanZhang(String Invalidtime, String Transferid, String FromUsername) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("CgiCmd", 85);
        params.put("Invalidtime", Invalidtime);
        params.put("Transferid", Transferid);
        params.put("FromUsername", FromUsername);
        return shortServerRequest(385, params);
    }

    //修改群名
    public String modifyChatRoomName(String wxid, String name) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Cmdid", 27);
        params.put("ChatRoom", wxid);
        params.put("Roomname", name);
      return shortServerRequest(681, params);
    }

    //删除好友
    public String delUser(String wxid) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Cmdid", 7);
        params.put("CmdBuf", wxid);
       return shortServerRequest(681, params);
    }
    //创建收款码
    public String F2ffee(String TotalAmount, String Desc) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("CgiCmd", 94);
        params.put("Desc", Desc);//url编码的描述 备注
        params.put("TotalAmount", TotalAmount);
        return shortServerRequest(385, params);
    }
    //设置备注名
    public void setBackName(String wxid, String remark) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Cmdid", 2);
        params.put("CmdBuf", wxid);
        params.put("BitVal", 7);
        params.put("Remark", remark);
        shortServerRequest(681, params);
    }
//    //拉黑
//    public void setBackName(String wxid, String remark) {
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("Cmdid", 2);
//        params.put("CmdBuf", wxid);
//        params.put("BitVal", 7);
//        params.put("Remark", remark);
//        shortServerRequest(681, params);
//    }
    //网络搜索，用关键字搜索好友等
    public String webSearch(String key) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Searchinfo", key);
        return shortServerRequest(719, params);
    }

    public String getAllContact(int contactSeq, int chatRoomContactSql) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("CurrentWxcontactSeq", contactSeq);
        params.put("CurrentChatRoomContactSeq", chatRoomContactSql);
        return shortServerRequest(851, params);
    }

    public void getImage(String MsgId,String ToUsername,String StartPos,String TotalLen,String DataLen) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("MsgId", NumberUtils.toLong(MsgId));
        params.put("ToUsername", ToUsername);
        params.put("StartPos", NumberUtils.toLong(StartPos));
        params.put("TotalLen", NumberUtils.toLong(TotalLen));
        params.put("DataLen", NumberUtils.toLong(DataLen));
        params.put("CompressType", 1);
        shortServerRequest(109,params);
    }
    public void getVoice(String startpos, String datalen, String datatotalength) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Startpos", startpos);
        params.put("datalen", datalen);
        params.put("datatotalength", datatotalength);
        shortServerRequest(128,params);
    }

    public String shortServerRequest(int code, HashMap<String, Object> params) {
        UtilMsg msg = new UtilMsg();
        msg.Token = Settings.getSet().machineCode;
        msg.Version = Settings.getSet().version;
        msg.TimeStamp = System.currentTimeMillis() / 1000;
        msg.ip = ip;
        msg.baseMsg.Cmd = code;
        msg.baseMsg.user = loginedUser;
        msg.baseMsg.PayLoads = new Gson().toJson(params).getBytes();
        final WechatMsg res = helloWechat(convert(msg));
        UtilMsg utilMsg = convert(res);
        try {
            URL url = new URL("http://" + shortServer + utilMsg.baseMsg.CmdUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            OutputStream os = con.getOutputStream();
            os.write(utilMsg.baseMsg.PayLoads);
            os.flush();
            InputStream is = con.getInputStream();
            int x = 0;
            byte[] bys = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((x = is.read(bys)) != -1) {
                bos.write(bys, 0, x);
                bos.flush();
            }
            bos.close();
            os.close();
            is.close();
            byte[] vxRes = bos.toByteArray();
            if(vxRes.length >0 && vxRes[0] ==-65) {
                utilMsg.baseMsg.PayLoads = vxRes;
                utilMsg.baseMsg.Cmd = -code;
                utilMsg = convert(helloWechat(convert(utilMsg)));
                return new String(utilMsg.baseMsg.PayLoads, "utf-8");
            }
            } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }





    public interface WxLongUtilCallBack {
        void onData(String resData);
    }

    public String openHongBao(String nativeUrl, String fromUsername) {
        String result = queryHongBao(nativeUrl, fromUsername, 3, 0, "");
        String timingIdentifier = JSONObject.parseObject(result).getString("timingIdentifier");
        return queryHongBao(nativeUrl, fromUsername, 4, 0, timingIdentifier);
    }

    public String queryHongBao(String nativeUrl, String fromUserName, int type, int offset) {
        return queryHongBao(nativeUrl, fromUserName, type, offset, "");
    }

    private String queryHongBao(String nativeUrl, String fromUserName, int type, int offset, String timingIdentifier) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("CgiCmd", type);
        params.put("FromUserName", fromUserName);
        params.put("Nativeurl", nativeUrl);
        params.put("Limit", offset);
        if (timingIdentifier != null && !timingIdentifier.equals("")) {
            params.put("TimingIdentifier", timingIdentifier);
        }
        return shortServerRequest(1556, params);
    }

    public static class UtilMsg {
        public String Token;
        public String Version;
        public long TimeStamp;
        public String ip;
        public UtilBase baseMsg = new UtilBase();

        public static class UtilBase {
            public int Cmd;
            public String CmdUrl;
            public String LongHost;
            public byte[] LongHead;
            public byte[] PayLoads;
            public UtilUser user = new UtilUser();
            public byte[] Playloadextend;
            public int Ret;
            public String ShortHost;
        }

        public static class UtilUser implements Serializable {
            public byte[] SessionKey;
            public String DeviceId;
            public byte[] MaxSyncKey;
            public long Uin;
            public byte[] AutoAuthKey;
            public byte[] Cookies;
            public byte[] CurrentsyncKey;
            public String DeviceName;
            public String DeviceType;
            public String NickName;
            public String UserName;
            public String UserExt;
        }
    }

    /**
     * @param msg
     * @return
     */
    public static WechatMsg convert(UtilMsg msg) {
        Builder chatBuilder = WechatMsg.newBuilder();
        BaseMsg.Builder baseBuilder = BaseMsg.newBuilder();
        User.Builder userBuilder = User.newBuilder();
        // 构建user对象
        try {
            userBuilder.setSessionKey(ByteString.copyFrom(msg.baseMsg.user.SessionKey));
        } catch (Exception e1) {
        }
        try {
            userBuilder.setDeviceId(msg.baseMsg.user.DeviceId);
        } catch (Exception e1) {
        }
        try {
            userBuilder.setMaxSyncKey(ByteString.copyFrom(msg.baseMsg.user.MaxSyncKey));
        } catch (Exception e1) {
        }
        try {
            userBuilder.setUin(msg.baseMsg.user.Uin);
        } catch (Exception e1) {
        }
        try {
            userBuilder.setAutoAuthKey(ByteString.copyFrom(msg.baseMsg.user.AutoAuthKey));
        } catch (Exception e1) {
        }
        try {
            userBuilder.setCookies(ByteString.copyFrom(msg.baseMsg.user.Cookies));
        } catch (Exception e1) {
        }
        try {
            userBuilder.setCurrentsyncKey(ByteString.copyFrom(msg.baseMsg.user.CurrentsyncKey));
        } catch (Exception e1) {
        }
        try {
            userBuilder.setDeviceName(msg.baseMsg.user.DeviceName);
        } catch (Exception e1) {
        }
        try {
            userBuilder.setDeviceType(msg.baseMsg.user.DeviceType);
        } catch (Exception e1) {
        }
        try {
            userBuilder.setNickname(ByteString.copyFrom(msg.baseMsg.user.NickName, "utf-8"));
            userBuilder.setUserExt(ByteString.copyFrom(msg.baseMsg.user.UserExt, "utf-8"));
        } catch (Exception e) {
        }
        try {
            userBuilder.setUserame(msg.baseMsg.user.UserName);
        } catch (Exception e) {
        }
        // 构建basemsg对象
        try {
            baseBuilder.setCmd(msg.baseMsg.Cmd);
        } catch (Exception e) {
        }
        try {
            baseBuilder.setCmdUrl(msg.baseMsg.CmdUrl);
        } catch (Exception e) {
        }
        try {
            baseBuilder.setLongHead(ByteString.copyFrom(msg.baseMsg.LongHead));
        } catch (Exception e) {
        }
        try {
            baseBuilder.setLongHost(msg.baseMsg.LongHost);
        } catch (Exception e) {
        }
        try {
            baseBuilder.setPayloads(ByteString.copyFrom(msg.baseMsg.PayLoads));
        } catch (Exception e) {
        }
        try {
            baseBuilder.setPlayloadextend(ByteString.copyFrom(msg.baseMsg.Playloadextend));
        } catch (Exception e) {
        }
        try {
            baseBuilder.setRet(msg.baseMsg.Ret);
        } catch (Exception e) {
        }
        try {
            baseBuilder.setShortHost(msg.baseMsg.ShortHost);
        } catch (Exception e) {
        }
        try {
            baseBuilder.setUser(userBuilder.build());
        } catch (Exception e) {
        }
        // 构建msg对象
        try {
            chatBuilder.setToken(msg.Token);
        } catch (Exception e) {
        }
        try {
            chatBuilder.setVersion(msg.Version);
        } catch (Exception e) {
        }
        try {
            chatBuilder.setTimeStamp((int) msg.TimeStamp);
        } catch (Exception e) {
        }
        try {
            chatBuilder.setIP(msg.ip);
        } catch (Exception e) {
        }
        try {
            chatBuilder.setBaseMsg(baseBuilder.build());
        } catch (Exception e) {
        }
        // 返回结果 可算恶心死我了
        return chatBuilder.build();
    }

//    /**
//     * 发起登录
//     *
//     * @param back 登录成功后的回调
//     */
//    public void UserLogin(int code ,String Username, String Password,String wxdat,final WxLongUtilCallBack back) {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("Username", Username);
//        param.put("PassWord", Password);
//        param.put("UUid", devideId);
//        param.put("DeviceType", deviceType);
//        param.put("DeviceName", deviceName);
//        param.put("ProtocolVer", protocolVer);//协议类型 分别是什么 上次写的被擦掉了你
//        param.put("language", "en_US");
//        param.put("realCountry", "en");
//        this.deviceId = wxdat;//62数据
//        if(code == 3333){
//            param.put("ProtocolVer", 4);
//            param.put("OSType", "android-25");
//            param.put("DeviceIMEI", "864530499210871");
//            param.put("DeviceAndroid", "c2dd2d301dd92789");
//            param.put("DeviceMac", "17:e2:c4:4a:01:21");
//            param.put("DeviceModel", "armeabi-v7a");
//            param.put("DeviceName", "HM NOTE 1TD");
//            param.put("DeviceBrand", "Xiaomi");
//            param.put("DeviceType", "android-23");
//            param.put("UUid", "这个");//a16数据
//            param.put("DeviceID", deviceId);
//        }
//        WechatMsg requestMsg = helloWechat(2222, param);
//        byte[] buffers = getBuffers(requestMsg);
//        vxClient.sendData(buffers, data -> {
//            if (back != null && data.length > 16 && data[16] == -65) {
//                WechatMsg responseMsg = helloWechat(-1001, data);
//                if (responseMsg.getBaseMsg().getRet() == 0) {
//                    back.onData(convert(responseMsg));
//                    syncToRedis();
//                } else if (responseMsg.getBaseMsg().getRet() == -301) {
//                    // 重定向
//                    logger.info("重定向!");
//                    longServerHost = responseMsg.getBaseMsg().getLongHost();
//                    connectToWx(data1 -> {
//                        UserLogin(code,Username,Password,wxdat, back);
//                    });
//                    return;
//                } else if (responseMsg.getBaseMsg().getRet() == -106) {
//                    baseService.getState().code = -106;
//                    baseService.getState().msg = responseMsg.getBaseMsg().getPayloads().toStringUtf8();
//                } else {
//                    logger.info("登录失败:" + responseMsg.getBaseMsg().getPayloads().toStringUtf8());
//                }
//            }
//        });
//    }













    private UtilMsg convert(WechatMsg msg) {
        UtilMsg res = new UtilMsg();
        res.ip = msg.getIP();
        res.TimeStamp = msg.getTimeStamp();
        res.Token = msg.getToken();
        res.Version = msg.getVersion();
        res.baseMsg.Cmd = msg.getBaseMsg().getCmd();
        res.baseMsg.CmdUrl = msg.getBaseMsg().getCmdUrl();
        res.baseMsg.LongHead = msg.getBaseMsg().getLongHead().toByteArray();
        res.baseMsg.LongHost = msg.getBaseMsg().getLongHost();
        res.baseMsg.PayLoads = msg.getBaseMsg().getPayloads().toByteArray();
        res.baseMsg.Playloadextend = msg.getBaseMsg().getPlayloadextend().toByteArray();
        res.baseMsg.Ret = msg.getBaseMsg().getRet();
        res.baseMsg.ShortHost = msg.getBaseMsg().getShortHost();

        res.baseMsg.user.AutoAuthKey = msg.getBaseMsg().getUser().getAutoAuthKey().toByteArray();
        res.baseMsg.user.Cookies = msg.getBaseMsg().getUser().getCookies().toByteArray();
        res.baseMsg.user.CurrentsyncKey = msg.getBaseMsg().getUser().getCurrentsyncKey().toByteArray();
        res.baseMsg.user.DeviceId = msg.getBaseMsg().getUser().getDeviceId();
        res.baseMsg.user.DeviceName = msg.getBaseMsg().getUser().getDeviceName();
        res.baseMsg.user.DeviceType = msg.getBaseMsg().getUser().getDeviceType();
        res.baseMsg.user.MaxSyncKey = msg.getBaseMsg().getUser().getMaxSyncKey().toByteArray();
        res.baseMsg.user.NickName = msg.getBaseMsg().getUser().getNickname().toStringUtf8();
        res.baseMsg.user.SessionKey = msg.getBaseMsg().getUser().getSessionKey().toByteArray();
        res.baseMsg.user.Uin = msg.getBaseMsg().getUser().getUin();
        res.baseMsg.user.UserExt = msg.getBaseMsg().getUser().getUserExt().toStringUtf8();
        res.baseMsg.user.UserName = msg.getBaseMsg().getUser().getUserame();

        return res;
    }

    public UtilMsg.UtilUser getLoginedUser() {
        return loginedUser;
    }

    public String getShortServer() {
        return shortServer;
    }

    public String getSecondUUid() {
        return secondUUid;
    }

    public String getLongServer() {
        return longServer;
    }
    public String getDevideId() {
        return devideId;
    }

    public void setDevideId(String devideId) {
        this.devideId = devideId;
    }
    public void setSecondUUid(String secondUUid) {
        this.secondUUid = secondUUid;
    }

    public void setLoginedUser(UtilMsg.UtilUser loginedUser) {
        this.loginedUser = loginedUser;
    }

    public void setShortServer(String shortServer) {
        this.shortServer = shortServer;
    }

    public void setLongServer(String longServer) {
        this.longServer = longServer;
    }

    public void sendMessage(String userName, String string) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ToUserName", userName);
        params.put("Content", string);
        params.put("MsgSource", "");
        params.put("Type", 0);
        longServerRequest(522, params, null);
    }

    /**
     * "{\"ToUserName\":\"" + tb_ToUsername.Text + "\",\"Content\":\"" + tb_Content.Text + "\",\"Type\":42,\"MsgSource\":\"" + tb_AtUserlist.Text + "\"}";
     *
     * @param userName
     */

    public void sendCardMsg(String userName, String content) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ToUserName", userName);
        params.put("Content", content);
        params.put("MsgSource", "");
        params.put("Type", 42);
        longServerRequest(522, params, null);
    }

    public void sendVoice(String userName, byte[] voiceByte, int voiceLength) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ToUserName", userName);
        params.put("Offset", 0);
        params.put("Length", voiceByte.length);
        params.put("VoiceLength", voiceLength);
        params.put("EndFlag", 1);
        params.put("Data", byte2Int(voiceByte));
        params.put("VoiceFormat", 0);
        longServerRequest(127, params, null);
    }

    //点赞，或评论
    public void SnsComment(String userName, String Id, int type, String content) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ID", Id);
        params.put("ToUsername", userName);
        params.put("Type", type);//1点赞2评论
        params.put("Content", content);
        shortServerRequest(213, params);
    }

    //1删除朋友圈2设为隐私3设为公开4删除评论5取消点赞
    public void snsObjectOp(String ids, int type) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Ids", ids);// 逗号分隔 要操作的朋友圈内容
        params.put("Type", type);//1删除朋友圈2设为隐私3设为公开4删除评论5取消点赞
        longServerRequest(218, params, null);
    }

    //获取自己朋友圈
    public String getOwnerSnsPage() {
        final String[] id = {null};

        longServerRequest(214, null, new CallBack() {
            @Override
            public void onData(byte[] data) {

            }
        });
        int i = 0;
        while (id[0]==null){
            if(i==30&&id[0]==null){
                break;
            }
            i++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            continue;
        }
        return id[0];
    }

    //发送朋友圈
    public String  sendSns(String content) {
        final String[] id = {null};
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Content", byte2Int(content.getBytes(StandardCharsets.UTF_8)));
        longServerRequest(209, params, new CallBack() {
            @Override
            public void onData(byte[] data) {
                id[0] = RegexUtils.regexSnsId(new String(data));
            }
        });
        int i = 0;
        while (id[0]==null){
            if(i==30&&id[0]==null){
                break;
            }
            i++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            continue;
        }
        return id[0];
    }

    public void snsTimeLine(String firstMd5, int lasterId, CallBack callBack) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("FirstPageMd5", firstMd5);
        params.put("ClientLatestId", lasterId);
        longServerRequest(211, params, callBack);
    }
    public String setChatRoomAnnouncement(String groupid, String content) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ChatRoomName", groupid);
        params.put("Announcement", content);
      return   shortServerRequest(993, params);
    }


    /**
     * 创建群
     */
    public void createChatRoom(String string, CallBack callBack) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Membernames", string);
        longServerRequest(119, params, callBack);
    }

    // QQ号 手机号微信号搜索
    public void searchContact(CallBack callBack, String userName) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Username", userName);
        longServerRequest(106, params, callBack);
    }

    public void getRoomQrcode(final CallBack callBack, String groupId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Username", groupId);
        longServerRequest(168, params, callBack);
    }

    /**
     * 获取好友或群的信息
     *
     * @return
     */
    public void getUserOrGroupInfo(final CallBack call, String wxIds) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserNameList", wxIds);
        longServerRequest(182, params, call);
    }

    //1关注公众号2打招呼 主动添加好友3通过好友请求
    public void contactOperate(String encrypUserName, String ticket, String content, int type, int Scene) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("Encryptusername", encrypUserName);//v1_
        params.put("Ticket", ticket);//v2_
        params.put("Type", type);//1关注公众号2打招呼 主动添加好友3通过好友请求
        params.put("Content", content);//打招呼内容
        params.put("Sence", Scene);//1来源QQ2来源邮箱3来源微信号14群聊15手机号18附近的人25漂流瓶29摇一摇30二维码
        shortServerRequest(137, params);
    }

        public void inviteUserToChatRoom(String chatRoomId, String userWxIds) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ChatRoom", chatRoomId);
        params.put("Username", userWxIds);
        shortServerRequest(610, params);
    }

    /**
     * 踢人
     */
    public String removeUser(String groupId, String username) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ChatRoom", groupId);
        params.put("Username", username);
     return  shortServerRequest(179, params);
    }
    public String changegroup(String groupId, String username) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ChatRoomName", groupId);
        params.put("Username", username);
        return  shortServerRequest(990, params);
    }
    public void addLabel(String labelName) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("LabelName", labelName);
        shortServerRequest(635, params);
    }

    public void getAllLabel(CallBack callBack) {
        shortServerRequest(639, null);
    }

    public void setLabel(String wxId, String labels) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Labelids", labels);
        params.put("Username", wxId);
        shortServerRequest(638, params);
    }

    //获取指定人朋友圈
    public String getUserPYQ(String wxId, String md5, int maxId) {
        final String[] id = {null};
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("FirstPageMd5", md5);//首页为空 第二页请附带md5
        params.put("Username", wxId);
        params.put("MaxId", maxId);//首页为0 次页朋友圈数据id 的最小值
        longServerRequest(212, params, new CallBack() {
            @Override
            public void onData(byte[] data) {
                id[0] = RegexUtils.regexSnsId(new String(data));
            }
        });
    int i = 0;
        while (id[0]==null){
        if(i==30&&id[0]==null){
            break;
        }
        i++;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        continue;
    }
        return id[0];

    }

    public void longServerRequest(int code, HashMap<String, Object> params, CallBack call) {
        try {
            UtilMsg msg = new UtilMsg();
            msg.Token = Settings.getSet().machineCode;
            msg.Version = Settings.getSet().version;
            msg.TimeStamp = System.currentTimeMillis() / 1000;
            msg.ip = ip;
            msg.baseMsg.Cmd = code;
            if (loginedUser == null) {
                msg.baseMsg.user.SessionKey = sessionKey;
                msg.baseMsg.user.DeviceId = devideId;
                loginedUser = msg.baseMsg.user;
            } else {
                msg.baseMsg.user = loginedUser;
            }
            if (params != null) {
                msg.baseMsg.PayLoads = new Gson().toJson(params).getBytes("utf-8");
            }
            if (code == 503) {
                msg.baseMsg.LongHead = temWechatMsg.getBaseMsg().getLongHead().toByteArray();
                msg.baseMsg.PayLoads = uuid;
                msg.baseMsg.user.MaxSyncKey = Base64.getDecoder().decode(notifyKey.getBytes());
            }
            WechatMsg res = helloWechat(convert(msg));
            if (code == 502 && temWechatMsg == null) {
                temWechatMsg = res;
            }
            byte[] buffers = WechatUtil.getBuffers(res);
            vxClient.sendData(buffers, data -> {
                if (call != null && data.length >16 && data[16] ==-65) {
                    UtilMsg req = convert(res);
                    req.baseMsg.Cmd = code == 211 ? -212 : -code;
                    req.baseMsg.PayLoads = data;
                    req = convert(helloWechat(convert(req)));
                    loginedUser = req.baseMsg.user;
                    call.onData(req.baseMsg.PayLoads);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     *
     * @param
     * @param
     */
    public void sendImage(String toUser, byte[] imgByte) {
        int block = 65535;
        long startPos = 0;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ClientImgId", loginedUser.UserName + "_" + System.currentTimeMillis() / 1000);
        params.put("ToUserName", toUser);
        params.put("TotalLen", imgByte.length);
        while (startPos != imgByte.length) {
            params.put("StartPos", startPos);
            if (imgByte.length - startPos > block) {
                byte[] temp = new byte[block];
                System.arraycopy(imgByte, (int) startPos, temp, 0, temp.length);
                startPos += temp.length;
                params.put("DataLen", temp.length);
                params.put("Data", byte2Int(temp));
            } else {
                byte[] temp = new byte[(int) (imgByte.length - startPos)];
                System.arraycopy(imgByte, (int) startPos, temp, 0, temp.length);
                startPos += temp.length;
                params.put("DataLen", temp.length);
                params.put("Data", byte2Int(temp));
            }
            longServerRequest(110, params, null);
        }
    }

    public void newInit() {
        //cgz 新增
        HashMap<String, Object> params = new HashMap<>();
        // wuzf modi
        params.put("ProtocolVer", Constant.protocolVer);
        longServerRequest(1002, params, data -> {
            try {
                String dataStr = new String(data, "utf-8");
                if (dataStr != null && !dataStr.equals("null") && !dataStr.equals("")) {
                    newInit();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }
    public String approveAddChat(String Ticket, String   Inviterusername, String    Username, String    Roomname) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("Ticket", Ticket);
        params.put("Inviterusername", Inviterusername);
        params.put("Username", Username);
        params.put("Roomname", Roomname);
        return  shortServerRequest(774, params);
    }

    public void sendCDnimg(String toUser, Long startPos, Long TotalLen, Long DataLen, String CDNMidImgUrl, String AESKey, Long CDNMidImgSize, Long CDNThumbImgSize, Long CDNThumbImgHeight, Long CDNThumbImgWidth, final CallBack call) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("ClientImgId", loginedUser.UserName + "_" + System.currentTimeMillis() / 1000);
        params.put("ToUserName", toUser);
        params.put("StartPos", startPos);
        params.put("TotalLen", TotalLen);
        params.put("DataLen", DataLen);
        params.put("CDNMidImgUrl", CDNMidImgUrl);
        params.put("AESKey", AESKey);
        params.put("CDNMidImgSize", CDNMidImgSize);
        params.put("CDNThumbImgSize", CDNThumbImgSize);
        params.put("CDNThumbImgHeight", CDNThumbImgHeight);
        params.put("CDNThumbImgWidth", CDNThumbImgWidth);
        longServerRequest(-110, params, call);
    }

    public void snsUploadData(byte[] dataByte, CallBack callBack) {
        int block = 65535;
        int startPos = 0;
        String clientId = loginedUser.UserName + "_" + System.currentTimeMillis() / 1000;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ClientId", clientId);
        params.put("TotalLen", dataByte.length);
        while (startPos != dataByte.length) {
            params.put("StartPos", startPos);
            boolean needCallBack = false;
            if (dataByte.length - startPos > block) {
                byte[] temp = new byte[block];
                System.arraycopy(dataByte, startPos, temp, 0, temp.length);
                startPos += temp.length;
                params.put("Uploadbuf", byte2Int(temp));
            } else {
                byte[] temp = new byte[dataByte.length - startPos];
                System.arraycopy(dataByte, startPos, temp, 0, temp.length);
                startPos += temp.length;
                params.put("Uploadbuf", byte2Int(temp));
                needCallBack = true;
            }
            if (needCallBack) {
                longServerRequest(207, params, callBack);
            } else {
                longServerRequest(207, params, null);
            }

        }
    }

    public void massSendImgOrText(String wxIds, byte[] imgByte, int msgType) {
        try {
            int block = 65535;
            int startPos = 0;
            String clientId = loginedUser.UserName + "_" + System.currentTimeMillis() / 1000;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("ClientId", clientId);
            params.put("ToList", wxIds);
            params.put("ToListMd5", WechatUtil.getMd5(wxIds.getBytes("utf-8")));
            params.put("VoiceFormat", 0);
            params.put("MsgType", msgType);
            while (startPos != imgByte.length) {
                params.put("DataStartPos", startPos);
                params.put("DataTotalLen", imgByte.length);
                if (imgByte.length - startPos > block) {
                    byte[] temp = new byte[block];
                    System.arraycopy(imgByte, startPos, temp, 0, temp.length);
                    startPos += temp.length;
                    params.put("DataBuffer", byte2Int(temp));
                } else {
                    byte[] temp = new byte[imgByte.length - startPos];
                    System.arraycopy(imgByte, startPos, temp, 0, temp.length);
                    startPos += temp.length;
                    params.put("DataBuffer", byte2Int(temp));
                }
                longServerRequest(193, params, null);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 二次登录
     */
    public void secondLogin() {
        if (loginedUser == null || userlogin) {
            return;
        }

        String deviceType = "<k21>TP_lINKS_5G</k21><k22>中国移动</k22><k24>" + CommonUtil.getMac(devideId) + "</k24>";
        HashMap<String, Object> loginData = new HashMap<String, Object>();
        loginData.put("UUid", secondUUid);
        loginData.put("DeviceType", deviceType);
        loginData.put("DeviceName", "xxx 的 ipad");
        loginData.put("ProtocolVer", 1);
        loginedUser.DeviceId=getDevideId();
        UtilMsg msg = new UtilMsg();
        msg.Token = Settings.getSet().machineCode;
        msg.Version = Settings.getSet().version;
        msg.TimeStamp = System.currentTimeMillis() / 1000;
        msg.ip = ip;
        msg.baseMsg.Cmd = 702;
        msg.baseMsg.user = loginedUser;
        try {
            msg.baseMsg.PayLoads = new Gson().toJson(loginData).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WechatMsg convert = convert(msg);
        WechatMsg logRes = helloWechat(convert);
        final UtilMsg resMsg = convert(logRes);
        // 发送请求给腾讯
        byte[] bys = WechatUtil.getBuffers(logRes);
        vxClient.sendData(bys, data -> {
            resMsg.baseMsg.Cmd = -702;
            resMsg.baseMsg.PayLoads = data;
            WechatMsg res = helloWechat(convert(resMsg));
            UtilMsg loginedRes = convert(res);
            logger.info(res.getBaseMsgOrBuilder().getPayloads().toStringUtf8());


            if (loginedRes.baseMsg.Ret == 0) {
                loginedUser = convert(res).baseMsg.user;
                logger.info("二次登陆成功");
                syncToRedis();
                baseService.getState().code = 3;
            } else if (loginedRes.baseMsg.Ret == -301) {
                // 重定向
                logger.info("二次登陆重定向");
                longServer = loginedRes.baseMsg.LongHost;
                connectToWx(data1 -> secondLogin());
            } else {
                logger.info("二次登陆失败");
                baseService.getState().code = -1;
                baseService.setIsDead(true);
            }
        });
    }

    public void syncToRedis() {
        RedisBean redisBean = new RedisBean();
        redisBean.loginedUser = getLoginedUser();
        redisBean.shortServerHost = getShortServer();
        redisBean.randomid = baseService.getrandomid();
        redisBean.softwareId = baseService.getSoftwareId();
        redisBean.account = baseService.getAccount();
        redisBean.uuid = getSecondUUid();
        redisBean.longServerHost = getLongServer();
        redisBean.serverid = getMd5(Settings.getSet().server_ip + ":" + Settings.getSet().server_port);
        redisBean.extraData = baseService.getExtraData();
        RedisManager.set(getLoginedUser().UserName,baseService.getrandomid());
        RedisManager.hset((Constant.redisk_key_loinged_user + WechatUtil.serverId).getBytes(), baseService.getrandomid().getBytes(), RedisBean.serialise(redisBean));
    }

    public void sendHeartPackage(final CallBack back) {
        longServerRequest(205, null, back);
    }

    public void releaseVxClent() {
        vxClient.end();
        vxClient = null;
    }

    private int[] byte2Int(byte[] bys) {
        int[] res = new int[bys.length];
        for (int x = 0; x < bys.length; x++) {
            res[x] = bys[x] & 0xff;
        }
        return res;
    }
}
