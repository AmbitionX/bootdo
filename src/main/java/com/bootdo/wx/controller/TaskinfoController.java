package com.bootdo.wx.controller;

import java.util.List;
import java.util.Map;

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

import com.bootdo.wx.domain.TaskinfoDO;
import com.bootdo.wx.service.TaskinfoService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;

/**
 * 微信任务信息
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2018-12-27 17:36:15
 */
 
@Controller
@RequestMapping("/wx/taskinfo")
public class TaskinfoController {
	@Autowired
	private TaskinfoService taskinfoService;
	
	@GetMapping()
	@RequiresPermissions("wx:taskinfo:taskinfo")
	String Taskinfo(){
	    return "wx/taskinfo/taskinfo";
	}

	@GetMapping("/testPage")
	@RequiresPermissions("wx:taskinfo:testPage")
	String testPage(){
		return "wx/taskinfo/test";
	}

	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("wx:taskinfo:taskinfo")
	public PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<TaskinfoDO> taskinfoList = taskinfoService.list(query);
		int total = taskinfoService.count(query);
		PageUtils pageUtils = new PageUtils(taskinfoList, total);
		return pageUtils;
	}
	
	@GetMapping("/add")
	@RequiresPermissions("wx:taskinfo:add")
	String add(){
	    return "wx/taskinfo/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("wx:taskinfo:edit")
	String edit(@PathVariable("id") Integer id,Model model){
		TaskinfoDO taskinfo = taskinfoService.get(id);
		model.addAttribute("taskinfo", taskinfo);
	    return "wx/taskinfo/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("wx:taskinfo:add")
	public R save( TaskinfoDO taskinfo){
		return taskinfoService.save(taskinfo);
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("wx:taskinfo:edit")
	public R update( TaskinfoDO taskinfo){
		return taskinfoService.update(taskinfo);
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("wx:taskinfo:remove")
	public R remove( Integer id){
		return taskinfoService.remove(id);
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("wx:taskinfo:batchRemove")
	public R remove(@RequestParam("ids[]") Integer[] ids){
		//taskinfoService.batchRemove(ids);
		return R.error();
	}
	
}
