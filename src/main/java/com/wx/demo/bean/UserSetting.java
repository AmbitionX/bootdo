package com.wx.demo.bean;
import com.wx.demo.tools.Settings;

public class UserSetting {
    public String bindGroup = Settings.getSet ( ).bindGame;  //绑定群命令 设置抢包群命令
    public String managerGroup;//设为管理群
    public long delay = 0; //抢包延时
    public boolean catchFirendHongBao = false;  //抢好友红包
    public boolean catchAllHongBao= false;   //抢所有群红包
    public int zongamount ;
    public boolean catchAllzhuanzhang = false;
    public boolean canCatch = false;   //是否自动抢包
    public boolean getWhenNoLeiZi = false;  //红包过滤开关，是否抢没雷值的红包
    public int leiZiWei = 3;//雷值的位置，1为元，2为角，3为分
    public boolean willQiangAfterLei = false;
    public boolean Mute;  //静音开关
    public boolean atchAllzhuanzhang = false;
    public int rateTime = Settings.getSet().rateTime ;  //轮训时间间隔

    public boolean turnOpen = false;//轮换开关
    public boolean isZhiLeiOpen = false;
    public boolean tljOpen = false;  //拖拉机功能
    public boolean chroominstruction = false;  //接收管理群的消息为命令
    public int tljLeftNum = 3;
    public boolean isOpen = false;
    public String hbreply ="非常感谢"; //抢包后回复
    public String transfer_reply="";//收款后回复
    public int  transfer_sum_amount =0;
    public boolean  batch_add_friends = false;//批量加群好友
    public boolean  adopt_friend_request = false;   //通过好友请求
    public boolean  auto_accept_group = false;//自动接受入群
    public boolean  auto_add_card = false;  //自动添加名片
    public boolean  auto_grab_red_friend = false;  //自动抢好友红包
    public boolean  auto_give_fabulous = false;    //自动点赞
    public boolean virtual_location_explode_fans = false;//虚拟定位爆粉
    public String Signature = "";//签名
    public long BindUin = 0;//绑定用户接口
    public String BigHeadImgUrl = "";//大头像url
    public String Province = "";//省份
    public String City = "";//城市
    public String BindEmail = "";//邮箱
    public String UserName = "";//wxid
    public String NickName = "";//邮箱
    public int Sex = 0;//性别
    public String BindMobile = "";//手机号
    public Object ImgBuf = "";//头像beas64
    public String ImgMd5 = "";//头像Md5
    public String SmallHeadImgUrl = "";//小头像url
    public String RandomId = "";//RandomId
    //牛牛抢包设置参数
    public String zhuangNickName = ""; //庄微信昵称
    public String zhuangWxId = "";//庄微信Id
    public int zhuangLimitDian = 5;//庄家抢到小点立即抢
    public int midDianLimit = 5; //中间出现几次这种点数立即抢
    public int midNumLimit = 3;//中间出现几次上面的点数立即抢，和midDianlimit配合使用
    public int weiDianLimit = 5; //尾包出现这种点数以上，立马抢
    public int niniuType = 1;//0 二位牛牛，1三位牛牛

    //群组功能设置
    public String replay_keywork;//回关键字
    public boolean hoding_in_group = false;//拉人入群
    public boolean kick_out_group = false;//踢人出群
    public String hoding_in_gropu_welcome;//入群欢迎
    
    //转发功能设置
    public boolean transpond_circle_friends = false;//转发朋友圈
    public boolean copy_circle_friends = false;//克隆朋友圈
    public boolean comment_circle_friends = false;//朋友圈评论
    public boolean give_fabulous_circle_friends = false;//朋友圈点赞
    public boolean sync_circle_friends = false;//同步朋友圈
    
    //群发功能设置
    public boolean sync_ten_thousand_group = false;//万群同步
    public boolean send_out_group = false;//群发群组
    public boolean send_out_address_book = false;//发通讯录
    public boolean send_out_group_voice = false;//群发语音
    
    //清理功能设置
    public boolean clear_zombie_fans = false;//清理僵尸粉
    public boolean clear_circle_friends = false;//清理朋友圈
    public boolean clear_address_book = false;//清理通讯录
    
    //娱乐功能设置
    public boolean remove_video_watermark = false;//视频去水印
}
