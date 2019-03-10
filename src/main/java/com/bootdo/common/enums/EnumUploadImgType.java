package com.bootdo.common.enums;
/**
 * 上传图片类型  枚举类 ： 用于获取 配置文件中存储路径
 * @author zlhx
 * @version 2.0
 * @date 2015-05-09 13:52
 */
public enum EnumUploadImgType {
	/**
	 * 积分商城
	 */
	GOODS("goods"),
	/**
	 * 文章模块
	 */
	ARTICLE("article"),
	/**
	 * 资料上传
	 */
	USERINFO("userinfo"),
	/**
	 * 发标图片
	 */
	BORROW("borrow"),
	/**
	 * 支付接口
	 */
	PAY("pay"),
	/**
	 * 其他图片
	 */
	UPFILES("upfiles")
	;
	
	private String value;
	
	EnumUploadImgType(String value){
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
