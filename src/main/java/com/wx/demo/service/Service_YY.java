package com.wx.demo.service;

import com.wx.demo.bean.Message;
import org.apache.log4j.Logger;

public class Service_YY extends BaseService{
	
	 private static Logger logger = Logger.getLogger(Service_YY.class);

	public Service_YY(String randomids) {
		super(randomids);
	}

	@Override
	public void parseMsg(Message msg) {
		
	}

}
