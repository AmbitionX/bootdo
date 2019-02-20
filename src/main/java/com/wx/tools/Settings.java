package com.wx.tools;

import org.apache.log4j.Logger;

public class Settings {
    private static Logger logger = Logger.getLogger(Settings.class);

    public static class Set {
        public String version = "7.0.2";
        public String longServer = "long.weixin.qq.com";
        public String shortServer = "short.weixin.qq.com";
        public String machineCode = "v3_8799ed22a680b70e6dbe0596b1a82bdc";
        public String appId = "v1_804188876_CodeVip";
        public String appKey = "v2_1b76be021d21114b6d59bd7edd7c55dc";
      //  public String appKey = "v3_8799ed22a680b70e6dbe0596b1a82bdc";
        public String localIp = "120.36.248.152";
        public String mysqlUrl = "jdbc:mysql://192.168.1.84/cyy_data?zeroDateTimeBehavior=convertToNull";
        public String mysqlUserName = "root";
        public String mysqlPwd = "root";
        public String server_ip = "182.87.240.24";
        public String grpc_host = "grpc.wxipad.com:12580";
        public String bindGame;
        public String apiappId;
        public String apiappKey;
        public int rateTime;
        public String SOFTWARE_DEMO = "666";
        public int server_port = 4567;
        public String[] rpcServerList = {grpc_host
        };
    }

    private static Set set = new Set();

    public static Set getSet() {
        return set;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        Settings.logger = logger;
    }

    public static void setSet(Set set) {
        Settings.set = set;
    }
}
