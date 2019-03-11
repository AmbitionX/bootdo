package com.bootdo.wx.service.impl;

import com.bootdo.common.TreadPool.TreadUtils;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.StringUtils;
import com.bootdo.wx.dao.WxuserDao;
import com.bootdo.wx.domain.WxuserDO;
import com.bootdo.wx.service.WxOperationService;
import com.bootdo.wx.service.WxuserService;
import com.mchange.v2.lang.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class WxOperationServiceImpl implements WxOperationService {

	@Override
	public R batch62DataBusi(List<String> wx62Data) {
//		for (String wx : wx62Data) {
//			if (StringUtils.isNotBlank(wx)) {
//				String[] wxdata=wx.split("----");
//				String code = wxdata[0];
//				String pwd = wxdata[1];
//				String data62 = wxdata[2];
//
//			}
//		}

		TreadUtils.login62DataXl(wx62Data);
		return R.ok();
	}
}
