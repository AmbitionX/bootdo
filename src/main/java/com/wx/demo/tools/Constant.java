package com.wx.demo.tools;

public interface Constant {
    public static final String redisk_key_loinged_user = "robot_logined_users";
    public static final String DEFAULT_DECODE = "UTF-8";
    public static final String WX_READ_NUM_URL="https://mp.weixin.qq.com/mp/getappmsgext?";

    //任务使用
    public final String prefix_task = "wx_task_";


    public static final String softwareId="666";
    public static final boolean autoLogin=true;
    public static final Integer protocolVer=1; // 1. ipad ,2.不知道 , 3. mac ,4.不知道 , 5.windows
    public static final String scene="2"; // Scene = 2 来源好友或群 必须设置来源的id 3 历史阅读 4 二维码连接 7 来源公众号 必须设置公众号的id
    public static final int DEFAULT_PROTOCOLVER=1;
    public static final String scene30="30"; //扫码
}
