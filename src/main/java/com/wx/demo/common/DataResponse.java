package com.wx.demo.common;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 17/11/29
 * @description:
 */
public class DataResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483574L;
    private static final Logger _log = LoggerFactory.getLogger(DataResponse.class);

    public int code;       // 返回码
    public String msg;     // 返回消息
    public Object data;    // 返回数据

    public DataResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        // _log.info(this.toString());
    }

    public DataResponse(RetEnum retEnum, Object data) {
        this.code = retEnum.getCode();
        this.msg = retEnum.getMessage();
        this.data = data;
        // _log.info(this.toString());
    }

    public static DataResponse build(RetEnum retEnum) {
        DataResponse dataResponse = new DataResponse(retEnum.getCode(), retEnum.getMessage(), null);
        return dataResponse;
    }

    public static DataResponse build(RetEnum retEnum, Object data) {
        DataResponse dataResponse = new DataResponse(retEnum.getCode(), retEnum.getMessage(), data);
        return dataResponse;
    }

    public static DataResponse buildSuccess(Object data) {
        DataResponse dataResponse = new DataResponse(RetEnum.RET_COMM_SUCCESS, data);
        return dataResponse;
    }

    public static DataResponse buildSuccess(Object data, JSONObject param) {
        if (param != null && param.getBooleanValue("returnArray")) {
            List<Object> objectList = new LinkedList<Object>();
            objectList.add(data);
            return new DataResponse(RetEnum.RET_COMM_SUCCESS, objectList);
        } else {
            return new DataResponse(RetEnum.RET_COMM_SUCCESS, data);
        }
    }

    public String getMsg() {
        return msg;
    }

    public DataResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public DataResponse setData(Object data) {
        this.data = data;
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
        return "DataResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
