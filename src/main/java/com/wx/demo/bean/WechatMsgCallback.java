package com.wx.demo.bean;

import com.wx.demo.frameWork.proto.WechatMsg;

public interface WechatMsgCallback {
    void onWechatMsg(WechatMsg msg);
}
