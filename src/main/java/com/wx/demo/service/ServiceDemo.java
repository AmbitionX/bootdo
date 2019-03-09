package com.wx.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wx.demo.DB.DBUtil;
import com.wx.demo.bean.Message;
import com.wx.demo.tools.StringUtil;
import com.wx.demo.httpHandler.HttpResult;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceDemo extends BaseService {
    private static Logger logger = Logger.getLogger(ServiceDemo.class);

    private static ScheduledExecutorService payCheckService = Executors.newScheduledThreadPool(4);

    private Future scheduleFuture;
    private String payingIndentId;
    private long payStartTime;
    private String payFoAccount;
    private String payDlAccount;
    private String payFoMoney;
    private String payUserType;
    private String payApilocation;

    public ServiceDemo(String randomid) {
        super(randomid);
        scheduleFuture = payCheckService.scheduleAtFixedRate(() -> {
            if (StringUtil.isEmpty(payingIndentId)) {
                return;
            } else if (payStartTime + 300000 < System.currentTimeMillis()) {
                DBUtil.executeUpdate(String.format("UPDATE EPS_USER_BIND SET STATUS_ = 6 WHERE RANDOM_ID = '%s'", getrandomid()));
                clearPayStatus();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void begin() {
        if (isNew) {
            DBUtil.executeUpdate(String.format
                    ("insert into EPS_USER_BIND (ID_,LOGIN_NAME,WX_ID,CREATE_DATE,RANDOM_ID,STATUS_,Nickname) VALUES('%s','%s','%s',NOW(),'%s',6,'%s')",
                            randomid.replace("-", ""), account, wxId, randomid, qrRes.Nickname));
        } else {
            DBUtil.executeUpdate(String.format(
                    "UPDATE EPS_USER_BIND SET STATUS_ = %d WHERE RANDOM_ID = '%s'", 6, randomid));
        }
        super.begin();
    }
//在这里直接调用 嗯
    @Override
    public void waitlogin() {
        super.waitlogin();
        if (isNew && qrRes.hasSaoMa) {
            String sql2 = "SELECT * FROM EPS_USER_BIND WHERE MyServerId='%d' AND RANDOM_ID = '%d'";
            JSONArray receiveRecord = DBUtil.executeQuery(String.format(sql2, curStatus.serverid, randomid));
            curStatus.setname(receiveRecord);
        } else {
            curStatus.nickname = qrRes.Nickname;
            curStatus.username = qrRes.Username;
            curStatus.bigHeadImgUrl = qrRes.HeadImgUrl;
            curStatus.ImgBuf = "";
        }
        curStatus.code = 2;
        curStatus.msg = "等待登录";
    }

    public void startPayMent(String indent, String foaccount, String dlaccount, String money, String usertype, String apilocation) {
        if (!StringUtil.isEmpty(payingIndentId)) {
            return;
        }
        payingIndentId = indent;
        payStartTime = System.currentTimeMillis();
        this.payDlAccount = dlaccount;
        this.payFoAccount = foaccount;
        this.payFoMoney = money;
        this.payUserType = usertype;
        this.payApilocation = apilocation;

        DBUtil.executeUpdate(String.format("UPDATE EPS_USER_BIND SET STATUS_ = %d WHERE RANDOM_ID = '%s'", 20010, getrandomid()));

    }

    private void clearPayStatus() {//初始化支付状态
        payingIndentId = null;
        payStartTime = 0;
        payDlAccount = null;
        payFoAccount = null;
        payFoMoney = null;
        payUserType = null;
        payApilocation = null;
    }


    @Override
    public HttpResult handleHttpRequest(String uri, HashMap<String, String> param) {
        if (uri.equals("getpayment")) {
            if (!StringUtil.isEmpty(payingIndentId)) {
                return new HttpResult(ServiceStatus.PAYSTATUSERROR);
            }
            String foaccount = param.get("foaccount");
            String dlaccount = param.get("dlaccount");
            String indent = param.get("indent");
            String money = param.get("money");
            String usertype = param.get("usertype");
            String apilocation = param.get("apilocation");
            startPayMent(indent, foaccount, dlaccount, money, usertype, apilocation);
            return new HttpResult(20010, indent);
        }
        return super.handleHttpRequest(uri, param);//返回到父类
    }

    @Override
    public void parseMsg(Message msg) {
        msg = this.preprocessing(msg);
        if (msg.MsgType == 2490) {
            String Invalidtime = "";
            String Transferid = "";
            logger.info("收到：" + msg.FromUserName + " 转账信息");
            String Content = msg.Content;
            Pattern p = Pattern.compile("<invalidtime.*?>(.*?)</invalidtime>");
            Matcher m = p.matcher(Content);
            while (m.find()) {
                Invalidtime = m.group(1).replace("<![CDATA[", "").replace("]]>", "");
            }

            Pattern p1 = Pattern.compile("<transferid.*?>(.*?)</transferid>");
            Matcher m1 = p1.matcher(Content);
            while (m1.find()) {
                Transferid = m1.group(1).replace("<![CDATA[", "").replace("]]>", "");
            }
            String FromUsername = msg.FromUserName;
            String FromData = longUtil.getZhuanZhang(Invalidtime, Transferid, FromUsername);
            //{"fee":1,"fee_type":"1","payer":"085e9858e4fda765dd1f3b42b","receiver":"085e9858e190857d74c4cd2f8","retcode":"0","retmsg":"ok"}
            //{"fee":2,"fee_type":"1","payer":"085e9858e4fda765dd1f3b42b","receiver":"085e9858e190857d74c4cd2f8","retcode":"0","retmsg":"ok"}

            JSONObject jsonObj = JSON.parseObject(FromData);
            String retcode = jsonObj.getString("retcode");
            String retmsg = jsonObj.getString("retmsg");
            if (retcode.equals("0") && retmsg.equals("ok")) {
                String paymentAmount = jsonObj.getString("fee");
                //发起支付金额(从内容中获取)
                String paymentWxid = msg.FromUserName;
                ;//支付方wxid
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String patTime = sdf.format(new Date());
                //修改状态 与最后支付时间
                DBUtil.executeUpdate(String.format("UPDATE EPS_USER_BIND SET STATUS_ = %d, LastPaymentTime = %d WHERE RANDOM_ID = '%s'", 6, System.currentTimeMillis(), getrandomid()));
                //更新订单表
                DBUtil.executeUpdate(String.format("UPDATE payment_user SET ACCOUNT ='%s', FO_MONEY='%s', PAYMENT_AMOUNT='%s', PAYMENT_TIME='%s', DL_ACCOUNT ='%s',PAYMENT_WXID='%s' WHERE INDENT= '%s'",
                        getAccount(), payFoMoney, paymentAmount, patTime, payDlAccount, paymentWxid, payingIndentId));
                clearPayStatus();
            }
        }
    }

    @Override
    protected void exit() {
        super.exit();
        if (scheduleFuture != null) {
            scheduleFuture.cancel(true);
        }
    }

    public Message preprocessing(Message msgss) {
        if (msgss.FromUserName.contains("@")) {
//            msgss.Type = 1;
            msgss.ChatRoomUserName = msgss.FromUserName;
            if (msgss.MsgType == 10000) {
                if (msgss.Content.contains("加入了群聊")) {
                    msgss.MsgType = 10101;
                    String[] Contents = msgss.Content.split("加入了");
                    String[] Contentsss = Contents[1].split("邀请");
                    msgss.Inviter = Contentsss[1];
                    msgss.Inviteruser = Contentsss[2];
                    msgss.MsgType = 10101;
                } else if (msgss.Content.contains("修改群名为")) {
                    msgss.MsgType = 10102;
                    String[] Contents = msgss.Content.split("修改群名为");
                    msgss.Inviter = Contents[1];
                    msgss.Inviteruser = Contents[2];
                } else if (msgss.Content.contains("修改群名为")) {
                    msgss.MsgType = 10103;
                } else if (msgss.Content.contains("成为新群主")) {
                    msgss.MsgType = 10104;
                } else if (msgss.Content.contains("群主已启用")) {
                    msgss.MsgType = 10111;
                } else if (msgss.Content.contains("主已恢复默认进群方式")) {
                    msgss.MsgType = 10112;
                }
            } else {
                String[] Contents = msgss.Content.split(":\n");
                String[] PushContents = msgss.PushContent.split(":\n");
                msgss.FromUserName = Contents[1];
                msgss.FromNickName = PushContents[1];
                msgss.Content = Contents[2];
                msgss.PushContent = PushContents[2];
            }
        }
        if (msgss.MsgType == 49) {
            if (msgss.Content.contains("<type>3</type>")) {
                msgss.MsgType = 4903;//音乐消息
            } else if (msgss.Content.contains("<type>5</type>")) {
                msgss.MsgType = 4905;//普通APP消息
            } else if (msgss.Content.contains("<type>6</type>")) {
                msgss.MsgType = 4906;//文件分享
            } else if (msgss.Content.contains("<type>10</type>")) {
                msgss.MsgType = 4910;//微信商品
            } else if (msgss.Content.contains("<type>16</type>")) {
                msgss.MsgType = 4916;//卡卷消息
            } else if (msgss.Content.contains("<type>17</type>")) {
                msgss.MsgType = 4917;//实时位置共享
            } else if (msgss.Content.contains("<type>19</type>")) {
                msgss.MsgType = 4919;//聊天记录分享
            } else if (msgss.Content.contains("<type>33</type>")) {
                msgss.MsgType = 4936;//小程序分享
            } else if (msgss.Content.contains("<type>36</type>")) {
                msgss.MsgType = 4936;//小程序分享
            } else if (msgss.Content.contains("<type>1</type>")) {
                msgss.MsgType = 4901;//小尾巴
            } else if (msgss.Content.contains("<type>[CDATA[1001]]</type>")) {
                msgss.MsgType = 1491;//群收款消息
            } else if (msgss.Content.contains("<type><![CDATA[2001]]></type>")) {
                msgss.MsgType = 2491;//红包消息
            } else if (msgss.Content.contains("<type>2000</type>")) {
                msgss.MsgType = 2490;//收到转账消息
            }
        }
        return msgss;
    }



}
