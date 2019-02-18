package com.bootdo.wx.domain;

import java.io.Serializable;
import java.util.Date;



/**
 * 
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-02-17 15:10:32
 */
public class WxuserDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private String account;
	//
	private String usercode;
	//
	private String wxid;
	//
	private String nickname;
	//
	private Long deadtime;
	//
	private String settings;
	//
	private String token;
	//
	private String wxdat;
	//
	private String serverid;
	//
	private String softwareid;
	//
	private String randomid;
	//
	private Integer status;

	/**
	 * 设置：
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * 获取：
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * 设置：
	 */
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	/**
	 * 获取：
	 */
	public String getUsercode() {
		return usercode;
	}
	/**
	 * 设置：
	 */
	public void setWxid(String wxid) {
		this.wxid = wxid;
	}
	/**
	 * 获取：
	 */
	public String getWxid() {
		return wxid;
	}
	/**
	 * 设置：
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * 获取：
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * 设置：
	 */
	public void setDeadtime(Long deadtime) {
		this.deadtime = deadtime;
	}
	/**
	 * 获取：
	 */
	public Long getDeadtime() {
		return deadtime;
	}
	/**
	 * 设置：
	 */
	public void setSettings(String settings) {
		this.settings = settings;
	}
	/**
	 * 获取：
	 */
	public String getSettings() {
		return settings;
	}
	/**
	 * 设置：
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * 获取：
	 */
	public String getToken() {
		return token;
	}
	/**
	 * 设置：
	 */
	public void setWxdat(String wxdat) {
		this.wxdat = wxdat;
	}
	/**
	 * 获取：
	 */
	public String getWxdat() {
		return wxdat;
	}
	/**
	 * 设置：
	 */
	public void setServerid(String serverid) {
		this.serverid = serverid;
	}
	/**
	 * 获取：
	 */
	public String getServerid() {
		return serverid;
	}
	/**
	 * 设置：
	 */
	public void setSoftwareid(String softwareid) {
		this.softwareid = softwareid;
	}
	/**
	 * 获取：
	 */
	public String getSoftwareid() {
		return softwareid;
	}
	/**
	 * 设置：
	 */
	public void setRandomid(String randomid) {
		this.randomid = randomid;
	}
	/**
	 * 获取：
	 */
	public String getRandomid() {
		return randomid;
	}
	/**
	 * 设置：
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：
	 */
	public Integer getStatus() {
		return status;
	}
}
