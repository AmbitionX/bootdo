package com.bootdo.common.enums;

/**
 * 返回码枚举类
 */
public enum EnumWxCmdType {

    NUM_TYPE_2222(2222, "微信62数据登录"),
    NUM_TYPE_TWO(2, "执行成功"),
    NUM_TYPE_THREE(3, "执行失败"),
    NUM_TYPE_FOUR(4, "平台已经存在");

    private int code;
    private String message;

    EnumWxCmdType(int code, String message) {
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
