package com.bootdo.baseinfo.service;

import com.bootdo.baseinfo.domain.WechatDO;

import java.util.List;
import java.util.Map;

/**
 * 微信号信息
 * 
 * @author zcg
 *
 * @date 2018-12-27 10:51:16
 */
public interface WechatService {
	
	WechatDO get(Integer id);
	
	List<WechatDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(WechatDO wechat);
	
	int update(WechatDO wechat);
	
	int remove(Integer id);
	
	int batchRemove(Integer[] ids);

	int updateForWechatId(WechatDO wechat);
}
