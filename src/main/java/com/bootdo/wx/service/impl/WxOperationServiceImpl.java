package com.bootdo.wx.service.impl;

import com.bootdo.common.TreadPool.TreadUtils;
import com.bootdo.common.enums.EnumParseRecordType;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.common.utils.StringUtils;
import com.bootdo.wx.dao.WxuserDao;
import com.bootdo.wx.domain.ParseRecordDO;
import com.bootdo.wx.domain.WxuserDO;
import com.bootdo.wx.service.ParseRecordService;
import com.bootdo.wx.service.WxOperationService;
import com.bootdo.wx.service.WxuserService;
import com.mchange.v2.lang.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class WxOperationServiceImpl implements WxOperationService {

	@Autowired
	private ParseRecordService parseRecordService;

}
