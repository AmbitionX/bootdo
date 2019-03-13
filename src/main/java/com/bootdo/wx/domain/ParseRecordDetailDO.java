package com.bootdo.wx.domain;

import java.io.Serializable;
import java.util.Date;



/**
 * 
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-03-11 10:30:23
 */
public class ParseRecordDetailDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//解析记录明细主键
	private Long id;
	//解析任务Code
	private String parseCode;
	//62数据账号
	private String username;
	//62数据密码
	private String password;
	//62数据内容
	private String wxdata;
	//解析明细状态【1：执行中；2：执行成功；3：执行失败；4：平台已经存在】
	private Integer state;
	//创建时间
	private Date ctime;
	//修改时间
	private Date utime;

	/**
	 * 设置：解析记录明细主键
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：解析记录明细主键
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：解析任务外键id
	 */
	public void setParseCode(String parseCode) {
		this.parseCode = parseCode;
	}
	/**
	 * 获取：解析任务外键id
	 */
	public String getParseCode() {
		return parseCode;
	}
	/**
	 * 设置：62数据账号
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取：62数据账号
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置：62数据密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 获取：62数据密码
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置：62数据内容
	 */
	public void setWxdata(String wxdata) {
		this.wxdata = wxdata;
	}
	/**
	 * 获取：62数据内容
	 */
	public String getWxdata() {
		return wxdata;
	}
	/**
	 * 设置：解析明细状态【1：执行中；2：执行成功；3：执行失败；4：平台已经存在】
	 */
	public void setState(Integer state) {
		this.state = state;
	}
	/**
	 * 获取：解析明细状态【1：执行中；2：执行成功；3：执行失败；4：平台已经存在】
	 */
	public Integer getState() {
		return state;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCtime() {
		return ctime;
	}
	/**
	 * 设置：修改时间
	 */
	public void setUtime(Date utime) {
		this.utime = utime;
	}
	/**
	 * 获取：修改时间
	 */
	public Date getUtime() {
		return utime;
	}
}
