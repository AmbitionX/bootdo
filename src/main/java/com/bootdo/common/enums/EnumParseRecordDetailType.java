package com.bootdo.common.enums;

/**
 * 返回码枚举类
 */
public enum EnumParseRecordDetailType {

    // 公共返回码1开头4位
    NUM_TYPE_ONE(1, "执行中"),
    NUM_TYPE_TWO(2, "执行成功"),
    NUM_TYPE_THREE(3, "执行失败"),
    NUM_TYPE_FOUR(4, "平台冲突"),
    NUM_TYPE_FIVE(5, "已存在此号");

    private int code;
    private String message;

    EnumParseRecordDetailType(int code, String message) {
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
