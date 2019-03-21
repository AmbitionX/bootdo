package com.bootdo.baseinfo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 用户账户信息
 * 
 * @author zcg
 *
 * @date 2018-12-27 10:51:15
 */
public class AccountDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户id
	private Long uid;
	//累计赚取佣金
	private BigDecimal totalgainmoney;
	//累计提现金额
	private BigDecimal totalwithdrawmoney;
	//申请提现金额合计（申请未到账的金额合计）
	private BigDecimal applywithdrawmoney;
	//可用金额
	private BigDecimal usemoney;
	//创建时间
	private Date createdate;
	//修改时间
	private Date modifydate;
	// 版本号
	private Integer version;
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
	 * 设置：累计赚取佣金
	 */
	public void setTotalgainmoney(BigDecimal totalgainmoney) {
		this.totalgainmoney = totalgainmoney;
	}
	/**
	 * 获取：累计赚取佣金
	 */
	public BigDecimal getTotalgainmoney() {
		return totalgainmoney;
	}
	/**
	 * 设置：累计提现金额
	 */
	public void setTotalwithdrawmoney(BigDecimal totalwithdrawmoney) {
		this.totalwithdrawmoney = totalwithdrawmoney;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * 获取：累计提现金额
	 */
	public BigDecimal getTotalwithdrawmoney() {
		return totalwithdrawmoney;
	}
	/**
	 * 设置：申请提现金额合计（申请未到账的金额合计）
	 */
	public void setApplywithdrawmoney(BigDecimal applywithdrawmoney) {
		this.applywithdrawmoney = applywithdrawmoney;
	}
	/**
	 * 获取：申请提现金额合计（申请未到账的金额合计）
	 */
	public BigDecimal getApplywithdrawmoney() {
		return applywithdrawmoney;
	}
	/**
	 * 设置：可用金额
	 */
	public void setUsemoney(BigDecimal usemoney) {
		this.usemoney = usemoney;
	}
	/**
	 * 获取：可用金额
	 */
	public BigDecimal getUsemoney() {
		return usemoney;
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
