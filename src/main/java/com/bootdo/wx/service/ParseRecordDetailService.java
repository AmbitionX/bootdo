package com.bootdo.wx.service;

import com.bootdo.wx.domain.ParseRecordDetailDO;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-03-11 10:30:23
 */
public interface ParseRecordDetailService {
	
	ParseRecordDetailDO get(Long id);
	
	List<ParseRecordDetailDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(ParseRecordDetailDO parseRecordDetail);

	void callbackRecordDetail(Map<String, Object> map);

	List<Map<String,Object>> listIsAllFinishedServ(Map<String, Object> map);
	
	int update(ParseRecordDetailDO parseRecordDetail);
	
	int remove(Long id);

	int removeByCode(String parseCode);

	int batchRemove(Long[] ids);
}
