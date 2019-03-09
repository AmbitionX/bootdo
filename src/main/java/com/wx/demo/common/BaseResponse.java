package com.wx.demo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author: dingzhiwei
 * @date: 17/11/29
 * @description:
 */
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;
    private static final Logger _log = LoggerFactory.getLogger(BaseResponse.class);

    public int code;       // 返回码
    public String msg;     // 返回消息

    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
        _log.info(this.toString());
    }

    public BaseResponse(RetEnum retEnum) {
        this.code = retEnum.getCode();
        this.msg = retEnum.getMessage();
    }

    public static BaseResponse build(RetEnum retEnum) {
        BaseResponse baseResponse = new BaseResponse(retEnum.getCode(), retEnum.getMessage());
        return baseResponse;
    }

    public static BaseResponse build(int code, String msg) {
        BaseResponse baseResponse = new BaseResponse(code, msg);
        return baseResponse;
    }

    public static BaseResponse buildSuccess() {
        BaseResponse baseResponse = new BaseResponse(RetEnum.RET_COMM_SUCCESS);
        return baseResponse;
    }

    public String getMsg() {
        return msg;
    }

    public BaseResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
