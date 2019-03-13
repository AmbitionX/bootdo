package com.bootdo.wx.dao;

import com.bootdo.wx.domain.ParseRecordDetailDO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-03-11 10:30:23
 */
@Mapper
public interface ParseRecordDetailDao {

	ParseRecordDetailDO get(Long id);
	
	List<ParseRecordDetailDO> list(Map<String, Object> map);

	List<Map<String,Object>> listIsAllFinished(Map<String,Object> map);
	
	int count(Map<String, Object> map);
	
	int save(ParseRecordDetailDO parseRecordDetail);
	
	int update(ParseRecordDetailDO parseRecordDetail);

	int removeByCode(String parseCode);

	int remove(Long id);
	
	int batchRemove(Long[] ids);
}
