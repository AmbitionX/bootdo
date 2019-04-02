package com.bootdo.system.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 
 * 
 * @author zcg
 *
 * @date 2018-12-27 11:04:16
 */
public class UserDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Long userId;
	//用户名
	private String username;
	//
	private String name;
	//密码
	private String password;
	//
	private Long deptId;
	private String deptName;

	private int maxnum;

	public int getMaxnum() {
		return maxnum;
	}

	public void setMaxnum(int maxnum) {
		this.maxnum = maxnum;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	//邮箱
	private String email;
	//手机号
	private String mobile;
	//状态 0:禁用，1:正常
	private Integer status;
	//创建用户id
	private Long userIdCreate;
	//创建时间
	private Date gmtCreate;
	//修改时间
	private Date gmtModified;
	//性别
	private Long sex;
	//出身日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birth;
	//
	private Long picId;
	//现居住地
	private String liveAddress;
	//爱好
	private String hobby;
	//省份
	private String province;
	//所在城市
	private String city;
	//所在地区
	private String district;
	//支付宝账号
	private String alipay;
	//微信账号
	private String wechat;
	//邀请码
	private String invitecode;
	//邀请人id
	private Long parentid;
	//计费方式 1.按天,2.按任务量
	private Integer billtype;

	private String veriCode;

	public String getVeriCode() {
		return veriCode;
	}

	public void setVeriCode(String veriCode) {
		this.veriCode = veriCode;
	}

	//角色
	private List<Long> roleIds;

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	/**
	 * 设置：
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取：用户名
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置：
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 获取：密码
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置：
	 */
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	/**
	 * 获取：
	 */
	public Long getDeptId() {
		return deptId;
	}
	/**
	 * 设置：邮箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * 获取：邮箱
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * 设置：手机号
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * 获取：手机号
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * 设置：状态 0:禁用，1:正常
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：状态 0:禁用，1:正常
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置：创建用户id
	 */
	public void setUserIdCreate(Long userIdCreate) {
		this.userIdCreate = userIdCreate;
	}
	/**
	 * 获取：创建用户id
	 */
	public Long getUserIdCreate() {
		return userIdCreate;
	}
	/**
	 * 设置：创建时间
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * 设置：修改时间
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	/**
	 * 获取：修改时间
	 */
	public Date getGmtModified() {
		return gmtModified;
	}
	/**
	 * 设置：性别
	 */
	public void setSex(Long sex) {
		this.sex = sex;
	}
	/**
	 * 获取：性别
	 */
	public Long getSex() {
		return sex;
	}
	/**
	 * 设置：出身日期
	 */
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	/**
	 * 获取：出身日期
	 */
	public Date getBirth() {
		return birth;
	}
	/**
	 * 设置：
	 */
	public void setPicId(Long picId) {
		this.picId = picId;
	}
	/**
	 * 获取：
	 */
	public Long getPicId() {
		return picId;
	}
	/**
	 * 设置：现居住地
	 */
	public void setLiveAddress(String liveAddress) {
		this.liveAddress = liveAddress;
	}
	/**
	 * 获取：现居住地
	 */
	public String getLiveAddress() {
		return liveAddress;
	}
	/**
	 * 设置：爱好
	 */
	public void setHobby(String hobby) {
		this.hobby = hobby;
	}
	/**
	 * 获取：爱好
	 */
	public String getHobby() {
		return hobby;
	}
	/**
	 * 设置：省份
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * 获取：省份
	 */
	public String getProvince() {
		return province;
	}
	/**
	 * 设置：所在城市
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * 获取：所在城市
	 */
	public String getCity() {
		return city;
	}
	/**
	 * 设置：所在地区
	 */
	public void setDistrict(String district) {
		this.district = district;
	}
	/**
	 * 获取：所在地区
	 */
	public String getDistrict() {
		return district;
	}
	/**
	 * 设置：支付宝账号
	 */
	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}
	/**
	 * 获取：支付宝账号
	 */
	public String getAlipay() {
		return alipay;
	}
	/**
	 * 设置：微信账号
	 */
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
	/**
	 * 获取：微信账号
	 */
	public String getWechat() {
		return wechat;
	}
	/**
	 * 设置：邀请码
	 */
	public void setInvitecode(String invitecode) {
		this.invitecode = invitecode;
	}
	/**
	 * 获取：邀请码
	 */
	public String getInvitecode() {
		return invitecode;
	}
	/**
	 * 设置：邀请人id
	 */
	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}
	/**
	 * 获取：邀请人id
	 */
	public Long getParentid() {
		return parentid;
	}
	/**
	 * 设置：计费方式 1.按天,2.按任务量
	 */
	public void setBilltype(Integer billtype) {
		this.billtype = billtype;
	}
	/**
	 * 获取：计费方式 1.按天,2.按任务量
	 */
	public Integer getBilltype() {
		return billtype;
	}
}
