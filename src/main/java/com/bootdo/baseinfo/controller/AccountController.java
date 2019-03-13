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

import com.bootdo.baseinfo.domain.AccountDO;
import com.bootdo.baseinfo.service.AccountService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;

/**
 * 用户账户信息
 * 
 * @author zcg
 *
 * @date 2018-12-27 10:51:15
 */
 
@Controller
@RequestMapping("/baseinfo/account")
public class AccountController {
	@Autowired
	private AccountService accountService;
	
	@GetMapping()
	@RequiresPermissions("baseinfo:account:account")
	String Account(){
	    return "baseinfo/account/account";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("baseinfo:account:account")
	public PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
		if(ShiroUtils.getUser().getDeptId()==16) {  // 注册用户加查询条件
			params.put("uid", ShiroUtils.getUserId());
		}
        Query query = new Query(params);
		List<AccountDO> accountList = accountService.list(query);
		int total = accountService.count(query);
		PageUtils pageUtils = new PageUtils(accountList, total);
		return pageUtils;
	}
	
	@GetMapping("/add")
	@RequiresPermissions("baseinfo:account:add")
	String add(){
	    return "baseinfo/account/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("baseinfo:account:edit")
	String edit(@PathVariable("id") Integer id,Model model){
		AccountDO account = accountService.get(id);
		model.addAttribute("account", account);
	    return "baseinfo/account/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("baseinfo:account:add")
	public R save( AccountDO account){
		if(accountService.save(account)>0){
			return R.ok();
		}
		return R.error();
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("baseinfo:account:edit")
	public R update( AccountDO account){
		accountService.update(account);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("baseinfo:account:remove")
	public R remove( Integer id){
		if(accountService.remove(id)>0){
		return R.ok();
		}
		return R.error();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("baseinfo:account:batchRemove")
	public R remove(@RequestParam("ids[]") Integer[] ids){
		accountService.batchRemove(ids);
		return R.ok();
	}
	
}
