package com.wx.demo.tools;

import org.apache.log4j.Logger;

public class Settings {
    private static Logger logger = Logger.getLogger(Settings.class);

    public static class Set {
        public String version = "7.0.1";
        public String longServer = "szlong.weixin.qq.com";
        public String shortServer = "szshort.weixin.qq.com";
        public String appId = "v1_804188876_CodeVip";
        public String appKey = "v2_1b76be021d21114b6d59bd7edd7c55dc";
        public String machineCode = "v3_8799ed22a680b70e6dbe0596b1a82bdc";
        public String localIp = "120.36.248.152";
        public String driverClass = "com.mysql.jdbc.Driver";
        public String mysqlUrl = "jdbc:mysql://101.132.110.15/bootdo?zeroDateTimeBehavior=convertToNull";
        public String mysqlUserName = "wx";
        public String mysqlPwd = "wx123456";
        public String server_ip = "127.0.0.1";
        public String redis_host = "127.0.0.1";
        //  public String grpc_host = "grpc.wxipad.com:12580";
        public String grpc_host = "10.20.10.11:12580"; //内网
        public String redis_auth = "Chen861212";
        public String bindname = "微咖";
        public String bindManager = "wk";
        public String SHQB = "";
        public String MQID = "";
        public String bindGame;
        public int rateTime;
        public String SOFTWARE_ZF = "888";
        public String SOFTWARE_YY = "666";
        public int maxPoolSize = 50;
        public int minPoolSize = 5;
        public int maxIdleTime = 10;
        public int server_port = 4567;
        //        public int redis_port = 64379;
        public int redis_port = 6679;
        public int redis_db = 4;
        public boolean force_text = false;
        public boolean isForceText = false;
    public String[] rpcServerList = {grpc_host
    };
}
    private static Set set = new Set();
    public static Set getSet() {
        return set;
    }


}
