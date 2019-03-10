package com.bootdo.baseinfo.dao;

import com.bootdo.baseinfo.domain.WechatDO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 微信号信息
 * @author zcg
 *
 * @date 2018-12-27 10:51:16
 */
@Mapper
public interface WechatDao {

	WechatDO get(Integer id);
	
	List<WechatDO> list(Map<String,Object> map);
	
	int count(Map<String,Object> map);
	
	int save(WechatDO wechat);
	
	int update(WechatDO wechat);
	
	int remove(Integer id);
	
	int batchRemove(Integer[] ids);

	List<WechatDO> wechatforJob(Map<String,Object> map);

	int batchUpdate(Map<String,Object> map);

	int relieveAllForTaskId(String taskid);

	int updateForWechatId(WechatDO wechat);

	int relieveStatus(WechatDO wechat);

}
