package com.bootdo.baseinfo.controller;

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

import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.baseinfo.service.WechatService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;

/**
 * 微信号信息
 * 
 * @author zcg
 *
 * @date 2018-12-27 10:51:16
 */
 
@Controller
@RequestMapping("/baseinfo/wechat")
public class WechatController {
	@Autowired
	private WechatService wechatService;
	
	@GetMapping()
	@RequiresPermissions("baseinfo:wechat:wechat")
	String Wechat(){
	    return "baseinfo/wechat/wechat";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("baseinfo:wechat:wechat")
	public PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
		if(ShiroUtils.getUser().getDeptId()==16) {  // 注册用户加查询条件
			params.put("uid", ShiroUtils.getUserId());
		}
        Query query = new Query(params);
		List<WechatDO> wechatList = wechatService.list(query);
		int total = wechatService.count(query);
		PageUtils pageUtils = new PageUtils(wechatList, total);
		return pageUtils;
	}
	
	@GetMapping("/add")
	@RequiresPermissions("baseinfo:wechat:add")
	String add(){
	    return "baseinfo/wechat/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("baseinfo:wechat:edit")
	String edit(@PathVariable("id") Integer id,Model model){
		WechatDO wechat = wechatService.get(id);
		model.addAttribute("wechat", wechat);
	    return "baseinfo/wechat/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("baseinfo:wechat:add")
	public R save( WechatDO wechat){
		if(wechatService.save(wechat)>0){
			return R.ok();
		}
		return R.error();
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("baseinfo:wechat:edit")
	public R update( WechatDO wechat){
		wechatService.update(wechat);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("baseinfo:wechat:remove")
	public R remove( Integer id){
		if(wechatService.remove(id)>0){
		return R.ok();
		}
		return R.error();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("baseinfo:wechat:batchRemove")
	public R remove(@RequestParam("ids[]") Integer[] ids){
		wechatService.batchRemove(ids);
		return R.ok();
	}

	/**
	 * 将未能进行有效阅读的号改成启用状态
	 */
	@ResponseBody
	@RequestMapping("/updateStatus")
	@RequiresPermissions("baseinfo:wechat:updateStatus")
	public R updateStauts(){
		wechatService.updateStatus();
		return R.ok();
	}
}
