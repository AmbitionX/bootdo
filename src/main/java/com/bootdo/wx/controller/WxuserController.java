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

import com.bootdo.wx.domain.WxuserDO;
import com.bootdo.wx.service.WxuserService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;

/**
 * 
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-02-17 15:10:32
 */
 
@Controller
@RequestMapping("/wx/wxuser")
public class WxuserController {
	@Autowired
	private WxuserService wxuserService;
	
	@GetMapping()
	@RequiresPermissions("wx:wxuser:wxuser")
	String Wxuser(){
	    return "wx/wxuser/wxuser";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("wx:wxuser:wxuser")
	public PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<WxuserDO> wxuserList = wxuserService.list(query);
		int total = wxuserService.count(query);
		PageUtils pageUtils = new PageUtils(wxuserList, total);
		return pageUtils;
	}
	
	@GetMapping("/add")
	@RequiresPermissions("wx:wxuser:add")
	String add(){
	    return "wx/wxuser/add";
	}

	@GetMapping("/edit/{account}")
	@RequiresPermissions("wx:wxuser:edit")
	String edit(@PathVariable("account") String account,Model model){
		WxuserDO wxuser = wxuserService.get(account);
		model.addAttribute("wxuser", wxuser);
	    return "wx/wxuser/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("wx:wxuser:add")
	public R save( WxuserDO wxuser){
		if(wxuserService.save(wxuser)>0){
			return R.ok();
		}
		return R.error();
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("wx:wxuser:edit")
	public R update( WxuserDO wxuser){
		wxuserService.update(wxuser);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("wx:wxuser:remove")
	public R remove( String account){
		if(wxuserService.remove(account)>0){
		return R.ok();
		}
		return R.error();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("wx:wxuser:batchRemove")
	public R remove(@RequestParam("ids[]") String[] accounts){
		wxuserService.batchRemove(accounts);
		return R.ok();
	}
	
}
