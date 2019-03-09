package com.wx.demo.bean;

import com.wx.demo.frameWork.proto.WechatMsg;

public interface CallBack {
    void onData(byte[] data);
}