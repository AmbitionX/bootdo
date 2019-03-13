package com.bootdo.wx.service.impl;

import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.baseinfo.service.WechatService;
import com.bootdo.common.enums.EnumParseRecordDetailType;
import com.bootdo.common.enums.EnumParseRecordType;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.wx.domain.ParseRecordDO;
import com.bootdo.wx.service.ParseRecordService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bootdo.wx.dao.ParseRecordDetailDao;
import com.bootdo.wx.domain.ParseRecordDetailDO;
import com.bootdo.wx.service.ParseRecordDetailService;



@Service
@Configuration
public class ParseRecordDetailServiceImpl implements ParseRecordDetailService {
	@Autowired
	private ParseRecordDetailDao parseRecordDetailDao;
	@Autowired
	private ParseRecordService parseRecordService;
	@Autowired
	private WechatService wechatService;
	
	@Override
	public ParseRecordDetailDO get(Long id){
		return parseRecordDetailDao.get(id);
	}
	
	@Override
	public List<ParseRecordDetailDO> list(Map<String, Object> map){
		return parseRecordDetailDao.list(map);
	}
	
	@Override
	public int count(Map<String, Object> map){
		return parseRecordDetailDao.count(map);
	}
	
	@Override
	public int save(ParseRecordDetailDO parseRecordDetail){
		return parseRecordDetailDao.save(parseRecordDetail);
	}

	@Override
	public void callbackRecordDetail(Map<String, Object> map) {
		//拿到需要的参数
		String detailId = map.get("detailId").toString();
		boolean isSucc = (Boolean) map.get("isSuccess");
		Object wxid = map.get("wxid");
		long userId=ShiroUtils.getUserId();

		int status=0;

		//判断成功或者失败
		if (isSucc) {
			//需要判断是否有其他账户拥有此wxid
			Map<String, Object> wechatMap = Maps.newConcurrentMap();
//			wechatMap.put("uid", ShiroUtils.getUserId());
			wechatMap.put("wechat", wxid.toString());
			//查询非当前账户下wxid号小于1天的修改时间，表示冲突
			wechatMap.put("modifydatelessthanoneday", "1");
			List<WechatDO> wechatDOS = wechatService.listByRecently(wechatMap);
			if (wechatDOS.size() > 0) {
				WechatDO wechat = wechatDOS.get(0);
				long userIdWechat = wechat.getUid();
				if (userId == userIdWechat) {
					status = EnumParseRecordDetailType.NUM_TYPE_FIVE.getCode();
				} else {
					status = EnumParseRecordDetailType.NUM_TYPE_FOUR.getCode();
				}
			} else {
				status = EnumParseRecordDetailType.NUM_TYPE_TWO.getCode();
			}
		} else {
			status = EnumParseRecordDetailType.NUM_TYPE_THREE.getCode();
		}

		//根据id查到对应明细
		ParseRecordDetailDO parseRecordDetailDO = parseRecordDetailDao.get(Long.parseLong(detailId));
		if (parseRecordDetailDO != null) {
			String parseCode=parseRecordDetailDO.getParseCode();

			ParseRecordDetailDO updateParseRecordDetail=new ParseRecordDetailDO();
			updateParseRecordDetail.setId(parseRecordDetailDO.getId());
			updateParseRecordDetail.setUtime(new Date());
			updateParseRecordDetail.setState(status);
			//根据明细与状态修改对应明细
			parseRecordDetailDao.update(updateParseRecordDetail);

			//判断主记录是否完成，完成并修改主记录状态
			Map<String, Object> isFinishedMap = Maps.newHashMap();
            isFinishedMap.put("parseCode", parseCode);
			List<Map<String, Object>> isAllFinishedRetLi = listIsAllFinishedServ(isFinishedMap);
			if (isAllFinishedRetLi.size() > 0) {
				Map<String, Object> isAllFinishedMap = isAllFinishedRetLi.get(0);
				String result=isAllFinishedMap.get("ret").toString();
				if ("true".equalsIgnoreCase(result)) {
					ParseRecordDO parseRecordDO=new ParseRecordDO();
					parseRecordDO.setParsecode(parseCode);
					parseRecordDO.setParsestate(EnumParseRecordType.NUM_TYPE_TWO.getCode());
					parseRecordService.updateByCode(parseRecordDO);
				}
			}
		}
	}

	@Override
	public List<Map<String,Object>> listIsAllFinishedServ(Map<String, Object> map) {
		return parseRecordDetailDao.listIsAllFinished(map);
	}

	@Override
	public int update(ParseRecordDetailDO parseRecordDetail){
		return parseRecordDetailDao.update(parseRecordDetail);
	}
	
	@Override
	public int remove(Long id){

		return parseRecordDetailDao.remove(id);
	}
	@Override
	public int removeByCode(String parseCode){
		return parseRecordDetailDao.removeByCode(parseCode);
	}

	@Override
	public int batchRemove(Long[] ids){
		return parseRecordDetailDao.batchRemove(ids);
	}
	
}
