package com.wx.demo.common;

/**
 * 返回码枚举类
 */
public enum RetEnum {

    // 公共返回码1开头4位
    RET_COMM_SUCCESS(0, "成功"),

    RET_COMM_1000(1000, "调用失败"),
    RET_COMM_1001(1001, "参数错误"),
    RET_COMM_1002(1002, "用户对应的服务线程不存在"),
    RET_COMM_1003(1003, "生成收款二维码失败"),
    RET_COMM_9999(9999, "未知错误"),
    RET_COMM_2001(2001, "a8k拉取为空,微信号系没有阅读功能"),
    RET_COMM_2002(2002, "未能实际进行有效阅读"),

    RET_COMM_3001(3001,"未能关注成功");

    private int code;
    private String message;

    RetEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
