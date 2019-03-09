package com.wx.demo.frameWork.protocol;

import com.wx.demo.frameWork.proto.WechatMsg;


public interface WechatMsgCallback {

    void onWechatMsg(WechatMsg wechatMsg);
}
