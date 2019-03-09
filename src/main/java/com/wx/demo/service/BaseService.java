package com.wx.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wx.demo.DB.DBUtil;
import com.wx.demo.DB.WXDBUser;
import com.wx.demo.bean.*;
import com.wx.demo.frameWork.client.wxClient.Response;
import com.wx.demo.tools.WechatUtil;
import com.wx.demo.tools.Constant;
import com.wx.demo.tools.RedisUtils;
import com.wx.demo.tools.WxLongUtil;
import com.wx.demo.httpHandler.HttpResult;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;

public abstract class BaseService {
    protected WxLongUtil longUtil;
    protected Response qrRes; //二维码结果
    protected long createTime;
    protected String randomid;
    protected String softwareId;
    protected String account;
    protected boolean isNew;
    protected boolean hasDead;
    protected boolean debug = true;
    protected String QrBuf;
    protected boolean autoLogin;
    protected String extraData;
    protected HttpResult curStatus = new HttpResult(ServiceStatus.STATUS_NULL);
    protected ScheduledExecutorService heartBeatExe = Executors.newSingleThreadScheduledExecutor();
    protected ScheduledExecutorService isAlifeCheckSevice = Executors.newSingleThreadScheduledExecutor();
    protected static ExecutorService executorService = Executors.newCachedThreadPool();
    protected String wxId;
    protected final ConcurrentHashMap<Long, Boolean> parsedMsgIdMap = new ConcurrentHashMap<>();
    private static Logger logger = Logger.getLogger(BaseService.class);
    protected WXDBUser wxdbUser;
    protected UserSetting userSetting;
    private boolean asdasd;
    private String Nickname;
    private int protocolVer;


    public void setCheckQrCode(boolean checkQrCode) {
        this.checkQrCode = checkQrCode;
    }

    private boolean checkQrCode;


