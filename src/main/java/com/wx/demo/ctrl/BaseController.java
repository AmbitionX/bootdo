package com.wx.demo.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wx.demo.common.RetEnum;
import com.wx.demo.common.WxException;
import com.wx.demo.util.MyLog;
import com.wx.demo.wechatapi.distributed.DistributedApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/6
 * @description:
 */
@Controller(value = "wxBaseController")
public class BaseController {
    private final MyLog _log = MyLog.getLog(DistributedApi.class);
    private static final int DEFAULT_PAGE_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    protected int getPageIndex(JSONObject object) {
        if (object == null) {
            return DEFAULT_PAGE_INDEX;
        }
        Integer pageIndex = object.getInteger("page");
        if (pageIndex == null) {
            return DEFAULT_PAGE_INDEX;
        }
        return pageIndex;
    }

    protected int getPageSize(JSONObject object) {
        if (object == null) {
            return DEFAULT_PAGE_SIZE;
        }
        Integer pageSize = object.getInteger("limit");
        if (pageSize == null) {
            return DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    protected int getPageIndex(Integer page) {
        if (page == null) {
            return DEFAULT_PAGE_INDEX;
        }
        return page;
    }

    protected int getPageSize(Integer limit) {
        if (limit == null) {
            return DEFAULT_PAGE_SIZE;
        }
        return limit;
    }

    protected JSONObject getJsonParam(HttpServletRequest request) {
        String params = request.getParameter("params");
        if (StringUtils.isNotBlank(params)) {
            return JSON.parseObject(params);
        }
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        JSONObject returnObject = new JSONObject();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name;
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnObject.put(name, value);
        }
        return returnObject;
    }

    protected Boolean getBoolean(JSONObject param, String key) {
        if (param == null) {
            return null;
        }
        return param.getBooleanValue(key);
    }

    protected String getString(JSONObject param, String key) {
        if (param == null) {
            return null;
        }
        return param.getString(key);
    }

    protected String getStringRequired(JSONObject param, String key) {
        if (param == null) {
            throw new WxException(RetEnum.RET_COMM_1001, getErrMsg(key));
        }
        String value = param.getString(key);
        if (StringUtils.isBlank(value)) {
            throw new WxException(RetEnum.RET_COMM_1001, getErrMsg(key));
        }
        return value;
    }

    protected String getStringDefault(JSONObject param, String key, String defaultValue) {
        String value = getString(param, key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    protected Byte getByte(JSONObject param, String key) {
        if (param == null) {
            return null;
        }
        return param.getByte(key);
    }

    protected Byte getByteRequired(JSONObject param, String key) {
        if (param == null) {
            throw new WxException(RetEnum.RET_COMM_1001, getErrMsg(key));
        }
        Byte value = param.getByte(key);
        if (value == null) {
            throw new WxException(RetEnum.RET_COMM_1001, getErrMsg(key));
        }
        return value;
    }

    protected int getByteDefault(JSONObject param, String key, byte defaultValue) {
        Byte value = getByte(param, key);
        if (value == null) {
            return defaultValue;
        }
        return value.byteValue();
    }

    protected Integer getInteger(JSONObject param, String key) {
        if (param == null) {
            return null;
        }
        return param.getInteger(key);
    }

    protected Integer getIntegerRequired(JSONObject param, String key) {
        if (param == null) {
            throw new WxException(RetEnum.RET_COMM_1001, getErrMsg(key));
        }
        Integer value = param.getInteger(key);
        if (value == null) {
            throw new WxException(RetEnum.RET_COMM_1001, getErrMsg(key));
        }
        return value;
    }

    protected int getIntegerDefault(JSONObject param, String key, int defaultValue) {
        Integer value = getInteger(param, key);
        if (value == null) {
            return defaultValue;
        }
        return value.intValue();
    }

    protected Long getLong(JSONObject param, String key) {
        if (param == null) {
            return null;
        }
        return param.getLong(key);
    }

    protected Long getLongRequired(JSONObject param, String key) {
        if (param == null) {
            throw new WxException(RetEnum.RET_COMM_1001, getErrMsg(key));
        }
        Long value = param.getLong(key);
        if (value == null) {
            throw new WxException(RetEnum.RET_COMM_1001, getErrMsg(key));
        }
        return value;
    }

    protected long getLongDefault(JSONObject param, String key, long defaultValue) {
        Long value = getLong(param, key);
        if (value == null) {
            return defaultValue;
        }
        return value.longValue();
    }

    protected JSONObject getJSONObject(JSONObject param, String key) {
        if (param == null) {
            return null;
        }
        return param.getJSONObject(key);
    }

    protected <T> T getObject(JSONObject param, String key, Class<T> clazz) {
        JSONObject object = getJSONObject(param, key);
        if (object == null) {
            return null;
        }
        return JSON.toJavaObject(object, clazz);
    }

    protected <T> T getObject(JSONObject param, Class<T> clazz) {
        if (param == null) {
            return null;
        }
        return JSON.toJavaObject(param, clazz);
    }

    private String getErrMsg(String key) {
        return "参数" + key + "必填";
    }
    public HashMap<String, String> toParam(JSONObject jsonObject) {
        HashMap<String, String> param = new HashMap<>();
        for (String k : jsonObject.keySet()) {
            param.put(k, jsonObject.getString(k));
        }
        return param;
    }

}
