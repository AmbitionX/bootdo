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
public class ParseRecordDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	private Long id;
	//用户id
	private Long userid;
	//62数据解析记录主键uuid
	private String parsecode;
	//62数据文件地址
	private String fileurl;
	//解析时间
	private Date parsedate;
	//解析状态【1：解析中；2：解析完成】
	private Integer parsestate;
	//解析记录完成时间
	private Date finishdate;

	/**
	 * 设置：主键
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键
	 */
	public Long getId() {
		return id;
	}

	public String getParsecode() {
		return parsecode;
	}

	public void setParsecode(String parsecode) {
		this.parsecode = parsecode;
	}

	/**
	 * 设置：用户id
	 */
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	/**
	 * 获取：用户id
	 */
	public Long getUserid() {
		return userid;
	}
	/**
	 * 设置：62数据文件地址
	 */
	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}
	/**
	 * 获取：62数据文件地址
	 */
	public String getFileurl() {
		return fileurl;
	}
	/**
	 * 设置：解析时间
	 */
	public void setParsedate(Date parsedate) {
		this.parsedate = parsedate;
	}
	/**
	 * 获取：解析时间
	 */
	public Date getParsedate() {
		return parsedate;
	}
	/**
	 * 设置：解析状态【1：解析中；2：解析完成】
	 */
	public void setParsestate(Integer parsestate) {
		this.parsestate = parsestate;
	}
	/**
	 * 获取：解析状态【1：解析中；2：解析完成】
	 */
	public Integer getParsestate() {
		return parsestate;
	}
	/**
	 * 设置：解析记录完成时间
	 */
	public void setFinishdate(Date finishdate) {
		this.finishdate = finishdate;
	}
	/**
	 * 获取：解析记录完成时间
	 */
	public Date getFinishdate() {
		return finishdate;
	}
}
