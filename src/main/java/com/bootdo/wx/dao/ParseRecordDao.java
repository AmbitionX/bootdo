package com.bootdo.wx.dao;

import com.bootdo.wx.domain.ParseRecordDO;

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
public interface ParseRecordDao {

	ParseRecordDO get(Long id);
	
	List<ParseRecordDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(ParseRecordDO parseRecord);
	
	int update(ParseRecordDO parseRecord);

	int updateByCode(ParseRecordDO parseRecord);

	int remove(Long id);

	int removeByCode(String parseCode);

	int batchRemove(Long[] ids);
}
