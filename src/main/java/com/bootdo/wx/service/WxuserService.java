package com.bootdo.wx.service;

import com.bootdo.wx.domain.WxuserDO;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-02-17 15:10:32
 */
public interface WxuserService {
	
	WxuserDO get(String account);
	
	List<WxuserDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(WxuserDO wxuser);
	
	int update(WxuserDO wxuser);
	
	int remove(String account);
	
	int batchRemove(String[] accounts);
}
