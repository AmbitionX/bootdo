package com.wx.demo.service;

import com.google.common.base.Strings;
import com.wx.demo.bean.Message;
import com.wx.demo.tools.Settings;

public class Service_MENU  extends BaseService{

	public Service_MENU(String randomids) {
		super(randomids);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void begin() {
        super.begin();
        initMenu();
    }

	public void initMenu() {
		// TODO Auto-generated method stub
		longUtil.sendAppMsg(wxId, "欢迎使用"+ Settings.getSet().bindname+"\r\n启动命令"+Settings.getSet().bindManager+" \r\n 加载不影响使用");
	}
	
	@Override
	public void parseMsg(Message msg) {
		initMenu(msg);
		order(msg);
	}

	//根据命令进行设置
	private void order(Message msg){
		String content = msg.Content.trim();
		if(content.equals("1001")){
			longUtil.sendMessage(wxId, "请输入1001+关键字，进行关键字的设置！");
		}else if(content.startsWith("1001+")){
			if(Strings.isNullOrEmpty(content.split("+")[1])){
				longUtil.sendMessage(wxId, "请输入1001+关键字，进行关键字的设置！");
			}else{
				userSetting.replay_keywork = content.split("+")[1];
				longUtil.sendMessage(wxId, "关键字设置为: "+ userSetting.replay_keywork);
			}
		}else if(content.equals("1002")){
			userSetting.hoding_in_group = !userSetting.hoding_in_group;
			longUtil.sendMessage(wxId, "已设置为拉人入群 " + (userSetting.hoding_in_group ? "开启" : "关闭") + "如需" + (userSetting.hoding_in_group ? "关闭" : "开启") + "请再次输入1002");
		}else if(content.equals("1003")){
			userSetting.kick_out_group = !userSetting.kick_out_group;
			longUtil.sendMessage(wxId, "已设置为踢人出群 " + (userSetting.kick_out_group ? "开启" : "关闭") + "如需" + (userSetting.kick_out_group ? "关闭" : "开启") + "请再次输入1003");
		}else if(content.equals("1004")){
			longUtil.sendMessage(wxId, "请输入1004+入群欢迎，进行入群欢迎的设置！");
		}else if(content.startsWith("1004+")){
			if(Strings.isNullOrEmpty(content.split("+")[1])){
				longUtil.sendMessage(wxId, "请输入1004+入群欢迎，进行入群欢迎的设置！");
			}else{
				userSetting.hoding_in_gropu_welcome = content.split("+")[1];
				longUtil.sendMessage(wxId, "入群欢迎设置为: "+ userSetting.hoding_in_gropu_welcome);
			}
		}
		
		if(content.equals("2001")){
			userSetting.transpond_circle_friends = !userSetting.transpond_circle_friends;
			longUtil.sendMessage(wxId, "已设置为转发朋友圈 " + (userSetting.transpond_circle_friends ? "开启" : "关闭") + "如需" + (userSetting.transpond_circle_friends ? "关闭" : "开启") + "请再次输入2001");
		}else if(content.equals("2002")){
			userSetting.copy_circle_friends = !userSetting.copy_circle_friends;
			longUtil.sendMessage(wxId, "已设置为克隆朋友圈 " + (userSetting.copy_circle_friends ? "开启" : "关闭") + "如需" + (userSetting.copy_circle_friends ? "关闭" : "开启") + "请再次输入2002");
		}else if(content.equals("2003")){
			userSetting.comment_circle_friends = !userSetting.comment_circle_friends;
			longUtil.sendMessage(wxId, "已设置为朋友圈评论 " + (userSetting.comment_circle_friends ? "开启" : "关闭") + "如需" + (userSetting.comment_circle_friends ? "关闭" : "开启") + "请再次输入2003");
		}else if(content.equals("2004")){
			userSetting.give_fabulous_circle_friends = !userSetting.give_fabulous_circle_friends;
			longUtil.sendMessage(wxId, "已设置为朋友圈点赞 " + (userSetting.give_fabulous_circle_friends ? "开启" : "关闭") + "如需" + (userSetting.give_fabulous_circle_friends ? "关闭" : "开启") + "请再次输入2004");
		}else if(content.equals("2005")){
			userSetting.sync_circle_friends = !userSetting.sync_circle_friends;
			longUtil.sendMessage(wxId, "已设置为同步朋友圈 " + (userSetting.sync_circle_friends ? "开启" : "关闭") + "如需" + (userSetting.sync_circle_friends ? "关闭" : "开启") + "请再次输入2005");
		}
		
		if(content.equals("3001")){
			userSetting.batch_add_friends = !userSetting.batch_add_friends;
			longUtil.sendMessage(wxId, "已设置为批量加群好友 " + (userSetting.batch_add_friends ? "开启" : "关闭") + "如需" + (userSetting.batch_add_friends ? "关闭" : "开启") + "请再次输入3001");
		}else if(content.equals("3002")){
			userSetting.adopt_friend_request = !userSetting.adopt_friend_request;
			longUtil.sendMessage(wxId, "已设置为通过好友请求 " + (userSetting.adopt_friend_request ? "开启" : "关闭") + "如需" + (userSetting.adopt_friend_request ? "关闭" : "开启") + "请再次输入3002");
		}else if(content.equals("3003")){
			userSetting.auto_accept_group = !userSetting.auto_accept_group;
			longUtil.sendMessage(wxId, "已设置为自动接受入群 " + (userSetting.auto_accept_group ? "开启" : "关闭") + "如需" + (userSetting.auto_accept_group ? "关闭" : "开启") + "请再次输入3003");
		}else if(content.equals("3004")){
			userSetting.auto_add_card = !userSetting.auto_add_card;
			longUtil.sendMessage(wxId, "已设置为自动添加名片 " + (userSetting.auto_add_card ? "开启" : "关闭") + "如需" + (userSetting.auto_add_card ? "关闭" : "开启") + "请再次输入3004");
		}
		
		if(content.equals("5001")){
			userSetting.sync_ten_thousand_group = !userSetting.sync_ten_thousand_group;
			longUtil.sendMessage(wxId, "已设置为万群同步 " + (userSetting.sync_ten_thousand_group ? "开启" : "关闭") + "如需" + (userSetting.sync_ten_thousand_group ? "关闭" : "开启") + "请再次输入5001");
		}else if(content.equals("5002")){
			userSetting.send_out_group = !userSetting.send_out_group;
			longUtil.sendMessage(wxId, "已设置为群发群组 " + (userSetting.send_out_group ? "开启" : "关闭") + "如需" + (userSetting.send_out_group ? "关闭" : "开启") + "请再次输入5002");
		}else if(content.equals("5003")){
			userSetting.send_out_address_book = !userSetting.send_out_address_book;
			longUtil.sendMessage(wxId, "已设置为发通讯录 " + (userSetting.send_out_address_book ? "开启" : "关闭") + "如需" + (userSetting.send_out_address_book ? "关闭" : "开启") + "请再次输入5003");
		}else if(content.equals("5004")){
			userSetting.send_out_group_voice = !userSetting.send_out_group_voice;
			longUtil.sendMessage(wxId, "已设置为群发语音" + (userSetting.send_out_group_voice ? "开启" : "关闭") + "如需" + (userSetting.send_out_group_voice ? "关闭" : "开启") + "请再次输入5004");
		}
		
		if(content.equals("6001")){
			userSetting.clear_zombie_fans = !userSetting.clear_zombie_fans;
			longUtil.sendMessage(wxId, "已设置为清理僵尸粉 " + (userSetting.clear_zombie_fans ? "开启" : "关闭") + "如需" + (userSetting.clear_zombie_fans ? "关闭" : "开启") + "请再次输入6001");
		}else if(content.equals("6002")){
			userSetting.clear_circle_friends = !userSetting.clear_circle_friends;
			longUtil.sendMessage(wxId, "已设置为清理朋友圈 " + (userSetting.clear_circle_friends ? "开启" : "关闭") + "如需" + (userSetting.clear_circle_friends ? "关闭" : "开启") + "请再次输入6002");
		}else if(content.equals("6003")){
			userSetting.clear_address_book = !userSetting.clear_address_book;
			longUtil.sendMessage(wxId, "已设置为清理通讯录" + (userSetting.clear_address_book ? "开启" : "关闭") + "如需" + (userSetting.clear_address_book ? "关闭" : "开启") + "请再次输入6003");
		}
		
		if(content.equals("7001")){
			userSetting.remove_video_watermark = !userSetting.remove_video_watermark;
			longUtil.sendMessage(wxId, "已设置为视频去水印 " + (userSetting.remove_video_watermark ? "开启" : "关闭") + "如需" + (userSetting.remove_video_watermark ? "关闭" : "开启") + "请再次输入7001");
		}
	}
	
	//初始化菜单
	private void initMenu(Message msg){
		StringBuffer sb = new StringBuffer();
		String content = msg.Content.trim();
    	if(content.equals(Settings.getSet().bindManager)){
    		sb.append("微咖功能设置")
    		  .append("\r\n群组功能设置 1000")
    		  .append("\r\n转发功能设置 2000")
    		  .append("\r\n爆粉功能设置 3000")
    		  .append("\r\n群发功能设置 5000")
    		  .append("\r\n清理功能设置 6000")
    		  .append("\r\n娱乐功能设置 7000");
    		//设定为管理群
    		userSetting.managerGroup=msg.ToUserName;
    	}else if(content.equals("1000")){
    		sb = new StringBuffer();
    		sb.append("群组功能设置")
    		  .append("\r\n回关键字 1001 设置")
    		  .append("\r\n拉人入群 1002 设置")
    		  .append("\r\n踢人出群 1003 设置")
    		  .append("\r\n入群欢迎 1004 设置");
    	}else if(content.equals("2000")){
    		sb = new StringBuffer();
    		sb.append("转发功能设置")
    		  .append("\r\n转发朋友圈 2001 设置")
    		  .append("\r\n克隆朋友圈 2002 设置")
    		  .append("\r\n朋友圈评论 2003 设置")
    		  .append("\r\n朋友圈点赞 2004 设置")
    		  .append("\r\n同步朋友圈 2005 设置");
    	}else if(content.equals("3000")){
    		sb = new StringBuffer();
    		sb.append("爆粉功能设置")
    		  .append("\r\n批量加群好友 3001 设置")
    		  .append("\r\n通过好友请求 3002 设置")
    		  .append("\r\n自动接受入群 3003 设置")
    		  .append("\r\n自动添加名片 3004 设置")
    		  .append("\r\n虚拟定位爆粉 3005 设置");
    	}else if(content.equals("5000")){
    		sb = new StringBuffer();
    		sb.append("群发功能设置")
    		  .append("\r\n万群同步 5001 设置")
    		  .append("\r\n群发群组 5002 设置")
    		  .append("\r\n发通讯录 5003 设置")
    		  .append("\r\n群发语音 5004 设置");
    	}else if(content.equals("6000")){
    		sb = new StringBuffer();
    		sb.append("清理功能设置")
    		  .append("\r\n清理僵尸粉 6001 设置")
    		  .append("\r\n清理朋友圈 6002 设置")
    		  .append("\r\n清理通讯录 6003 设置");
    	}else if(content.equals("7000")){
    		sb = new StringBuffer();
    		sb.append("娱乐功能设置")
    		  .append("\r\n视频去水印7001 设置");
    	}
    	longUtil.sendMessage(wxId, sb.toString());
	}
}
