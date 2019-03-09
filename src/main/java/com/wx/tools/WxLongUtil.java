package com.wx.tools;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.utils.JSONUtils;
import com.bootdo.common.utils.StringUtils;
import com.bootdo.util.HxHttpClient;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.wx.bean.CallBack;
import com.wx.bean.RedisBean;
import com.wx.frameWork.client.grpcClient.GrpcPool;
import com.wx.frameWork.client.wxClient.Response;
import com.wx.frameWork.client.wxClient.SocketClient;
import com.wx.frameWork.proto.BaseMsg;
import com.wx.frameWork.proto.User;
import com.wx.frameWork.proto.WechatMsg;
import com.wx.frameWork.proto.WechatMsg.Builder;

import static com.wx.httpHandler.HttpResult.getMd5;

import com.wx.service.BaseService;
import org.apache.coyote.http2.Setting;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class WxLongUtil {
    private SocketClient vxClient;
    public static byte[] sessionKey = new byte[]{80, 117, -128, 85, 2, 55, -76, 126, -115, 93, -71, -36, 112, -114, 15, -128};
    private String secondUUid = UUID.randomUUID().toString().toUpperCase();
    private String devideId = ConfigService.getMd5(secondUUid.getBytes());
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
        vxClient = new SocketClient(longServer, 80, () -> secondLogin(), () -> async(), callBack);
    }

    public void sendAppMsg(String userName, String content) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ToUserName", userName);
        params.put("Content", content);
        params.put("Type", 5);
        params.put("AppId", "");
        longServerRequest(222, params, data -> {
            logger.info(Arrays.toString(data));
        });
    }

    public void getQrcode(CallBack callBack) {

        longServerRequest(502, null, callBack);
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
        loginData.put("DeviceName", "shangan 的 ipad");
        loginData.put("ProtocolVer", 5);

        UtilMsg reqMsg = new UtilMsg();
        reqMsg.Version = Settings.getSet().version;
        reqMsg.TimeStamp = System.currentTimeMillis() / 1000;
        reqMsg.ip = ip;
        reqMsg.Token = Settings.getSet().machineCode;
        reqMsg.baseMsg.Cmd = 1111;
        try {
            reqMsg.baseMsg.PayLoads = new Gson().toJson(loginData).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        reqMsg.baseMsg.user.SessionKey = sessionKey;
        reqMsg.baseMsg.user.DeviceId = devideId;
        WechatMsg convert = convert(reqMsg);
        WechatMsg logRes = helloWechat(convert);
        final UtilMsg resMsg = convert(logRes);
        // 发送请求给腾讯
        byte[] bys = getBuffers(logRes);
        vxClient.asynSend(bys, data -> {
            if(data.length>16&&data[16]==-65) {
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
            }
        });
    }

    private WechatMsg helloWechat(WechatMsg msg) {
        return GrpcPool.getInstance().helloWechat(msg);
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
    public void modifyChatRoomName(String wxid, String name) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Cmdid", 27);
        params.put("ChatRoom", wxid);
        params.put("Roomname", name);
        shortServerRequest(681, params);
    }

    //删除好友
    public void delUser(String wxid) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Cmdid", 7);
        params.put("CmdBuf", wxid);
        shortServerRequest(681, params);
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
            byte[] data = bos.toByteArray();
            if(data.length>0&&data[0]==-65) {
                utilMsg.baseMsg.PayLoads = data;
                utilMsg.baseMsg.Cmd = -code;
                utilMsg = convert(helloWechat(convert(utilMsg)));
                return new String(utilMsg.baseMsg.PayLoads, "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getBuffers(WechatMsg msg) {
        int headLength = msg.getBaseMsg().getLongHead().toByteArray().length;
        byte[] bys = new byte[(headLength < 16 ? 16 : headLength) + msg.getBaseMsg().getPayloads().toByteArray().length];
        byte[] head = msg.getBaseMsg().getLongHead().toByteArray();
        byte[] body = msg.getBaseMsg().getPayloads().toByteArray();
        System.arraycopy(head, 0, bys, 0, head.length);
        System.arraycopy(body, 0, bys, 16, body.length);

        int t = Math.abs(new Random().nextInt());
        byte[] tbs = new byte[4];
        tbs[3] = (byte) (t & 0xFF);
        tbs[2] = (byte) ((t >> 8) & 0xFF);
        tbs[1] = (byte) ((t >> 16) & 0xFF);
        tbs[0] = (byte) ((t >> 24) & 0xFF);
        System.arraycopy(tbs, 0, bys, 12, 4);
        return bys;
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
        longServerRequest(213, params, null);
    }

    //1删除朋友圈2设为隐私3设为公开4删除评论5取消点赞
    public void snsObjectOp(String ids, int type) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Ids", ids);// 逗号分隔 要操作的朋友圈内容
        params.put("Type", type);//1删除朋友圈2设为隐私3设为公开4删除评论5取消点赞
        longServerRequest(218, params, null);
    }

    //获取自己朋友圈
    public void getOwnerSnsPage(CallBack callBack) {
        longServerRequest(214, null, callBack);
    }

    //发送朋友圈
    public void sendSns(String content) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Content", content);
        longServerRequest(209, params, null);
    }

    public void snsTimeLine(String firstMd5, int lasterId, CallBack callBack) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("FirstPageMd5", firstMd5);
        params.put("ClientLatestId", lasterId);
        longServerRequest(211, params, callBack);
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
    public void removeUser(String groupId, String username) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ChatRoom", groupId);
        params.put("Username", username);
        shortServerRequest(179, params);
    }

    public void addLabel(String labelName) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("LabelName", labelName);
        shortServerRequest(635, params);
    }

    /**
     * 识别二维码可实现扫码入群
     * 自动加群
     * 公众号阅读Key的获取
     * @param reqUrl 要获取key的连接 授权登陆时的链接即为转跳链接https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx53c677b55caa45fd&redirect_uri=http%3A%2F%2Fmeidang.cimiworld.com%2Fh5%2Fchourenpin%3Fs%3D77e881961fee12eb65f5497bbff02fac%26from%3Dsinglemessage%26isappinstalled%3D0&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect
     * @param scene 2 来源好友或群 必须设置来源的id 3 历史阅读 4 二维码连接 7 来源公众号 必须设置公众号的id
     * @param username 来源 来源设置wxid 来源群id@chatroom 来源公众号gh_e09c57858a0c原始id
     */
    public String getA8Key(String reqUrl,int scene,String username) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ReqUrl", reqUrl);
        params.put("Scene", scene);//Scene = 2 来源好友或群 必须设置来源的id 3 历史阅读 4 二维码连接 7 来源公众号 必须设置公众号的id
        params.put("Username", username);//来源 来源设置wxid 来源群id@chatroom 来源公众号gh_e09c57858a0c原始id
        params.put("ProtocolVer",1);//ProtocolVer 1-5

        String reqJson = "";
        try {
            String a8kJson=shortServerRequest(233, params);
            Map<String, Object> a8kMap = JSONUtils.jsonToMap(a8kJson);
            if (a8kMap.size() > 0) {
                Map<String,String> readReq=Maps.newHashMap();
                StringBuilder fullUrl=new StringBuilder();
                fullUrl.append(a8kMap.get("Url").toString());
                String XWechatUin=a8kMap.get("XWechatUin").toString();
                String XWechatKey=a8kMap.get("XWechatKey").toString();
                if (StringUtils.isNotBlank(XWechatUin)) {
//                    fullUrl.append("&X-WECHAT-UIN=" + XWechatUin);
                    readReq.put("uin", XWechatUin);
                }
                if (StringUtils.isNotBlank(XWechatKey)) {
//                    fullUrl.append("&X-WECHAT-KEY=" + XWechatKey);
                    readReq.put("key", XWechatKey);
                }
                reqJson=HttpUtil.sendPostRead(fullUrl.toString(), readReq);
//                reqJson=HxHttpClient.postBytes(fullUrl.toString(), JSONUtils.beanToJson(readReq));
                return reqJson;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reqJson;
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
    public void getUserPYQ(String wxId, String md5, int maxId, final CallBack call) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("FirstPageMd5", md5);//首页为空 第二页请附带md5
        params.put("Username", wxId);
        params.put("MaxId", maxId);//首页为0 次页朋友圈数据id 的最小值
        longServerRequest(212, params, call);
    }

    public void longServerRequest(int code, HashMap<String, Object> paramss, CallBack call) {
        HashMap<String, Object> params = paramss;
        if (params == null){
            params = new HashMap<String, Object>();
            params.put("ProtocolVer", 1);
        }
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
            byte[] buffers = getBuffers(res);
            vxClient.asynSend(buffers, data -> {
                if (data.length == 47 && data[16] == 126) {//下线或者sessionkey过期
                    System.out.println("---->>>进行离线判断"+data +"<<<----");
                    longServer = ConfigService.longServerHost;
                    baseService.setIsDead(true);
                    connectToWx(data1 -> secondLogin());
                    return;
                }

                if(data.length>16&&data[16]==-65) {
                    if (call != null) {
                        UtilMsg req = convert(res);
                        req.baseMsg.Cmd = code == 211 ? -212 : -code;
                        req.baseMsg.PayLoads = data;
                        req = convert(helloWechat(convert(req)));
                        loginedUser = req.baseMsg.user;
                        call.onData(req.baseMsg.PayLoads);
                    }
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
        params.put("ProtocolVer", 3);
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
            params.put("ToListMd5", ConfigService.getMd5(wxIds.getBytes("utf-8")));
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
        if (loginedUser == null) {
            return;
        }
        String deviceType = "<k21>TP_lINKS_5G</k21><k22>中国移动</k22><k24>" + CommonUtil.getMac(devideId) + "</k24>";
        HashMap<String, Object> loginData = new HashMap<String, Object>();
        loginData.put("UUid", secondUUid);
        loginData.put("DeviceType", deviceType);
        loginData.put("DeviceName", "shangan 的 ipad");
        loginData.put("ProtocolVer", 1);

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
        byte[] bys = getBuffers(logRes);
        vxClient.asynSend(bys, data -> {
            resMsg.baseMsg.Cmd = -702;
            resMsg.baseMsg.PayLoads = data;

            WechatMsg res = helloWechat(convert(resMsg));
            UtilMsg loginedRes = convert(res);
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
        RedisUtils.hset((Constant.redisk_key_loinged_user + ConfigService.serverid).getBytes(), baseService.getrandomid().getBytes(), RedisBean.serialise(redisBean));
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
