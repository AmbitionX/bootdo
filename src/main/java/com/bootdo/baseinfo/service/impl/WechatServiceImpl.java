package com.bootdo.baseinfo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.bootdo.baseinfo.dao.WechatDao;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.baseinfo.service.WechatService;



@Service
public class WechatServiceImpl implements WechatService {
	@Autowired
	private WechatDao wechatDao;
	
	@Override
	public WechatDO get(Integer id){
		return wechatDao.get(id);
	}
	
	@Override
	public List<WechatDO> list(Map<String, Object> map){
		return wechatDao.list(map);
	}

	@Override
	public List<WechatDO> listByRecently(Map<String, Object> map) {
		return wechatDao.listByRecently(map);
	}

	@Override
	public int count(Map<String, Object> map){
		return wechatDao.count(map);
	}
	
	@Override
	public int save(WechatDO wechat){
		return wechatDao.save(wechat);
	}
	
	@Override
	public int update(WechatDO wechat){
		return wechatDao.update(wechat);
	}
	
	@Override
	public int remove(Integer id){
		return wechatDao.remove(id);
	}
	
	@Override
	public int batchRemove(Integer[] ids){
		return wechatDao.batchRemove(ids);
	}

	@Override
	public int updateForWechatId(WechatDO wechat) {
		return wechatDao.updateForWechatId(wechat);
	}

}
