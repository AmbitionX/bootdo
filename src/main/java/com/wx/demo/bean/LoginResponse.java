package com.wx.demo.bean;

public class LoginResponse {
	public BaseMsg baseMsg;
	public static class BaseMsg{
		public int Ret;
		public User user;
	}
	public static class User{
		public long Uin;
		public String UserName;
		public String NickName;
		public String DeviceName;
		public String DeviceType;
	}
}
