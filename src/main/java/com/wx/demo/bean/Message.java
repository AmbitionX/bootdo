package com.wx.demo.bean;

import com.wx.demo.tools.StringUtil;

public class Message {
    public long MsgId = -10086;
    public String FromUserName = "";//消息发送者
    public String FromNickName = "";
    public String ToUserName;//用户名称
    public int MsgType;//消息id
    public String Content = "";//内容
    public int Status;//状态
    public int ImgStatus;//1推送图片,2普通图片
    public Object ImgBuf;//图片
    public int CreateTime;//消息发送时间戳
    public String MsgSource;//消息来源
    public String PushContent;//推送
    public long NewMsgId;//新消息ID
    public String NickName;//昵称
    public String ChatRoomOwner;//群主
    public String ChatRoomNickName;//群名
    public String ChatRoomUserName;//群id
    public String ExtInfo;//群成员的wxid文本
    public String UserName;//ID
    public boolean GrabHongBao;//是否抢这个群的红包，
    public boolean Statistics;//是否统计这个群的流水
    public int Nature;//群的性质，0为游戏群，1为管理群
    public String Inviter;//邀请人
    public String Inviteruser;//被邀请人

    public boolean isHongbao() {
        return StringUtil.isEmpty(Content) ? false : Content.contains("CDATA[2001]") && Content.contains("红包");//是否为红包
    }

    public boolean Transferaccounts() {
        return StringUtil.isEmpty(Content) ? false : Content.contains("CDATA[2000]") && Content.contains("转账"); //转账
    }

    public boolean Invitechat() {
        return StringUtil.isEmpty(Content) ? false : Content.contains("邀请你加入群聊") && Content.contains("@");
    }

    //邀请你加入群聊
    public boolean Paymentreceivedgroupmembers() {
        return StringUtil.isEmpty(Content) ? false : Content.contains("PayMsgType>18</PayMsgType");
    }

    //收到群成员的付款
    public boolean Facecollection() {
        return StringUtil.isEmpty(Content) ? false : Content.contains("PayMsgType>9</PayMsgType");
    }
}
