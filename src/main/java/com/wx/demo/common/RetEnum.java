package com.wx.demo.common;

/**
 * 返回码枚举类
 */
public enum RetEnum {

    // 公共返回码1开头4位
    RET_COMM_SUCCESS(0, "成功"),
    RET_COMM_1001(1001, "参数错误"),
    RET_COMM_1002(1002, "用户对应的服务线程不存在"),
    RET_COMM_1003(1003, "生成收款二维码失败"),
    RET_COMM_9999(9999, "未知错误");

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
