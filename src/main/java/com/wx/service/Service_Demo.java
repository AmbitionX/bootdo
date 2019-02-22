package com.wx.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.wx.tools.WxLongUtil;
import org.apache.log4j.Logger;

import com.wx.bean.Message;

public class Service_Demo extends BaseService {
	private static Logger logger = Logger.getLogger(Service_Demo.class);
	private boolean longUtilyes=true;


	public Service_Demo(String randomids) {
		super(randomids);//创建时这个逻辑类这个父类生成的基类
	}

	@Override
	public void parseMsg(Message msg) {
//		logger.info(msg.toString());
//		System.out.println(JSONObject.toJSON(msg));
	}

	@Override
	public void setBackName(String wxid, String remark) {
		longUtil.setBackName(wxid,remark);

	}
	//这里是父类的回调，等于是你创建的时候面试先创建的父类，然后基类是继承的这个类，
	//在收到消息后，就会进入这个回调，所以在这里，写全部的逻辑，所以这里也就是逻辑类，在这里的话，调用函数，可以直接调用接口类，
	//因为接口类是基类创建的，基类又是继承的这个逻辑类，所以可以直接在这里调用，你可能会说那我直接从api创建的这个逻辑类，是没错，但是我个人会觉得很乱，所以就这么设计的
}//你只要复制这个类，就可以创建无数的功能类，这个是为了做负载均衡，。比如说你前端，有很多个不同类型的功能，那么你前端首先吧这个请求发到gat去分配
//而gat根据你的请求的初始参数，分配到实际的后端，后端返回服务器id，等参数，前端第二次访问就可以直接访问实际id
//这个是借鉴腾讯的-301重定向的原理来实现的负载均衡

//gat = gateway
//基类=子类  是的，你懂就行，不要在意那些细节  。。。- -  刚开始差点没听懂。。我擦，我这设计的这么科学，没理由不懂，除非你没有慧根  这个必须有   。。那我吸收一下。
//这个，你要是不懂，那只能说在你心里，把这个定义的太“小”，往大了想，这个框架，10W并发无压力
// 行，我们这边再调一下，谢谢你耐心指导。
//客气，互相学习，对了，有个事情，你有没有时间帮我个忙，很简单，gateway的demo帮我解析下wechatmsg那个我前两天农历一天，没弄好，烦死了
//或者这样说，wechatmsg是proto，假如我传来的是json或者说是吧proto转json转实体类的那个
//我这个demo写的不是很好你看你有没有时间，我后端调试很麻烦，
//
// //
// 是spring boot的一开始那个东西？时间有的，就怕给你弄不好，我可以先了解一下