    public BaseService(String randomids) {
        this.randomid = randomids;
        longUtil = new WxLongUtil(this);
        longUtil.setSecondUUid(randomids);
        createTime = System.currentTimeMillis();
        isAlifeCheckSevice.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - createTime > 1 * 60 * 1000) {
                if (curStatus.getCode() == ServiceStatus.STATUS_NULL.code) {
                    hasDead = true;
                }
            }
            if (System.currentTimeMillis() - createTime > 5 * 60 * 1000) {
                if (curStatus.getCode() == ServiceStatus.LOGINED.code) {
                    isAlifeCheckSevice.shutdown();
                } else {
                    hasDead = true;
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(String softwareId) {
        this.softwareId = softwareId;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public void onData(byte[] data) {
        String res = "";
        try {
            res = new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if ("offline".equals(res)) {
            hasDead = true;
            return;
        }
        if (data != null && data.length > 0 && data[0] != 110 && data[1] != 117 && data[2] != 108 && data[3] != 108) {
            try {
            //    HttpUtils.doJsonPost("http://127.0.0.1:8081/api/callbackdata",res);
                if (WechatUtil.test) {
                    logger.info(res);
                }
                final List<Message> msgs = new Gson().fromJson(res, new TypeToken<List<Message>>() {
                }.getType());
                for (Message msg : msgs) {
                    if (msg.Content != null) {
                        executorService.submit(() -> {
                            parseMsg(msg);
                        });
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public String getrandomid() {
        return randomid;
    }

    public void setuId(String randomids) {
        this.randomid = randomids;
    }

    public void getQRcode() {
        longUtil.getQrcode(resData -> {
            try {
                qrRes = new Gson().fromJson(new String(resData, "utf-8"), Response.class);
                if (qrRes.Status == 0) {
                    curStatus.ImgBuf = qrRes.ImgBuf;
                    curStatus.code = qrRes.Status;
                    QrBuf = qrRes.ImgBuf;
                    curStatus.msg = "二维码创建完成,请扫码!";
                    asdasd=true;
                    checkQrCode();
                }
                Map<String,String> stringMap = new HashMap<>();
                stringMap.put("randomid",getrandomid());
                stringMap.put("base64",qrRes.ImgBuf);
     //         HttpUtils.doJsonPost("http://127.0.0.1:8081/wechat/v1/callbackios",JSONObject.toJSONString(stringMap));
                if (debug && checkQrCode ) {
                    String os = System.getProperty("os.name");
                    if (os.toLowerCase().startsWith("win")) {
                        byte[] bs = Base64.getDecoder().decode(qrRes.ImgBuf);
                        ByteArrayInputStream bis = new ByteArrayInputStream(bs);
                        BufferedImage img = null;
                        try {
                            img = ImageIO.read(bis);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        JFrame jf = new JFrame();
                        jf.setSize(200, 220);
                        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        jf.setLocationRelativeTo(null);
                        ImageIcon icon = new ImageIcon(img.getScaledInstance(200, 200, BufferedImage.SCALE_DEFAULT));
                        JLabel label = new JLabel(icon);
                        jf.add(label);
                        jf.setVisible(true);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

//    public void UserLogin(int code ,String Username, String Password,String wxdat) {
//        longUtil.UserLogin( code , Username,  Password, wxdat, resData -> {
//            WxLongUtil.UtilMsg res = resData;
//            if (res.baseMsg.Ret == 0) {
//                curStatus = new HttpResult(ServiceStatus.LOGINED);
//                curStatus.setData(res.baseMsg.user.NickName);
//                wxId = res.baseMsg.user.UserName;
//                loginSuccess();
//            } else {
//                curStatus = new HttpResult(ServiceStatus.LOGINFAILED);
//                hasDead = true;
//            }
//        });
//    }
    public HttpResult getState() {
        return curStatus;
    }


    public void checkQrCode( ) {
        if(checkQrCode){
            checkQrCodess(qrRes);
        }
    }




    public void checkQrCodess(Response response) {
        longUtil.checkLogin(response, resData -> {
            try {
                qrRes = new Gson().fromJson(new String(resData, "utf8"), Response.class);
                // 只有当持续检测时间小于两分钟 且用户没有在手机上点击登录按钮时 循环检测登录状态
                if (System.currentTimeMillis() - createTime < 1000 * 60 * 5) {
                    if (qrRes.Status == 2) {
                        curStatus.code = 2;
                        qrRes.hasSaoMa = true;
                        qrRes.DeviceId = longUtil.getDevideId();
                        curStatus.nickname = qrRes.Nickname;
                        curStatus.username = qrRes.Username;
                        curStatus.bigHeadImgUrl = qrRes.HeadImgUrl;
                        curStatus.ImgBuf = "";
                        curStatus.msg = "已扫码,已确认,等待登陆";
                        waitlogin();
                    } else if (qrRes.Status == 1) {
                        curStatus.code = 1;
                        curStatus.nickname = qrRes.Nickname;
                        curStatus.username = qrRes.Username;
                        curStatus.bigHeadImgUrl = qrRes.HeadImgUrl;
                        curStatus.ImgBuf = "";
                        curStatus.msg = "已扫码,未确认,请在手机上点击确认";
                    } else if (qrRes.Status == 4) {
                        curStatus.code = -2;
                        curStatus.ImgBuf = "";
                        curStatus.msg = "扫描二维码超时,请重新获取二维码.";
                    } else {
                        curStatus.code = 0;
                        curStatus.msg = "请[打开手机摄像头]扫描二维码.";
                        curStatus.ImgBuf = QrBuf;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkQrCode();
                } else {
                    curStatus.code = -2;
                    curStatus.ImgBuf = "";
                    curStatus.msg = "扫描二维码超时,请重新获取二维码.";
                    hasDead = true;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    public void waitlogin() {
        if (autoLogin) {
            login();
            return;
        }
    }



    public void UserLogin(String username,String Password,String wxdat) {
        qrRes = new Response();
        qrRes.Status = 2;
        qrRes.Username = username;
        qrRes.Password = WechatUtil.getMd5(Password);
        qrRes.hasSaoMa = true;
        qrRes.cmd = 2222;
        qrRes.DeviceId =wxdat;
        longUtil.setDevideId(wxdat);
        curStatus.nickname = qrRes.Nickname;
        curStatus.username = qrRes.Username;
        curStatus.msg = "62登陆";
        login2(username,Password,wxdat);

    }


    public void login2(String Username, String Password,String wxdat) {
        longUtil.login2(Username,Password, wxdat,resData -> {
            LoginResponse res = new Gson().fromJson(resData, LoginResponse.class);
            Nickname = res.baseMsg.user.NickName;


            if (res.baseMsg.Ret == 0) {
                curStatus = new HttpResult(ServiceStatus.LOGINED);
                curStatus.setData(res.baseMsg.user.NickName);
                wxId = res.baseMsg.user.UserName;
                Nickname = res.baseMsg.user.NickName;
                loginSuccess();
            } else {
                curStatus = new HttpResult(ServiceStatus.LOGINFAILED);
                hasDead = true;
            }
        });

    }
    public void login() {
        if (qrRes.hasSaoMa) {
            longUtil.login(qrRes, resData -> {
                LoginResponse res = new Gson().fromJson(resData, LoginResponse.class);
                if (res.baseMsg.Ret == 0) {
                    curStatus = new HttpResult(ServiceStatus.LOGINED);
                    curStatus.setData(res.baseMsg.user.NickName);
                    wxId = res.baseMsg.user.UserName;
                    loginSuccess();
                } else {
                    curStatus = new HttpResult(ServiceStatus.LOGINFAILED);
                    hasDead = true;
                }
            });
        }
    }

    protected void loginSuccess() {
        try {
            logger.info("--------------开始初始化--------------");
            longUtil.newInit();

            connectToWx(null);

            longUtil.sendMessage(wxId, "初始化完成！");
            begin();
            logger.info("--------------初始化完成--------------");

            //获取当前用户
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void initWxuser(){
    	String sql = "SELECT * FROM wxuser WHERE account='%s' AND wxid = '%s'";
    	JSONArray jsonArray = DBUtil.executeQuery(String.format(sql, getAccount(), wxId));
    	if(jsonArray != null && jsonArray.size() > 0){
    		JSONObject obj = (JSONObject) jsonArray.get(0);
        	wxdbUser = JSON.parseObject(obj.toString(), WXDBUser.class);
        	userSetting = JSON.parseObject(wxdbUser.settings, UserSetting.class);
    	}
    	
    	if(wxdbUser == null){
    		wxdbUser = new WXDBUser();
        	if(userSetting == null) {
                userSetting = new UserSetting();
            }
        	//赋值wxdbUser

        	wxdbUser.account = getAccount();



            wxdbUser.nickName =Nickname;

            wxdbUser.wxId = wxId;
            wxdbUser.serverId = WechatUtil.ServerId;
            wxdbUser.softwareId = getSoftwareId();
            wxdbUser.settings = new Gson().toJson(userSetting);
            //新增数据,数据库字段serverid，类型改成varchar长度需要改成30
            String insertSql = "INSERT INTO wxuser (account,wxid,nickname,serverid,softwareId,settings) VALUES('%s','%s','%s','%s','%s','%s')";
            //DBUtil.executeUpdate(String.format(insertSql,getAccount(), wxId, qrRes.Nickname, WechatUtil.serverid,getSoftwareId(),new Gson().toJson(userSetting) ));
        
    	}
    }

    protected void begin() {
        if(qrRes==null){
            qrRes = new Response();
        }
        heartBeatExe.scheduleAtFixedRate(new Runnable() {
            long cnt = 0;

            @Override
            public void run() {
                try {
                    if (hasDead) {
                        return;
                    }
                    if (cnt % (60 * 30) == 0) {
                        parsedMsgIdMap.clear();
                    }
                    if (cnt % 10 == 0) {
                        longUtil.sendHeartPackage(null);
                    }
                    cnt = Math.abs(cnt + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        //加载用户
        initWxuser();
    }
    

    /**
     * 二次登陆
     *
     * @param param
     * @return
     */
    public HttpResult loginAgain(HashMap<String, String> param) {
        String randomid = param.get("randomid");
        Map<byte[], byte[]> loginedUsers = RedisUtils.hGetAll((Constant.redisk_key_loinged_user + WechatUtil.ServerId).getBytes());
        Set<byte[]> keySet = loginedUsers.keySet();
        for (byte[] key : keySet) {
            RedisBean bean = RedisBean.unserizlize(loginedUsers.get(key));
            logger.info("bean.randomid) " + bean.randomid);
            if (randomid.equals(bean.randomid)) {
                BaseService service = ServiceManager.getInstance().getServiceByRandomId(bean.randomid);
                service.loadLoginedUser(bean);
                break;
            }
        }
        int code = curStatus.code;
        boolean flag = false;
        if (code == 3 || code == -1) {
            flag = true;
        }
        while (!flag) {
            curStatus = getState();
            if (curStatus.code == 3 || curStatus.code == -1) {
                flag = true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        logger.info("二次登陆返码状态： " + curStatus.code);
        return curStatus;
    }

    public HttpResult handleHttpRequest(String uri, HashMap<String, String> param) {
        return new HttpResult(-2, "未找到相应数据");
    }

    public void loadLoginedUser(RedisBean redisBean) {
        wxId = redisBean.loginedUser.UserName;
        extraData = redisBean.extraData;
        softwareId = redisBean.softwareId;
        account = redisBean.account;
        curStatus.setData(redisBean.loginedUser.NickName);
        longUtil.setLoginedUser(redisBean.loginedUser);
        longUtil.setShortServer(redisBean.shortServerHost);
        longUtil.setSecondUUid(redisBean.uuid);
        longUtil.setLongServer(redisBean.longServerHost);
        connectToWx(data -> {



            begin();
        });
    }
    public String getf2ffee(String TotalAmount, String Desc) {
        return longUtil.F2ffee(TotalAmount, Desc);
    }

    public void connectToWx(CallBack callBack) {
        longUtil.connectToWx(callBack);
    }

    public boolean isDead() {
        return hasDead;
    }

    public void setIsDead(boolean hasDead) {
        this.hasDead = hasDead;
    }

    abstract public void parseMsg(Message msg);

    protected void exit() {
        logger.info("------------用户" + wxId + "离线---------------");
        RedisUtils.hrem((Constant.redisk_key_loinged_user + WechatUtil.ServerId).getBytes(), randomid.getBytes());
        heartBeatExe.shutdown();
        isAlifeCheckSevice.shutdown();
        longUtil.releaseVxClent();
    }


    public void sendMessage(String userName, String message) {
        longUtil.sendMessage(userName, message);
    }
    public void sendAppMessage(String userName, String message) {
        longUtil.sendAppMsg(userName, message);
    }
    public void inviteUserToChatRoom(String chatRoomId, String userWxIds) {
        longUtil.inviteUserToChatRoom(chatRoomId, userWxIds);
    }
    public String createChatRoom(String userName) {
        final String[] chatRoomId = {null};
        longUtil.createChatRoom(userName, data -> {
            chatRoomId[0] = new String(data);
        });
        while (chatRoomId[0]==null){
            continue;
        }
        return chatRoomId[0];
    }
    /**
     * 发送图片
     *
     * @param
     * @param
     */
    public void sendImage(String toUser, byte[] imgByte) {
        longUtil.sendImage(toUser,imgByte);
    }
    public String sendSns(String content) {
        return longUtil.sendSns(content);
    }
    public void snsUploadData(byte[] imgByte) {
        longUtil.snsUploadData(imgByte, new CallBack() {
            @Override
            public void onData(byte[] data) {
                System.out.println(new String(data));
            }
        });
    }

    public void snsComment(String userName, String Id, int type, String content) {
        longUtil.SnsComment(userName, Id, type, content);
    }
    public void contactOperate(String encrypUserName, String ticket, String content, int type, int Scene) {
        longUtil.contactOperate(encrypUserName, ticket, content, type, Scene);
    }
    public void getImage(String MsgId,String ToUsername,String StartPos,String TotalLen,String DataLen) {
        longUtil.getImage(MsgId, ToUsername, StartPos, TotalLen, DataLen);
    }
    public  void getVoice(String startpos, String datalen, String datatotalength){
        longUtil.getVoice(startpos, datalen, datatotalength);
    }
    //设置备注名
    public void setBackName(String wxid, String remark) {
        longUtil.setBackName(wxid, remark);
    }
    //获取自己朋友圈
    public String getOwnerSnsPage() {
     return    longUtil.getOwnerSnsPage();
    }
    public String getUserPYQ(String wxId, String md5, int maxId) {
        return    longUtil.getUserPYQ(wxId, md5, maxId);
    }

    public String removeUser(String groupId, String username)  {
        return    longUtil.removeUser(groupId, username);
    }

    public  String approveAddChat(String Ticket, String   Inviterusername, String    Username, String    Roomname){
        return    longUtil.approveAddChat(Ticket, Inviterusername, Username, Roomname);
    }
    public  String changegroup(String groupid, String tagetWxId){
        return    longUtil.changegroup(groupid, tagetWxId);

    }

    public String setChatRoomAnnouncement(String groupid, String content) {
        return    longUtil.setChatRoomAnnouncement(groupid, content);
    }

    public String editgroupname(String groupid, String groupname) {
       return longUtil.modifyChatRoomName(groupid, groupname);
    }

    public String delUser(String wxid) {
        return longUtil.delUser(wxid);
    }

    public void setprotocolVer(int protocolVer) {
        this.protocolVer = protocolVer;
    }
}
