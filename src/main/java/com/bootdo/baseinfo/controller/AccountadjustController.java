package com.bootdo.baseinfo.controller;

import java.util.List;
import java.util.Map;

import com.bootdo.bizservice.AccountadjustBizService;
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

import com.bootdo.baseinfo.domain.AccountadjustDO;
import com.bootdo.baseinfo.service.AccountadjustService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;

/**
 * 账户资金冲减
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-01-15 15:22:42
 */
 
@Controller
@RequestMapping("/baseinfo/accountadjust")
public class AccountadjustController {
	@Autowired
	private AccountadjustService accountadjustService;
	@Autowired
	private AccountadjustBizService accountadjustBizService;
	
	@GetMapping()
	@RequiresPermissions("baseinfo:accountadjust:accountadjust")
	String Accountadjust(){
	    return "baseinfo/accountadjust/accountadjust";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("baseinfo:accountadjust:accountadjust")
	public PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<AccountadjustDO> accountadjustList = accountadjustService.list(query);
		int total = accountadjustService.count(query);
		PageUtils pageUtils = new PageUtils(accountadjustList, total);
		return pageUtils;
	}
	
	@GetMapping("/add")
	@RequiresPermissions("baseinfo:accountadjust:add")
	String add(){
	    return "baseinfo/accountadjust/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("baseinfo:accountadjust:edit")
	String edit(@PathVariable("id") Integer id,Model model){
		AccountadjustDO accountadjust = accountadjustService.get(id);
		model.addAttribute("accountadjust", accountadjust);
	    return "baseinfo/accountadjust/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("baseinfo:accountadjust:add")
	public R save( AccountadjustDO accountadjust){
		return accountadjustBizService.save(accountadjust);
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("baseinfo:accountadjust:edit")
	public R update( AccountadjustDO accountadjust){
		accountadjustService.update(accountadjust);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("baseinfo:accountadjust:remove")
	public R remove( Integer id){
		if(accountadjustService.remove(id)>0){
		return R.ok();
		}
		return R.error();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("baseinfo:accountadjust:batchRemove")
	public R remove(@RequestParam("ids[]") Integer[] ids){
		accountadjustService.batchRemove(ids);
		return R.ok();
	}
	
}
