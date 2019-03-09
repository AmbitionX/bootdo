package com.wx.demo.bean;

import com.wx.demo.tools.WxLongUtil;

import java.io.*;

public class RedisBean implements Serializable {
    private static final long serialVersionUID = -3797710630759293569L;
    public WxLongUtil.UtilMsg.UtilUser loginedUser;
    public String shortServerHost;
    public String longServerHost;
    public String randomid;
    public String uuid;
    public String serverid;
    public String softwareId;
    public String extraData;
    public String account;

    public static byte[] serialise(Object object) {
        ObjectOutputStream obi = null;
        ByteArrayOutputStream bai = null;
        try {
            bai = new ByteArrayOutputStream();
            obi = new ObjectOutputStream(bai);
            obi.writeObject(object);
            byte[] byt = bai.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RedisBean unserizlize(byte[] byt) {
        ObjectInputStream oii = null;
        ByteArrayInputStream bis = null;
        bis = new ByteArrayInputStream(byt);
        try {
            oii = new ObjectInputStream(bis);
            Object obj = oii.readObject();
            return (RedisBean) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
