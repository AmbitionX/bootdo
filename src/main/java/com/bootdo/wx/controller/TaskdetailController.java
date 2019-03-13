package com.bootdo.wx.controller;

import java.util.List;
import java.util.Map;

import com.bootdo.common.utils.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootdo.wx.domain.TaskdetailDO;
import com.bootdo.wx.service.TaskdetailService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;

/**
 * 任务执行明细信息
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2018-12-27 17:36:15
 */
 
@Controller
@RequestMapping("/wx/taskdetail")
public class TaskdetailController {
	@Autowired
	private TaskdetailService taskdetailService;
	
	@GetMapping()
	@RequiresPermissions("wx:taskdetail:taskdetail")
	String Taskdetail(){
	    return "wx/taskdetail/taskdetail";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("wx:taskdetail:taskdetail")
	public PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
		if(ShiroUtils.getUser().getDeptId()==16) {  // 注册用户加查询条件
			params.put("uid", ShiroUtils.getUserId());
		}
        Query query = new Query(params);
		List<TaskdetailDO> taskdetailList = taskdetailService.list(query);
		int total = taskdetailService.count(query);
		PageUtils pageUtils = new PageUtils(taskdetailList, total);
		return pageUtils;
	}
	
	@GetMapping("/add")
	@RequiresPermissions("wx:taskdetail:add")
	String add(){
	    return "wx/taskdetail/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("wx:taskdetail:edit")
	String edit(@PathVariable("id") Integer id,Model model){
		TaskdetailDO taskdetail = taskdetailService.get(id);
		model.addAttribute("taskdetail", taskdetail);
	    return "wx/taskdetail/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("wx:taskdetail:add")
	public R save( TaskdetailDO taskdetail){
		if(taskdetailService.save(taskdetail)>0){
			return R.ok();
		}
		return R.error();
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("wx:taskdetail:edit")
	public R update( TaskdetailDO taskdetail){
		taskdetailService.update(taskdetail);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("wx:taskdetail:remove")
	public R remove( Integer id){
		if(taskdetailService.remove(id)>0){
		return R.ok();
		}
		return R.error();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("wx:taskdetail:batchRemove")
	public R remove(@RequestParam("ids[]") Integer[] ids){
		taskdetailService.batchRemove(ids);
		return R.ok();
	}
	
}
