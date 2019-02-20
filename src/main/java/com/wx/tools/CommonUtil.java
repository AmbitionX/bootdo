package com.wx.tools;
import java.util.UUID;

public class CommonUtil {
    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
    public static String getPublicIpAddress() throws Exception {
        return Settings.getSet().localIp;
    }
    public static String makePK(String area, String uid) {
        return ConfigService.getMd5((area + uid).getBytes()).substring(8, 24);
    }
    public static String getMac(String usercode) {
        String res = "";
        if (usercode.length() < 12) {
            usercode = ConfigService.getMd5(usercode.getBytes());
        }
        for (int x = 0; x < 6; x++) {
            res += usercode.substring(x * 2, x * 2 + 2);
            res += ":";
        }
        res = res.substring(0, res.length() - 1);
        return res;
    }
    public static void main(String args[]) {
        System.out.print(UUID.randomUUID().toString());
    }
}
