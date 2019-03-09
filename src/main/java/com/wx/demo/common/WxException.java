package com.wx.demo.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: dingzhiwei
 * @date: 2019-01-18
 * @description:
 */
public class WxException extends RuntimeException {

    private RetEnum retEnum;

    private String extraMsg;

    public WxException(RetEnum retEnum) {
        this.retEnum = retEnum;
    }

    public WxException(RetEnum retEnum, String extraMsg) {
        this.retEnum = retEnum;
        this.extraMsg = extraMsg;
    }

    public static WxException build(RetEnum retEnum) {
        WxException serviceException = new WxException(retEnum);
        return serviceException;
    }

    public RetEnum getRetEnum() {
        return retEnum;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public int getErrCode() {
        return retEnum.getCode();
    }

    public String getErrMsg() {
        String errMsg = retEnum.getMessage();
        if (StringUtils.isNotBlank(extraMsg)) {
            errMsg += "[" + extraMsg + "]";
        }
        return errMsg;
    }

}
