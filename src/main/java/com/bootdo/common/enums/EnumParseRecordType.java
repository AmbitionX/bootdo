package com.bootdo.common.enums;

/**
 * 返回码枚举类
 */
public enum EnumParseRecordType {

    // 公共返回码1开头4位
    NUM_TYPE_ONE(1, "解析中"),
    NUM_TYPE_TWO(2, "解析完成"),
    NUM_TYPE_THREE(3, "解析失败");

    private int code;
    private String message;

    EnumParseRecordType(int code, String message) {
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
