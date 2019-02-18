package com.wx.service;

import com.alibaba.fastjson.JSON;
import com.bootdo.common.redis.shiro.RedisCacheManager;
import com.bootdo.common.redis.shiro.RedisManager;
import com.bootdo.common.utils.SpringContextHolder;
import com.bootdo.wx.domain.WxuserDO;
import com.bootdo.wx.service.WxuserService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wx.DB.WXDBUser;
import com.wx.bean.CallBack;
import com.wx.bean.LoginResponse;
import com.wx.bean.Message;
import com.wx.bean.RedisBean;
import com.wx.bean.UserSetting;
import com.wx.frameWork.client.wxClient.Response;
import com.wx.httpHandler.HttpResult;

import static com.wx.httpHandler.HttpResult.getMd5;

import com.wx.tools.*;

import static com.wx.tools.ConfigService.getMac;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;

public abstract class BaseService {
    private static Logger logger = Logger.getLogger(BaseService.class);
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

    protected WXDBUser wxdbUser;
    protected UserSetting userSetting;

    private RedisManager redisManager;
    private RedisCacheManager redisCacheManager;

    public RedisCacheManager getRedisCacheManager() {
        return redisCacheManager;
    }

    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public BaseService(String randomids) {//这里是每个微信号，不管逻辑是什么都要执行的一些函数，是必走的流程，所以放在基类里，等这些全部走完进入到心跳，待命的时候，就回到了刚才的父类里，去执行不同的逻辑
        this.randomid = randomids;
        longUtil = new WxLongUtil(this);
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
                if (ConfigService.test) {
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

                    RedisUtils.set("qrcode",qrRes.ImgBuf);

                    curStatus.ImgBuf = qrRes.ImgBuf;
                    curStatus.code = qrRes.Status;
                    QrBuf = qrRes.ImgBuf;
                    curStatus.msg = "二维码创建完成,请扫码!";//你现在的问题是卡在哪里，你跑下我看看
                    if(ConfigService.test){
                        //javaSE窗口先注释
//                        QrcodeWindows.getQrcode(curStatus.msg,qrRes.ImgBuf);
                    }
                    checkQrCode(qrRes);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }
    public HttpResult getState() {
        return curStatus;
    }

    public void checkQrCode(Response response) {
        longUtil.checkLogin(response, resData -> {
            try {
                qrRes = new Gson().fromJson(new String(resData, "utf8"), Response.class);
                // 只有当持续检测时间小于两分钟 且用户没有在手机上点击登录按钮时 循环检测登录状态
                if (System.currentTimeMillis() - createTime < 1000 * 60 * 5) {
                    logger.info(qrRes);
                    if (qrRes.Status == 2) {
                        curStatus.code = 2;
                        qrRes.hasSaoMa = true;
                        curStatus.nickname = qrRes.Nickname;
                        curStatus.username = qrRes.Username;
                        curStatus.bigHeadImgUrl = qrRes.HeadImgUrl;
                        curStatus.ImgBuf = "";
                        curStatus.msg = "已扫码,已确认,等待登陆";
//                        QrcodeWindows.overQrcode();
                        waitlogin();
                    } else if (qrRes.Status == 1) {
                        curStatus.code = 1;
                        curStatus.nickname = qrRes.Nickname;
                        curStatus.username = qrRes.Username;
                        curStatus.bigHeadImgUrl = qrRes.HeadImgUrl;
                        curStatus.ImgBuf = "";
                        curStatus.msg = "已扫码,未确认,请在手机上点击确认";
                    } else if (qrRes.Status == 4) {
//                        QrcodeWindows.overQrcode();
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
                    checkQrCode(qrRes);
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

    public void login() {//这就是api调用，需要先到这个基类然后从基类调用接口，
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
        Map<String,Object> map= Maps.newHashMap();
        map.put("account", getAccount());
        map.put("wxid", wxId);
        WxuserService wxuserService=SpringContextHolder.getBean(WxuserService.class);
        List<WxuserDO> list = wxuserService.list(map);
        if(list != null && list.size() > 0){
            WxuserDO obj = list.get(0);
            if (wxdbUser==null) {
                wxdbUser=new WXDBUser();
            }
            wxdbUser.account=obj.getAccount();
            wxdbUser.settings=obj.getSettings();
            wxdbUser.nickName = obj.getNickname();
            wxdbUser.serverId = obj.getServerid();
            wxdbUser.softwareId=obj.getSoftwareid();
            wxdbUser.wxId = obj.getWxid();
            wxdbUser.deadTime=obj.getDeadtime()!=null?obj.getDeadtime():0;
            wxdbUser.token= obj.getToken()!=null?obj.getToken():null;
            wxdbUser.userCode=obj.getUsercode();
            wxdbUser.wxDat=obj.getWxdat()!=null?obj.getWxdat():null;
            userSetting = JSON.parseObject(wxdbUser.settings, UserSetting.class);
        }

    	if(wxdbUser == null){
    		wxdbUser = new WXDBUser();
        	if(userSetting == null)userSetting = new UserSetting();
        	//赋值wxdbUser
            if (qrRes==null) {
                qrRes = new Response();
                qrRes.Nickname = "缺省";
            }
        	wxdbUser.account = getAccount();
        	wxdbUser.nickName = qrRes.Nickname;
            wxdbUser.wxId = wxId;
            wxdbUser.serverId = ConfigService.serverid;
            wxdbUser.softwareId = getSoftwareId();
            wxdbUser.settings = new Gson().toJson(userSetting);

            WxuserDO wxuserDO=new WxuserDO();
            wxuserDO.setAccount(wxdbUser.account);
            wxuserDO.setNickname(wxdbUser.nickName);
            wxuserDO.setWxid(wxdbUser.wxId);
            wxuserDO.setServerid(wxdbUser.serverId);
            wxuserDO.setSoftwareid(softwareId);
            wxuserDO.setSettings(wxdbUser.settings);
            wxuserDO.setStatus(1);
            wxuserDO.setRandomid(randomid);

            wxuserService.save(wxuserDO);
            //新增数据,数据库字段serverid，类型改成varchar长度需要改成30
//            String insertSql = "INSERT INTO wxuser (account,wxid,nickname,serverid,softwareId,settings,randomid,status) VALUES('%s','%s','%s','%s','%s','%s','%s','%s')";
//            DBUtil.executeUpdate(String.format(insertSql,getAccount(), wxId, qrRes.Nickname, ConfigService.serverid,getSoftwareId(),new Gson().toJson(userSetting) ,randomid,1));
    	}
    }
    protected void begin() {
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
        Map<byte[], byte[]> loginedUsers = RedisUtils.hGetAll((Constant.redisk_key_loinged_user + ConfigService.serverid).getBytes());
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
        if (code == 3 || code == -1) flag = true;
        while (!flag) {
            curStatus = getState();
            if (curStatus.code == 3 || curStatus.code == -1) flag = true;
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
        longUtil.setShortServerHost(redisBean.shortServerHost);
        longUtil.setSecondUUid(redisBean.uuid);
        longUtil.setLongServerHost(redisBean.longServerHost);
        connectToWx(data -> {
            begin();
        });
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
        RedisUtils.hrem((Constant.redisk_key_loinged_user + ConfigService.serverid).getBytes(), randomid.getBytes());
        heartBeatExe.shutdown();
        isAlifeCheckSevice.shutdown();
        longUtil.releaseVxClent();
    }

    public void sendMessage(String userName, String message) {
        longUtil.sendMessage(userName, message);
    }

    public HttpResult getname(HttpResult HttpResult) {
        HttpResult.code = -1;
        HttpResult.msg = "初始化数据";
        HttpResult.data = "";
        HttpResult.serverip = Settings.getSet().server_ip;
        HttpResult.serverport = Settings.getSet().server_port;
        HttpResult.serverid = getMd5(HttpResult.serverip + ":" + HttpResult.serverport);
        HttpResult.ImgBuf = "";
        HttpResult.createTime = System.currentTimeMillis() / 1000;
        HttpResult.Token = Settings.getSet().machineCode;
        HttpResult.TimeStamp = System.currentTimeMillis() / 1000;
        HttpResult.Version = Settings.getSet().version;
        HttpResult.ServerIp = Settings.getSet().localIp;
        HttpResult.LongHost = Settings.getSet().longServer;
        HttpResult.ShortHost = Settings.getSet().shortServer;
        HttpResult.ProtocolVer = 1;
        HttpResult.DeviceUuid = UUID.randomUUID().toString().toUpperCase();
        HttpResult.SessionKey = new byte[]{80, 117, -128, 85, 2, 55, -76, 126, -115, 93, -71, -36, 112, -114, 15, -128};
        HttpResult.DeviceId = getMd5(HttpResult.DeviceUuid);
        HttpResult.DeviceType = "<k21>TP_lINKS_5G</k21><k22>中国移动</k22><k24>" + getMac(HttpResult.DeviceId) + "</k24>";
        String[] name = {"千渔", "小驭", "开娥", "思宏", "启然", "子鑫", "张今", "中文", "高炎", "永匀", "晨荣", "辉君", "宏泽", "明莲", "昊钟", "文鸿", "骏意", "泓超", "漾东", "仕松", "丽菱", "观桓", "悠君", "子兵", "医水", "之石", "丰隽", "亚林", "炅学", "梅宇", "同结", "华明", "汶洲", "悠梅", "昕腾", "才涛", "同丽", "依飞", "雨勇", "泓瑶", "彦贞", "红强", "皙萱", "建英", "福轩", "业丽", "佳芮", "梓怡", "学美", "盛平", "剑开", "芮仪", "泞惠", "张璞", "点丽", "思崴", "姿宇", "恩才", "青霏", "张愉", "维叶", "秀诚", "一宾", "相时", "昱哲", "采铭", "翌天", "海岳", "亮杰", "存原", "雁舒", "乐锋", "韶惠", "亚博", "旎婷", "梓远", "十锦", "丹祥", "季霖", "义涵", "雄月", "俊竣", "琼铭", "秋茵", "亦熠", "晨华", "俞麾", "琦娟", "瀚梅", "凡方", "皓仪", "春晶", "辰冉", "梓润", "蒋畅", "蒋贺", "君浩", "俊博", "小萱", "意辉", "蒋箐", "蒋锦", "迩谷", "蒋坤", "俊琪", "新红", "全领", "增佳", "蒋庸", "蒋斐", "开力", "文兴", "天彬", "士倩", "蒋克", "蒋婉", "锦明", "蒋晏", "泉良", "臻设", "程林", "咏爱", "鹏明", "耀儒", "韩博", "艺欣", "巨麟", "良珍", "虹胜", "潇宏", "安彤", "亦良", "润全", "立棠", "自芮", "乃盈", "蒋霓", "桂骏", "雅凤", "蒋斌", "亦鹏", "昀熙", "政玉", "蒋颜", "晓希", "宗丽", "浩钰", "小微", "蒋奕", "桢宸", "中今", "朝仪", "晓新", "子铄", "星舒", "子语", "怡晴", "春德", "蒋姗", "金煜", "帅雯", "贞元", "俏瞳", "丽铭", "蒋军", "春荷", "岩家", "蒋蕴", "梓睿", "晓彤", "雨萍", "涵涛", "佳雯", "雍宇", "佳嘉", "宝萌", "蒋迪", "蒋锌", "蒋坡", "蒋金", "广琪", "双明", "嘉葭", "泓杰", "蒋宁", "雨坤", "马芯", "钰林", "玉景", "春淇", "友成", "马梅", "马麟", "程辉", "荣平", "尉文", "丽杨", "晟玉", "怡菡", "凯诚", "誉红", "彦潼", "逸霞", "梓杭", "夕苗", "马煦", "思海", "米宵", "竞阳", "大羽", "爱冰", "亚茜", "克鑫", "一涵", "志玉", "瀛鑫", "树芝", "妸轩", "雨东", "凤帆", "颖亮", "上枫", "宏杰", "俊伟", "马蕾", "煜杰", "秦莹", "剑轩", "知亮", "宇婷", "煜元", "婧莲", "冲卫", "马之", "晚鹏", "鑫希", "静岚", "泊标", "中玲", "鹏艳", "恩华", "靖文", "马芳", "岽楷", "龙慧", "心紫", "思佳", "丹杰", "佳冰", "玳娜", "马了", "家涵", "应洪", "小欲", "秀治", "增琪", "马渔", "嘉实", "嗣华", "思颖", "永婷", "兴心", "马磬", "马媛", "修辰", "墨杰", "志雨", "洪思", "涪辉", "恩玲", "鸿泰", "梦玟", "马漩", "晓娟", "马淄", "向煌", "木文", "明杰", "景军", "紫君", "善晶", "房真", "昊松", "房微", "丁佳", "浩荃", "紫童", "杰舜", "良翔", "少芮", "建涵", "仁鸣", "房傧", "晓隽", "彤媛", "房祁", "福畅", "房亮", "翊鸽", "书宇", "淑泽", "房俊", "泰六", "思榕", "秀媚", "佳然", "誉霏", "伟海", "具军", "河涵", "小文", "竹芳", "雨聍", "怀荷", "彧明", "承彤", "睿淮", "冠义", "敏震", "宇涛", "兰坚", "筵明", "曾烁", "寰棉", "缜华", "世旎", "映林", "敬绩", "曾凝", "卓涵", "曾畅", "俊锋", "腊婷", "曾尚", "湛洋", "梅颖", "雅亮", "鸾凯", "雨梅", "曾城", "香波", "鸣佳", "香文", "悦薇", "文来", "智桥", "珍朵", "霆英", "天涵", "饴芹", "成红", "梓轩", "佳宇", "曾芙", "明帆", "子惠", "文芸", "青澄", "胜砚", "嘉旭", "曾超", "一迪", "曾莉", "梓渊", "昱博", "曾广", "锦鸣", "庆坤", "曾茸", "殷琨", "玉峰", "曾亮", "昶邑", "城涵", "梓含", "明莉", "秀一", "梦如", "彬晨", "云淼", "洪心", "哲钰", "鹏轩", "佳涛", "梦然", "冠樱", "驿轩", "雅媚", "曾瑜", "婉沁", "红源", "佳泽", "文馨", "嘉琳", "兆蕾", "晓健", "曾波", "睿燕", "曾原", "曾泺", "明浩", "曾彤", "曾钥", "雅岚", "凯勋", "曾乔", "韩爽", "瑞宇", "韩缘", "勐爱", "昌彬", "文鑫", "文尧", "雨舒", "祯智", "焯杰", "石泉", "则凡", "媛硕", "君升", "铭莹", "庄伟", "雨华", "韩婕", "尚珏", "韩钦", "彦荣", "小冠", "鑫齐", "惠聪", "韩于", "毓尔", "建平", "彬锛", "韩雨", "展恒", "韩烨", "晓明", "韩琼", "韩龙", "韩潼", "栩轩", "千宾", "怡龙", "韩七", "华弘", "韩童", "韩瑞", "丽豪", "韩旗", "韩晨", "韩维", "林谆", "宝宁", "新娇", "艺青", "韩梅", "丽丛", "昭亮", "桃恒", "永子", "韩妤", "学仁", "军霞", "韩静", "进锐", "嘉平", "秀清", "得林", "薇鸣", "梅渊", "庆怡", "佳升", "宏明", "鸣迪", "馨伦", "子瑶", "贵睿", "琼晨", "大民", "丽真", "希英", "彦武", "顺靖", "韩正", "羽辉", "呈恬", "沂涵", "立童", "闰乐", "敬淋", "妙绚", "叶耕", "瑾桓", "韩逸", "家宁", "凤仿", "瀚涛", "韩焱", "淇燮", "恩骏", "美翔", "泽江", "真谣", "郑萱", "傲云", "小涵", "郑跃", "红珊", "延明", "心云", "毛羲", "阔溪", "世涵", "茹芙", "龙怡", "春蕊", "思慧", "瑞华", "咏静", "锡达", "若入", "文宇", "虹婷", "作淇", "奕宾", "郑菁", "麒永", "永晗", "圣轩", "痍熹", "俊英", "媛瑾", "模彤", "法右", "建骋", "涵雅", "朝萍", "郑正", "娟苒", "斐龙", "城恩", "思予", "群弈", "鹏虎", "宜谦", "木智", "大修", "增澳", "郑菲", "锦萍", "思婷", "宝霞", "郑妤", "奕东", "绍明", "郑德", "滔菱", "正涵", "佳子", "智静", "怡适", "帅蕙", "郑迅", "小昊", "语师", "东瑶", "郑榕", "伟涵", "得宇", "东璇", "惠骏", "长璁", "郑柔", "晟娜", "艳豪", "郑彭", "三渝", "济平", "少几", "科生", "路昕", "郑赢", "羽茗", "沛硕", "郑恺", "艺池", "郑卉", "忠兰", "易晗", "玉馨", "占玲", "铖雨", "学英", "姚森", "楠蘅", "玉婷", "姚霭", "莉滨", "景诗", "福红", "姚柯", "小淼", "意瑞", "蓓平", "凯闻", "剑彤", "姚钺", "学燕", "应华", "国畅", "安烁", "晨臻", "荣富", "世文", "泽玉", "茂驿", "可瑜", "志捷", "姚盛", "奕哲", "洪轩", "姚婧", "曦伊", "全伟", "韵军", "咏哲", "蒙佟", "楚芳", "姚超", "姚元", "博众", "一琳", "青林", "跃甲", "姚乐", "园宇", "耀修", "银源", "铭元", "慧彦", "晋恬", "稚辉", "赫秦", "志哲", "明蓉", "睿龙", "月灏", "姚火", "鑫哲", "耀鹏", "银琪", "宛吉", "姚熙", "蔓洁", "思鹏", "松雯", "建雄", "沅祥", "姚宽", "庆芸", "云卿", "姚璇", "静晗", "楚茜", "柯诚", "锦玉", "姚流", "姚拓", "基宝", "姚薇", "子莹", "红萌", "澜梅", "惠平", "姚馨", "晟伟", "占耀", "姚婕", "歆雄", "佳翩", "明玲", "启晓", "柯辉", "姚含", "红怡", "忠淼", "春扬", "熠滨", "乔东", "弈威", "丹平", "明华", "铎婉", "芷民", "伍骏", "睿琳", "泠怡", "素磊", "语恒", "书辰", "杰杰", "亭涛", "溥林", "瀚飞", "玉然", "子松", "丽璐", "梦玲", "唐静", "贤雪", "一鑫", "唐文", "唐锋", "龙芳", "宛虹", "彤萍", "彦梅", "继梅", "唐上", "小玲", "擎伟", "伊蔓", "疏春", "唐桐", "晶平", "乃丽", "唐苗", "语曦", "奕发", "元星", "玉蒙", "思莹", "香丞", "祥泳", "舍文", "明辉", "琦乐", "秀品", "逸桔", "唐莫", "锦栓", "朔涵", "慕荣", "钻予", "庆伟", "延浩", "睿妍", "欣芝", "卫文", "子琪", "唐遥", "邵岩", "睿屏", "世华", "景娇", "骏根", "倩侠", "宗文", "乙薇", "静林", "唐星", "馨文", "嘉莹", "雯缓", "艳衡", "晓微", "婧杰", "涵新", "小波", "大榕", "笑福", "唐瑶", "小林", "金琴", "淳斌", "栩浩", "唐哲", "毅航", "名漪", "泽翔", "露瑜", "逸雯", "然彤", "泽凡", "姜兰", "为生", "博柳", "豪静", "姜煜", "胤智", "艺君", "建吉", "翔玲", "祉财", "筱辉", "军欣", "享祖", "翰生", "军雄", "纾明", "炫伟", "艺轩", "儒楠", "学涟", "姜励", "姜煊", "元铭", "康花", "德贤", "子鑫", "土洋", "冠腾", "建懿", "泽明", "宇基", "姿元", "保嘉", "强锋", "嘉珍", "姜奕", "晓源", "姜渝", "缦萌", "姜芳", "当甜", "人林", "垠琪", "佳茜", "秋菲", "紫淞", "惠翔", "建雅", "晓宁", "曾薇", "荣英", "姜鹂", "友岑", "竞岩", "丽坡", "姜芹", "京雨", "姜照", "国钦", "婀祁", "竞丽", "与煌", "姜烜", "希学", "七杰", "新柳", "尚聪", "荧平", "怡栩", "晨霖", "秭安", "姜淼", "宝睿", "宸源", "毛婷", "鸿晨", "姜砚", "璨芬", "政芬", "释媛", "嘉睫", "来立", "敦桦", "子健", "姜江", "悛洁", "庄敏", "艾兰", "瑞峰", "巍汶", "纪森", "振珂", "辛峰", "乐乜", "清童", "飞康", "家荣", "睿成", "思英", "和欣", "程焕", "骅文", "君琪", "辛凌", "昭妍", "旭文", "辛睿", "致婷", "思峰", "垄宇", "绘琛", "禾腾", "芷阳", "辛俊", "清予", "辛晔", "辛珏", "文华", "加鸢", "辛程", "嘉博", "嘉铭", "忠惠", "彦诺", "辛冬", "业栋", "晓涛", "显换", "小桦", "一佳", "曾心", "聿旺", "惠君", "麓桐", "齐棚", "齐远", "桂超", "胺晨", "晋硕", "雨轩", "翰平", "梓臻", "笑娴", "涵丰", "骢潍", "海风", "泽城", "雨军", "子平", "俊晔", "锦远", "靖心", "明萱", "亮萱", "汉轩", "凯涵", "法墨", "瑾奕", "淑铭", "晓源", "钰泫", "律如", "佑田", "齐千", "海燕", "子涵", "海阳", "曜茜", "艺君", "齐佳", "康聆", "诗珂", "朝东", "可洋", "泽呈", "齐昱", "雨鑫", "炳枫", "齐笛", "齐睫", "昌昙", "齐秀", "甘骊", "一毅", "思炜", "胤昊", "坤琪", "艾霖", "子铧", "千勤", "理洋", "齐帆", "齐婕", "紫诚", "洵宜", "丹荧", "阿熙", "若涓", "家雯", "齐晖", "正展", "雨翔", "宇朵", "媛建", "宇戈", "丰婷", "骏宸", "子祥", "文芹", "子胜", "玺帅", "明潭", "新甫", "千华", "慧震", "瑞茹", "星升", "若霞", "玉城", "健玲", "玉芸", "海夫", "鸿阳", "振婷", "诚涵", "思林", "水轩", "夏默", "嘉运", "聪烨", "雅庭", "澄林", "冬良", "巴菱", "叶林", "树亚", "海鹰", "慧霖", "太金", "雨晨", "橹涵", "可辛", "裕谨", "子慧", "勋怡", "泓馨", "惠茹", "明谊", "子芸", "江识", "葱阳", "夏薇", "一格", "云悦", "云玉", "新荣", "夏悠", "永木", "谈芳", "兰岳", "夏凡", "海昶", "乐霖", "田翔", "双方", "宇妮", "李彬", "予清", "建晨", "鸿林", "夏东", "相炅", "夏鲜", "夏寒", "瑗非", "夏与", "夏至", "文铭", "梓乾", "元素", "靖霖", "夏烨", "子平", "佳琳", "沛红", "夏果", "芷莉", "万蓓", "春豫", "煜滢", "钰舒", "黎月", "永强", "明润", "丞宇", "雯雅", "雁惠", "涵菱", "舒惠", "岚菱", "昊茗", "玥宁", "妘容", "芹潞", "从芸", "香琴", "芯茹", "冉抒", "茹轩", "蕊昱", "睿祁", "容歆", "轩玥", "芸绮", "盈雯", "凝遥", "萱焓", "多睿", "芩喧", "欣甜", "书乐", "颜抒", "昕宇", "谨谣", "锦然", "夏烁", "锦语", "玲鹭", "雪芩", "萧茹", "懿涵", "旭芹", "茵云", "羽溪", "忻灿", "珺谣", "馨元", "淑云", "莉馨", "梦蝶", "琳潼", "馨雅", "致语", "初露", "昕潞", "爱琴", "亦芸", "天翼", "昕芳", "依笛", "凤娟", "宇煊", "安娜", "惠彤", "娴易", "誉雯", "芩煊", "予宇", "婷秀", "雨彤", "夏颢", "雨泽", "婧云", "昱茹", "锦文", "荣诗", "笑芝", "语莲", "晓凡", "寄耘", "晗晨", "翠玲", "颜晗", "佳泓", "忻菲", "念暄", "琦兴", "秒欣", "颢舒", "玥暮", "容琼", "宸谣", "雁红", "耘雨", "盈昕", "丽书", "灵秀", "誉然", "湘晗", "雪甜", "文智", "姝伶", "芯洁", "译羽", "晨烨", "佩芩", "嘉羽", "语蔚", "美菏", "恩瑶", "纾芹", "芹蕊", "雁函", "涵妍", "彩菱", "聪晨", "诗煊", "玥函", "雯心", "含儒", "碧蝶", "莉语", "耘怀", "笑晗", "遥琴", "慧云", "函依", "南蓉", "清心", "遥羿", "羽思", "湘峪", "晨湘", "美儒", "兰韵", "念滢", "谣捷", "洳初", "瑾雯", "函文", "琴琀", "耘妤", "昕谣", "正妍", "舒睿", "修哲", "薇莹", "馨愉", "梦颜", "欢柔", "舒瑛", "云霏", "笑诗", "灵钰", "妍瑗", "含碹", "若辰", "宜岚", "璐雯", "毓静", "嘉美", "羽珏", "妍弘", "云忆", "雁鸿", "妍美", "纯娴", "艺含", "寄香", "蕊丹", "莺文", "芸璐", "笛妤", "彤夕", "晖莹", "清婉", "菲悦", "淑卉", "妘灏", "钧遥", "琳露", "芯文", "新睿", "聆雯", "依书", "锦颖", "以芸", "水芸", "雯亿", "润银", "舒萌", "婿睛", "旻琳", "翌茹", "莹翌", "如凡", "舒颐", "芳琳", "逸甜", "函芸", "歆洳", "暄笑", "翠茹", "宇欣", "抒谣", "淑琲", "雅姝", "颢轩", "安慧", "婌怀", "芹昊", "灵瞳", "涵燕", "宇宣", "秋莲", "锦雯", "智欣", "妤芠", "云纾", "烁浠", "珺谣", "琀雅", "韵颔", "水蓉", "意婌", "云含", "憧瑶", "依蝶", "雨译", "问雪", "晴晔", "辰茹", "婉曼", "羽轩", "晨悦", "雯彤", "昕叶", "采喧", "胜怡", "美瑗", "秀逸", "淑旋", "颍菡", "柔芹", "书枫", "婌莺", "鸿耘", "琳焓", "泽佳", "旭甜", "夕谣", "雅飞", "梓忻", "舒萌", "丽琳", "宛茹", "馨淯", "歆年", "蕊婷", "梦露", "莎雅", "函容", "千奕", "萱滢", "姝琦", "予羽", "舒弘", "雯予", "萤含", "诗姝", "欣斐", "茹渲", "露雅", "烨雪", "睿媚", "梦依", "黛萱", "莹骄", "欣辰", "雯歆", "馨琦", "涵韵", "安萱", "依笛", "心蕊", "舒菲", "鹭智", "姝逍", "琳善", "畅莺", "皓云", "焓音", "忆忻", "妙晴", "滢昱", "汝熙", "谣旭", "美羽", "舒晴", "若菡", "娜宇", "舒倩", "妘欷", "旻湘", "霜宇", "澜莹", "含湘", "颍舒", "誉珍", "霏涵", "函娇", "骄扬", "亿歆", "宁耘", "宇默", "亦蕊", "蕊誉", "晨洳", "语文", "佳韵", "欣聪", "柔如", "宁云", "加怡", "新言", "紫函", "心皓", "莉姝", "雨颢", "晟茹", "娴依", "含芹", "西函", "昕宜", "暄文", "羽意", "修含", "欣叶", "海洁", "滢倩", "莹雅", "晓纾", "若瑶", "云儒", "素忻", "湘孺", "书岚", "予云", "新凡", "梦懿", "茹琴", "书琀", "程歆", "焓玲", "羽姻", "亦彤", "之歆", "雨歆", "晗抒", "孺遥", "惜香", "语芩", "诗桐", "芩柔", "琳淼", "琴诺", "谦滢", "婌育", "妘勤", "奕浛", "秋荣", "舒桦", "儒梦", "易晨", "郁琬", "利羽", "翌焓", "雨娅", "宇雯", "溢琼", "羽莲", "誉然", "诗娜", "昊文", "玥姻", "潇睿", "舒文", "涵颖", "雨蓓", "睿晴", "小阳", "茹晗", "宁玲", "琴晔", "如雪", "慈遥", "馨梅", "向璐", "言烨", "芯憧", "昔纯", "梦芩", "誉纯", "耘绮", "昊汝", "晗颜", "可忆", "骊颖", "宸蕾", "语濡", "婌荣", "琴萱", "瑾琳", "淑柔", "菡露", "彦忻", "馨煊", "睿喧", "玥聪", "亦雯", "莹妃", "羽漩", "欣昱", "懿昕", "抒菲", "语灏", "溪雯", "馨毓", "蔓遥", "新婌", "心璐", "耘琪", "婷俪", "品梦", "昔琳", "语霏", "洁文", "晴含", "颢琴", "宸聪", "意耘", "莹弈", "容蕊", "姝羽", "悦亦", "汝元", "媛祺", "初鹭", "箫芩", "艺颖", "语真", "美扬", "思宇", "盈烁", "岚如", "昱雅", "菡喧", "荞蕾", "姝孺", "喧抒", "莹奕", "若翡", "雅纾", "亚娇", "迎蔓", "曼荷", "芹抒", "雅琳", "芯娅", "亦焓", "辰嘉", "浩焓", "琴羽", "昕得", "昔潇", "依薪", "颢函", "雅信", "懿语", "歆淇", "煜琼", "茹莺", "文洳", "瑞文", "茹喧", "颢阳", "淑渲", "茵宸", "蓉岩", "耘为", "颜璐", "抒孺", "莉雯", "睛菲", "彤雅", "书函", "语云", "淑芸", "雪芹", "懿宏", "从耘", "雯箫", "忆融", "予姝", "致萱", "美耘", "涵清", "欢月", "珺媛", "丝雯", "岚瑛", "岚涵", "雯碧", "淑菲", "媛岚", "岚晴", "惠菲", "涵絮", "寒舞", "絮华", "茹裳", "茹雅", "惠雯", "晴语", "涵雁", "翔雅", "云岚", "茜婷", "舒菡", "晴嘉", "絮嫣", "茵茜", "淑嫣", "雯茜", "涵媚", "雅梦", "媛珺", "岚语", "云惠", "岚嫣", "涵雅", "翔梦", "茵华", "云茜", "清岚", "惠媚", "雁菱", "茜语", "晴絮", "雅绿", "云雅", "涵雯", "舒婷", "珺裳", "茵嘉", "茜嘉", "茜雯", "茜瑛", "涵淑", "寒梦", "惠茹", "舒雅", "雯茵", "涵瑛", "晴瑛", "寒茹", "舒语", "清晴", "惜茵", "淑婷", "媛婷", "雯鸣", "舒淑", "涵云", "雯涵", "寒菱", "淑语", "媚雅", "茜寒", "珺淑", "惠珺", "茹菡", "清嫣", "媚嘉", "惠嫣", "晴云", "雯珺", "茹雯", "涵华", "寒惠", "淑茜", "茹珺", "云舒", "珺睿", "惠雅", "珺菡", "惠睿", "晴茜", "岚嫦", "云涵", "晴惠", "涵惠", "惠絮", "涵菡", "雯婷", "寒淑", "晴清", "淑涵", "珺涵", "云华", "舒媛", "岚雅", "清华", "寒菊", "涵茵", "岚菡", "岚菲", "寒云", "茹絮", "寒媛", "岚瑜", "淑淑", "惠语", "寒华", "涵婷", "晴珺", "寒瑜", "云嫦", "茵清", "茵嫣", "惠云", "翔雯", "淑梦", "晴菡", "珺云", "清雅", "雯嘉", "雯舒", "茜菡", "云嫣", "清梦", "惠茜", "茜华", "茜茜", "舒菲", "婷雯", "翔嘉", "晴岚", "晴翠", "雅舒", "茹语", "翔媛", "惠嘉", "云絮", "茹云", "雯翔", "雅淑", "雯嫣", "岚茹", "雯淑", "茜云", "晴睿", "茜梦", "云菡", "岚萍", "茵雅", "涵菲", "茵茹", "晴茵", "岚婷", "涵语", "寒雁", "淑雅", "岚舒", "寒嫣", "涵绮", "茜珺", "淑菡", "茜舒", "媚晴", "珺媚", "岚珺", "惠嫦", "舒华", "晴媛", "涵涵", "茵睿", "媛嫣", "雯菲", "茹惠", "晴雅", "岚淑", "寒媚", "雯惠", "岚雯", "茵语", "云珺", "惠瑛", "惠舒", "茜晴", "惠菡", "云瑜", "珺菲", "寒云", "舒岚", "翔淑", "珺惠", "琦翾", "晴华", "雯菱", "茜嫣", "岚云", "媛雅", "茹嫣", "茜惠", "云梦", "惠华", "晴晴", "茜茵", "茹瑜", "茜瑜", "惠婷", "茹菱", "茜茹", "媛惠", "雯绮", "雯云", "涵瑜", "惠菱", "涵珺", "惠舞", "惠雁", "雯媛", "岚碧", "茹嘉", "絮雅", "雯絮", "寒翔", "淑岚", "舒涵", "惠媛", "茜雅", "翔瑛", "清语", "雯萍", "寻雁", "清嘉", "茜媛", "涵茹", "惠晴", "茵惠", "媛菲", "珺绮", "茹菲", "涵嫣", "翔鸣", "寒睿", "雯瑛", "惠淑", "岚雁", "茜菲", "寒岚", "茜嫦", "茜菊", "涵媛", "淑云", "舒嫣", "清云", "茵萍", "淑华", "媛语", "云晴", "寒鸣", "云清", "岚华", "淑惠", "雯语", "晴茹", "雯晴", "雯睿", "清茜", "芬迎", "恬梨", "芙婕", "宸旋", "翾琳", "桐卿", "宸英", "素娅", "花曼", "芳梨", "芳海", "素海", "宸婕", "芳敏", "宸曼", "花敏", "素若", "素英", "珊婧", "桐彩", "桑婉", "珊娅", "宸悦", "珊英", "倚婕", "芬敏", "芬雪", "珊旋", "芙卿", "玲紫", "芬旋", "珊悦", "娜娅", "夏娅", "桑曼", "素婉", "花婉", "凌旋", "桑婕", "芸海", "芝婧", "蓝莺", "素敏", "芬婉", "桐旋", "花婕", "珊若", "芙悦", "芳婉", "夏卿", "珊雪", "倩婕", "花娅", "纹彩", "芙婉", "芹曼", "芬曼", "桐娅", "素曼", "宸雪", "桐梅", "芝雪", "素雪", "娜曼", "倩婉", "芳英", "芳珠", "宸珠", "芳曼", "芬梨", "芳婧", "芳卿", "翾琪", "宸卿", "桐悦", "倩雪", "素迎", "花卿", "素婧", "娜卿", "芳甜", "桐敏", "芙娅", "珊卿", "珊曼", "夏婕", "芷雪", "纹珠", "素旋", "珍娅", "笑甜", "夏婉", "娜婕", "素婕", "娜若", "芳娅", "凌雪", "宸婉", "芝英", "夏曼", "绿夏", "筠佩", "舞桑", "菱宸", "菊娜", "语恬", "淑音", "歆沐", "虞明", "绮凌", "涵柳", "晴柔", "虞青", "诗英", "欢蕾", "诗沁", "尔珍", "淑柳", "清盈", "翠芙"};
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            int num = rand.nextInt(1976) + 1;
            HttpResult.DeviceName = name[num] + "的 iPad";
        }
        return HttpResult;
    }

}
