package com.bootdo.baseinfo.domain;

import java.io.Serializable;
import java.util.Date;



/**
 * 微信号信息
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-02-21 13:42:06
 */
public class WechatDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户id
	private Long uid;
	//父级用户id(邀请人id)
	private Long parentid;
	//微信号
	private String wechat;
	//微信密码
	private String password;
	//62数据
	private String data62;
	//最近一次做任务时间
	private Date lastdate;
	//累计任务次数
	private Integer totaltaskquantity;
	//今日任务次数
	private Integer todaytaskquantity;
	//状态 1.启用,2.停用,3.占用
	private Integer stauts;
	//微信状态备注
	private String remark;
	//创建时间
	private Date createdate;
	//修改时间
	private Date modifydate;
	//任务id
	private Integer taskid;
	//服务id
	private String randomid;
	//loginedUser
	private String sessionkey;
	//loginedUser.deviceid
	private String deviceid;
	//loginedUser.maxsynckey
	private String maxsynckey;
	//loginedUser.uin
	private String uin;
	//loginedUser.AutoAuthKey
	private String autoauthkey;
	//loginedUser.Cookies
	private String cookies;
	//loginedUser.CurrentsyncKey
	private String currentsynckey;
	//loginedUser.DeviceName
	private String devicename;
	//loginedUser.DeviceType
	private String devicetype;
	//loginedUser.NickName
	private String nickname;
	//loginedUser.UserName
	private String username;
	//loginedUser.UserExt
	private String userext;

	public Long getParentid() {
		return parentid;
	}

	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}

	/**
	 * 设置：
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：用户id
	 */
	public void setUid(Long uid) {
		this.uid = uid;
	}
	/**
	 * 获取：用户id
	 */
	public Long getUid() {
		return uid;
	}
	/**
	 * 设置：微信号
	 */
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
	/**
	 * 获取：微信号
	 */
	public String getWechat() {
		return wechat;
	}
	/**
	 * 设置：微信密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 获取：微信密码
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置：62数据
	 */
	public void setData62(String data62) {
		this.data62 = data62;
	}
	/**
	 * 获取：62数据
	 */
	public String getData62() {
		return data62;
	}
	/**
	 * 设置：最近一次做任务时间
	 */
	public void setLastdate(Date lastdate) {
		this.lastdate = lastdate;
	}
	/**
	 * 获取：最近一次做任务时间
	 */
	public Date getLastdate() {
		return lastdate;
	}
	/**
	 * 设置：累计任务次数
	 */
	public void setTotaltaskquantity(Integer totaltaskquantity) {
		this.totaltaskquantity = totaltaskquantity;
	}
	/**
	 * 获取：累计任务次数
	 */
	public Integer getTotaltaskquantity() {
		return totaltaskquantity;
	}
	/**
	 * 设置：今日任务次数
	 */
	public void setTodaytaskquantity(Integer todaytaskquantity) {
		this.todaytaskquantity = todaytaskquantity;
	}
	/**
	 * 获取：今日任务次数
	 */
	public Integer getTodaytaskquantity() {
		return todaytaskquantity;
	}
	/**
	 * 设置：状态 1.启用,2.停用,3.占用
	 */
	public void setStauts(Integer stauts) {
		this.stauts = stauts;
	}
	/**
	 * 获取：状态 1.启用,2.停用,3.占用
	 */
	public Integer getStauts() {
		return stauts;
	}
	/**
	 * 设置：微信状态备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 获取：微信状态备注
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreatedate() {
		return createdate;
	}
	/**
	 * 设置：修改时间
	 */
	public void setModifydate(Date modifydate) {
		this.modifydate = modifydate;
	}
	/**
	 * 获取：修改时间
	 */
	public Date getModifydate() {
		return modifydate;
	}
	/**
	 * 设置：任务id
	 */
	public void setTaskid(Integer taskid) {
		this.taskid = taskid;
	}
	/**
	 * 获取：任务id
	 */
	public Integer getTaskid() {
		return taskid;
	}
	/**
	 * 设置：服务id
	 */
	public void setRandomid(String randomid) {
		this.randomid = randomid;
	}
	/**
	 * 获取：服务id
	 */
	public String getRandomid() {
		return randomid;
	}
/*	*//**
	 * 设置：loginedUser
	 *//*
	public void setSessionkey(String sessionkey) {
		this.sessionkey = sessionkey;
	}
	*//**
	 * 获取：loginedUser
	 *//*
	public String getSessionkey() {
		return sessionkey;
	}*/



	/**
	 * 设置：loginedUser.deviceid
	 */
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	/**
	 * 获取：loginedUser.deviceid
	 */
	public String getDeviceid() {
		return deviceid;
	}
	/**
	 * 设置：loginedUser.maxsynckey
	 */
/*	public void setMaxsynckey(String maxsynckey) {
		this.maxsynckey = maxsynckey;
	}
	*//**
	 * 获取：loginedUser.maxsynckey
	 *//*
	public String getMaxsynckey() {
		return maxsynckey;
	}*/


	/**
	 * 设置：loginedUser.uin
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}
	/**
	 * 获取：loginedUser.uin
	 */
	public String getUin() {
		return uin;
	}
	/**
	 * 设置：loginedUser.AutoAuthKey
	 */
/*	public void setAutoauthkey(String autoauthkey) {
		this.autoauthkey = autoauthkey;
	}
	*//**
	 * 获取：loginedUser.AutoAuthKey
	 *//*
	public String getAutoauthkey() {
		return autoauthkey;
	}
	*//**
	 * 设置：loginedUser.Cookies
	 *//*
	public void setCookies(String cookies) {
		this.cookies = cookies;
	}
	*//**
	 * 获取：loginedUser.Cookies
	 *//*
	public String getCookies() {
		return cookies;
	}
	*//**
	 * 设置：loginedUser.CurrentsyncKey
	 *//*
	public void setCurrentsynckey(String currentsynckey) {
		this.currentsynckey = currentsynckey;
	}
	*//**
	 * 获取：loginedUser.CurrentsyncKey
	 *//*
	public String getCurrentsynckey() {
		return currentsynckey;
	}*/

	public String getSessionkey() {
		return sessionkey;
	}

	public void setSessionkey(String sessionkey) {
		this.sessionkey = sessionkey;
	}

	public String getMaxsynckey() {
		return maxsynckey;
	}

	public void setMaxsynckey(String maxsynckey) {
		this.maxsynckey = maxsynckey;
	}

	public String getAutoauthkey() {
		return autoauthkey;
	}

	public void setAutoauthkey(String autoauthkey) {
		this.autoauthkey = autoauthkey;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	public String getCurrentsynckey() {
		return currentsynckey;
	}

	public void setCurrentsynckey(String currentsynckey) {
		this.currentsynckey = currentsynckey;
	}

	/**
	 * 设置：loginedUser.DeviceName
	 */
	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}
	/**
	 * 获取：loginedUser.DeviceName
	 */
	public String getDevicename() {
		return devicename;
	}
	/**
	 * 设置：loginedUser.DeviceType
	 */
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}
	/**
	 * 获取：loginedUser.DeviceType
	 */
	public String getDevicetype() {
		return devicetype;
	}
	/**
	 * 设置：loginedUser.NickName
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * 获取：loginedUser.NickName
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * 设置：loginedUser.UserName
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取：loginedUser.UserName
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置：loginedUser.UserExt
	 */
	public void setUserext(String userext) {
		this.userext = userext;
	}
	/**
	 * 获取：loginedUser.UserExt
	 */
	public String getUserext() {
		return userext;
	}
}
