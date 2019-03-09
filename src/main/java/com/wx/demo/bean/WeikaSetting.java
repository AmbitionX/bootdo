package com.wx.demo.bean;


public class WeikaSetting {
    public boolean  hongbao=true;
    public long    hbdelay=1600;
    public boolean  filter_packet=true;
    public String hbreply ="非常感谢";
    public String group_command="哈哈哈";
    public boolean  transfer=true;
    public String transfer_reply="";
    public int  transfer_sum_amount =0;
    public boolean  batch_add_friends = false;//批量加群好友
    public boolean  adopt_friend_request = false;   //通过好友请求
    public boolean  auto_accept_group = false;//自动接受入群
    public boolean  auto_add_card = false;  //自动添加名片
    public boolean  auto_grab_red_friend = false;  //自动抢好友红包
    public boolean  auto_grab_red_group = false;  //自动抢群红包
    public boolean  auto_accept_transfer = false;    //自动接受转账
    public boolean  auto_give_fabulous = false;    //自动点赞
}
