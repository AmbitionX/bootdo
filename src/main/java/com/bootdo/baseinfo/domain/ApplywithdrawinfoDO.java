package com.bootdo.baseinfo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 提现申请
 * 
 * @author zcg
 *
 * @date 2018-12-27 10:51:15
 */
public class ApplywithdrawinfoDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户id
	private Long uid;
	//手机号码
	private String phone;
	//申请提现金额
	private BigDecimal applymoney;
	//支付方式 1.支付宝，2.微信
	private String paytype;
	//收款账号
	private String account;
	//状态 1.未支付,2.已支付,3.审核失败
	private Integer stauts;
	//备注
	private String remark;
	//创建时间
	private Date createdate;
	//修改时间
	private Date modifydate;
	// 用户名
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	 * 设置：手机号码
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * 获取：手机号码
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * 设置：申请提现金额
	 */
	public void setApplymoney(BigDecimal applymoney) {
		this.applymoney = applymoney;
	}
	/**
	 * 获取：申请提现金额
	 */
	public BigDecimal getApplymoney() {
		return applymoney;
	}
	/**
	 * 设置：支付方式 1.支付宝，2.微信
	 */
	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}
	/**
	 * 获取：支付方式 1.支付宝，2.微信
	 */
	public String getPaytype() {
		return paytype;
	}
	/**
	 * 设置：收款账号
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * 获取：收款账号
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * 设置：状态 1.未支付,2.已支付,3.审核失败
	 */
	public void setStauts(Integer stauts) {
		this.stauts = stauts;
	}
	/**
	 * 获取：状态 1.未支付,2.已支付,3.审核失败
	 */
	public Integer getStauts() {
		return stauts;
	}
	/**
	 * 设置：备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 获取：备注
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
}
