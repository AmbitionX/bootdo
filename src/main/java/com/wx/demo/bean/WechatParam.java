package com.wx.demo.bean;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.wx.demo.frameWork.proto.BaseMsg;
import com.wx.demo.frameWork.proto.User;
import com.wx.demo.frameWork.proto.WechatMsg;
import com.wx.demo.tools.WechatUtil;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
public class WechatParam {
    public String randomid;
    public User.Builder userBuilder;
    public BaseMsg.Builder baseMsgBuilder;
    public WechatMsg.Builder wechatMsgBuilder;
    private User.Builder NewUserBuilder(String randomid){
        return NewUserBuilder(randomid,null);
    }
    private User.Builder NewUserBuilder(String randomid,String wxDat){
        String wxtype;
        String wxdat = wxDat;
        if(wxdat ==null || wxdat.equals("")){
             wxdat = WechatUtil.getWxDat(randomid,0);
        }
            wxtype = WechatUtil.getDeviceType(wxdat,2);
        User.Builder builder =  User.newBuilder()
                .setSessionKey(ByteString.copyFrom(WechatUtil.sessionKey))
                .setDeviceName(WechatUtil.getname())
                .setDeviceId(wxdat)
                .setDeviceType(wxtype);
        return builder;
    }
    private BaseMsg.Builder NewBaseMsgBuilder(String randomid){
        return NewBaseMsgBuilder(NewUserBuilder(randomid));
    }
    private BaseMsg.Builder NewBaseMsgBuilder(String randomid,String wxdat){
        return NewBaseMsgBuilder(NewUserBuilder(randomid,wxdat));
    }
    private BaseMsg.Builder NewBaseMsgBuilder(String randomid,String username,String PassWord,String wxdat){
        User.Builder userbuilder = NewUserBuilder(randomid,wxdat);
        HashMap<String, Object> loginData = new HashMap<String, Object>();
        loginData.put("Username", username);
        loginData.put("PassWord", PassWord);
        loginData.put("UUid", randomid);
        loginData.put("DeviceType", userbuilder.getDeviceType());
        loginData.put("DeviceName", userbuilder.getDeviceName());
        loginData.put("ProtocolVer", WechatUtil.protocolVer);
        loginData.put("language", WechatUtil.language);
        loginData.put("realCountry", WechatUtil.realCountry);
        byte[] Payloadsbyte = new Gson().toJson(loginData).getBytes(StandardCharsets.UTF_8);
        return NewBaseMsgBuilder(userbuilder,Payloadsbyte);
    }
    private BaseMsg.Builder NewBaseMsgBuilder(User.Builder UserBuilder){
        HashMap<String, Object> loginData = new HashMap<String, Object>();
        loginData.put("ProtocolVer", WechatUtil.protocolVer);
        byte[] Payloadsbyte = new Gson().toJson(loginData).getBytes(StandardCharsets.UTF_8);
        return NewBaseMsgBuilder(UserBuilder,Payloadsbyte);
    }
    private BaseMsg.Builder NewBaseMsgBuilder(User.Builder UserBuilder,byte[] Payloadsbyt){
        BaseMsg.Builder builder = BaseMsg.newBuilder()
                .setLongHost(WechatUtil.longServerHost)
                .setShortHost(WechatUtil.shortServerHost)
                .setPayloads(ByteString.copyFrom(Payloadsbyt))
                .setUser(UserBuilder);
        return builder;
    }
    private WechatMsg.Builder NewWechatMsgBuilder(String randomid) {
        return NewWechatMsgBuilder(randomid,null);
    }
    private WechatMsg.Builder NewWechatMsgBuilder(String randomid,String wxDat) {
        return NewWechatMsgBuilder(randomid,null,null,wxDat);
    }
    private WechatMsg.Builder NewWechatMsgBuilder(String randomid,String username,String PassWord,String wxDat) {
        String wxdat = wxDat;
        BaseMsg.Builder  BaseMsgbuilder;
        if (wxdat != null && !wxdat.equals("")) {
            if (username != null && !username.equals("") && PassWord != null && !PassWord.equals("")) {
                BaseMsgbuilder = NewBaseMsgBuilder(randomid, username, PassWord, wxdat);
            } else {
                BaseMsgbuilder = NewBaseMsgBuilder(randomid, wxdat);
            }
        } else {
            BaseMsgbuilder = NewBaseMsgBuilder(randomid);
        }
        long time = System.currentTimeMillis() / 1000;
        return WechatMsg.newBuilder()
                .setTimeStamp((int)time)
                .setIP(WechatUtil.getRealIp())
                .setToken(WechatUtil.appTocken)
                .setVersion(WechatUtil.version)
                .setBaseMsg(BaseMsgbuilder);
    }
    public String getRandomid() {
        return randomid;
    }
    public void setRandomid(String randomid) {
        this.randomid = randomid;
    }
    public User.Builder getUserBuilder() {
        return getUserBuilder(null,null);
    }
    public User.Builder getUserBuilder(String randomid) {
        return getUserBuilder(randomid,null);
    }
    public User.Builder getUserBuilder(String randomid,String wxDat) {
        if(userBuilder !=null){
            return userBuilder;
        } else {
            if (randomid != null && !randomid.equals("") ) {
                if ( wxDat != null && !wxDat.equals("")  ) {
                    userBuilder = NewUserBuilder(randomid,wxDat);
                }else {
                    userBuilder = NewUserBuilder(randomid);
                }
            }else {
                userBuilder = User.newBuilder();
            }
            return userBuilder;
        }
    }
    public void setUserBuilder(User.Builder userBuilder) {
        this.userBuilder = userBuilder;
    }
    public BaseMsg.Builder getBaseMsgBuilder(String randomid,String username,String PassWord,String wxdat) {
        if(baseMsgBuilder != null){
            return baseMsgBuilder;
        } else {
            if (randomid!=null && !randomid.equals("")){
                if (wxdat!=null && !wxdat.equals("")){
                    if (username!=null && !username.equals("")&&PassWord!=null && !PassWord.equals("")){
                        baseMsgBuilder = NewBaseMsgBuilder(randomid,username,PassWord,wxdat);
                    }else {
                        baseMsgBuilder = NewBaseMsgBuilder(randomid,wxdat);
                    }
                }else {
                    baseMsgBuilder = NewBaseMsgBuilder(randomid);
                }
            }else {
                baseMsgBuilder = BaseMsg.newBuilder();
            }
        }
        return baseMsgBuilder;
    }
    public BaseMsg.Builder getBaseMsgBuilder(String randomid,String wxdat) {
        return getBaseMsgBuilder(randomid,null,null,wxdat);
    }
    public BaseMsg.Builder getBaseMsgBuilder(String randomid) {
        return getBaseMsgBuilder(randomid,null);
    }
    public BaseMsg.Builder getBaseMsgBuilder() {
        return getBaseMsgBuilder(null);
    }
    public void setBaseMsgBuilder(BaseMsg.Builder baseMsgBuilder) {
        this.baseMsgBuilder = baseMsgBuilder;
    }
    public WechatMsg.Builder getWechatMsgBuilder(String randomid,String username,String PassWord,String wxdat) {
        if(wechatMsgBuilder != null){
            return wechatMsgBuilder;
        } else {
            if (randomid!=null && !randomid.equals("")){
                if (wxdat!=null && !wxdat.equals("")){
                    if (username!=null && !username.equals("")&&PassWord!=null && !PassWord.equals("")){
                        wechatMsgBuilder = NewWechatMsgBuilder(randomid,username,PassWord,wxdat);
                    }else {
                        wechatMsgBuilder = NewWechatMsgBuilder(randomid,wxdat);
                    }
                }else {
                    wechatMsgBuilder = NewWechatMsgBuilder(randomid);
                }
            }else {
                wechatMsgBuilder = WechatMsg.newBuilder();
            }
        }
        return wechatMsgBuilder;
    }
    public WechatMsg.Builder getWechatMsgBuilder(String randomid,String wxdat){
        return getWechatMsgBuilder(randomid,null,null,wxdat);
    }
    public WechatMsg.Builder getWechatMsgBuilder(String randomid){
        return getWechatMsgBuilder(randomid,null);
    }
    public WechatMsg.Builder getWechatMsgBuilder(){
        return getWechatMsgBuilder(null);
    }
    public void setWechatMsgBuilder(WechatMsg.Builder wechatMsgBuilder) {
        this.wechatMsgBuilder = wechatMsgBuilder;
    }
}
