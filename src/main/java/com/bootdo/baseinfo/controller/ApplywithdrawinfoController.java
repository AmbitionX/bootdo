package com.bootdo.baseinfo.controller;

import java.util.List;
import java.util.Map;

import com.bootdo.common.utils.*;
import com.bootdo.util.MessagesCode;
import com.bootdo.util.MsgUtil;
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

import com.bootdo.baseinfo.domain.ApplywithdrawinfoDO;
import com.bootdo.baseinfo.service.ApplywithdrawinfoService;

/**
 * 提现申请
 * 
 * @author zcg
 *
 * @date 2018-12-27 10:51:15
 */
 
@Controller
@RequestMapping("/baseinfo/applywithdrawinfo")
public class ApplywithdrawinfoController {
	@Autowired
	private ApplywithdrawinfoService applywithdrawinfoService;
	
	@GetMapping()
	@RequiresPermissions("baseinfo:applywithdrawinfo:applywithdrawinfo")
	String Applywithdrawinfo(){
	    return "baseinfo/applywithdrawinfo/applywithdrawinfo";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("baseinfo:applywithdrawinfo:applywithdrawinfo")
	public PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
		if(ShiroUtils.getUser().getDeptId()==16) {  // 注册用户加查询条件
			params.put("uid", ShiroUtils.getUserId());
		}
        Query query = new Query(params);
		List<ApplywithdrawinfoDO> applywithdrawinfoList = applywithdrawinfoService.list(query);
		int total = applywithdrawinfoService.count(query);
		PageUtils pageUtils = new PageUtils(applywithdrawinfoList, total);
		return pageUtils;
	}
	
	@GetMapping("/add")
	@RequiresPermissions("baseinfo:applywithdrawinfo:add")
	String add(){
	    return "baseinfo/applywithdrawinfo/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("baseinfo:applywithdrawinfo:edit")
	String edit(@PathVariable("id") Integer id,Model model){
		ApplywithdrawinfoDO applywithdrawinfo = applywithdrawinfoService.get(id);
		model.addAttribute("applywithdrawinfo", applywithdrawinfo);
	    return "baseinfo/applywithdrawinfo/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("baseinfo:applywithdrawinfo:add")
	public R save( ApplywithdrawinfoDO applywithdrawinfo){
		applywithdrawinfo.setUid(ShiroUtils.getUser().getUserId());
		applywithdrawinfo.setUsername(ShiroUtils.getUser().getUsername());
		return applywithdrawinfoService.save(applywithdrawinfo);
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("baseinfo:applywithdrawinfo:edit")
	public R update( ApplywithdrawinfoDO applywithdrawinfo){
		return applywithdrawinfoService.update(applywithdrawinfo);
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("baseinfo:applywithdrawinfo:remove")
	public R remove( Integer id){
		return applywithdrawinfoService.remove(id);
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("baseinfo:applywithdrawinfo:batchRemove")
	public R remove(@RequestParam("ids[]") Integer[] ids){
		applywithdrawinfoService.batchRemove(ids);
		return R.ok();
	}
	
}
