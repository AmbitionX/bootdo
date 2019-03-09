package com.wx.demo.service;

public enum ServiceStatus {
    STATUS_NULL(0, "获取二维码"),
    HAS_GET_QRCODE(1, "等待扫码"),
    LOGINING(2, "正在登陆中"),
    LOGINED(3, "已登录"),
    OVERTIME(4, "超时未扫码"),
    LOGINFAILED(5, "登录失败, 请重新获登录二维码"),
    PAYSTATUSERROR(-1, "正在支付其它订单，锁定中");
    public int code;
    public String msg;
    ServiceStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
