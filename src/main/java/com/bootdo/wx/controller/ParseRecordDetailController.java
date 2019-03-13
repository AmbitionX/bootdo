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

import com.bootdo.wx.domain.ParseRecordDetailDO;
import com.bootdo.wx.service.ParseRecordDetailService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;

/**
 * 
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-03-11 10:30:23
 */
 
@Controller
@RequestMapping("/wx/parseRecordDetail")
public class ParseRecordDetailController {
	@Autowired
	private ParseRecordDetailService parseRecordDetailService;
	
	@GetMapping()
	@RequiresPermissions("wx:parseRecordDetail:parseRecordDetail")
	String ParseRecordDetail(){
	    return "wx/parseRecordDetail/parseRecordDetail";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("wx:parseRecordDetail:parseRecordDetail")
	public PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<ParseRecordDetailDO> parseRecordDetailList = parseRecordDetailService.list(query);
		int total = parseRecordDetailService.count(query);
		PageUtils pageUtils = new PageUtils(parseRecordDetailList, total);
		return pageUtils;
	}
	
	@GetMapping("/add")
	@RequiresPermissions("wx:parseRecordDetail:add")
	String add(){
	    return "wx/parseRecordDetail/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("wx:parseRecordDetail:edit")
	String edit(@PathVariable("id") Long id,Model model){
		ParseRecordDetailDO parseRecordDetail = parseRecordDetailService.get(id);
		model.addAttribute("parseRecordDetail", parseRecordDetail);
	    return "wx/parseRecordDetail/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("wx:parseRecordDetail:add")
	public R save( ParseRecordDetailDO parseRecordDetail){
		if(parseRecordDetailService.save(parseRecordDetail)>0){
			return R.ok();
		}
		return R.error();
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("wx:parseRecordDetail:edit")
	public R update( ParseRecordDetailDO parseRecordDetail){
		parseRecordDetailService.update(parseRecordDetail);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("wx:parseRecordDetail:remove")
	public R remove( Long id){
		if(parseRecordDetailService.remove(id)>0){
		return R.ok();
		}
		return R.error();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("wx:parseRecordDetail:batchRemove")
	public R remove(@RequestParam("ids[]") Long[] ids){
		parseRecordDetailService.batchRemove(ids);
		return R.ok();
	}
	
}
