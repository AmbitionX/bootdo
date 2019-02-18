package com.bootdo.wx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.bootdo.wx.dao.WxuserDao;
import com.bootdo.wx.domain.WxuserDO;
import com.bootdo.wx.service.WxuserService;



@Service
public class WxuserServiceImpl implements WxuserService {
	@Autowired
	private WxuserDao wxuserDao;
	
	@Override
	public WxuserDO get(String account){
		return wxuserDao.get(account);
	}
	
	@Override
	public List<WxuserDO> list(Map<String, Object> map){
		return wxuserDao.list(map);
	}
	
	@Override
	public int count(Map<String, Object> map){
		return wxuserDao.count(map);
	}
	
	@Override
	public int save(WxuserDO wxuser){
		return wxuserDao.save(wxuser);
	}
	
	@Override
	public int update(WxuserDO wxuser){
		return wxuserDao.update(wxuser);
	}
	
	@Override
	public int remove(String account){
		return wxuserDao.remove(account);
	}
	
	@Override
	public int batchRemove(String[] accounts){
		return wxuserDao.batchRemove(accounts);
	}
	
}
