package com.bootdo.wx.service;

import com.bootdo.common.utils.R;
import com.bootdo.wx.domain.TaskinfoDO;

import java.util.List;
import java.util.Map;

/**
 * 微信任务信息
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2018-12-27 17:36:15
 */
public interface TaskinfoService {
	
	TaskinfoDO get(Integer id);
	
	List<TaskinfoDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	R save(TaskinfoDO taskinfo);
	
	R update(TaskinfoDO taskinfo);
	
	R remove(Integer id);
	
	int batchRemove(Integer[] ids);

	R batchOverTask(Integer id);
}
