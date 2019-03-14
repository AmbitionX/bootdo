package com.bootdo.wx.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 微信任务信息
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2018-12-27 17:36:15
 */
public class TaskinfoDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//链接地址
	private String url;
	//公众号名称
	private String wxname;
	//公众号id
	private String wxid;
	//uin,暂不知道什么用处
	private String uin;
	//key,暂不知道什么用处
	private String key;
	//任务类型,1.阅读，2.点赞，3.关注
	private Integer tasktype;
	//任务单价
	private BigDecimal price;
	//操作数量
	private Integer num;
	//任务总金额
	private BigDecimal totalmoney;
	//任务间隔 单位(毫秒)
	private Integer taskperiod;
	//状态 1.未开始，3.未完成，5已完成
	private Integer stauts;
	//已完成数量
	private Integer finishnum;
	//优先级别
	private Integer sort;
	//创建时间
	private Date createdate;
	//修改时间
	private Date modifydate;
	//结算方式
	private Integer settletype;
	// 结算次数
	private Integer settlenum;

	public Integer getSettlenum() {
		return settlenum;
	}

	public void setSettlenum(Integer settlenum) {
		this.settlenum = settlenum;
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
	 * 设置：链接地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 获取：链接地址
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 设置：公众号名称
	 */
	public void setWxname(String wxname) {
		this.wxname = wxname;
	}
	/**
	 * 获取：公众号名称
	 */
	public String getWxname() {
		return wxname;
	}
	/**
	 * 设置：公众号id
	 */
	public void setWxid(String wxid) {
		this.wxid = wxid;
	}
	/**
	 * 获取：公众号id
	 */
	public String getWxid() {
		return wxid;
	}
	/**
	 * 设置：uin,暂不知道什么用处
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}
	/**
	 * 获取：uin,暂不知道什么用处
	 */
	public String getUin() {
		return uin;
	}
	/**
	 * 设置：key,暂不知道什么用处
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * 获取：key,暂不知道什么用处
	 */
	public String getKey() {
		return key;
	}
	/**
	 * 设置：任务类型,1.阅读，2.点赞，3.关注
	 */
	public void setTasktype(Integer tasktype) {
		this.tasktype = tasktype;
	}
	/**
	 * 获取：任务类型,1.阅读，2.点赞，3.关注
	 */
	public Integer getTasktype() {
		return tasktype;
	}
	/**
	 * 设置：任务单价
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * 获取：任务单价
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * 设置：操作数量
	 */
	public void setNum(Integer num) {
		this.num = num;
	}
	/**
	 * 获取：操作数量
	 */
	public Integer getNum() {
		return num;
	}
	/**
	 * 设置：任务总金额
	 */
	public void setTotalmoney(BigDecimal totalmoney) {
		this.totalmoney = totalmoney;
	}
	/**
	 * 获取：任务总金额
	 */
	public BigDecimal getTotalmoney() {
		return totalmoney;
	}
	/**
	 * 设置：任务周期 单位(分钟)
	 */
	public void setTaskperiod(Integer taskperiod) {
		this.taskperiod = taskperiod;
	}
	/**
	 * 获取：任务间隔 单位(毫秒)
	 */
	public Integer getTaskperiod() {
		return taskperiod;
	}
	/**
	 * 设置：状态 1.未开始，3.未完成，5已完成
	 */
	public void setStauts(Integer stauts) {
		this.stauts = stauts;
	}
	/**
	 * 获取：状态 1.未开始，3.未完成，5已完成
	 */
	public Integer getStauts() {
		return stauts;
	}
	/**
	 * 设置：已完成数量
	 */
	public void setFinishnum(Integer finishnum) {
		this.finishnum = finishnum;
	}
	/**
	 * 获取：已完成数量
	 */
	public Integer getFinishnum() {
		return finishnum;
	}
	/**
	 * 设置：优先级别
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	/**
	 * 获取：优先级别
	 */
	public Integer getSort() {
		return sort;
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
	 * 获取：结算类型
	 */
	public Integer getSettletype() {
		return settletype;
	}

	/**
	 * 设置：结算类型
	 */
	public void setSettletype(Integer settletype) {
		this.settletype = settletype;
	}
}
