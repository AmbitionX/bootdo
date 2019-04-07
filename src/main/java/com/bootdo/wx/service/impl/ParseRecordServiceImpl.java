package com.bootdo.wx.service.impl;

import com.bootdo.common.TreadPool.TreadUtils;
import com.bootdo.common.enums.EnumParseRecordType;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bootdo.wx.dao.ParseRecordDao;
import com.bootdo.wx.domain.ParseRecordDO;
import com.bootdo.wx.service.ParseRecordService;



@Service
public class ParseRecordServiceImpl implements ParseRecordService {
	@Autowired
	private ParseRecordDao parseRecordDao;
	
	@Override
	public ParseRecordDO get(Long id){
		return parseRecordDao.get(id);
	}
	
	@Override
	public List<ParseRecordDO> list(Map<String, Object> map){
		return parseRecordDao.list(map);
	}
	
	@Override
	public int count(Map<String, Object> map){
		return parseRecordDao.count(map);
	}
	
	@Override
	public int save(ParseRecordDO parseRecord){
		return parseRecordDao.save(parseRecord);
	}
	
	@Override
	public int update(ParseRecordDO parseRecord){
		return parseRecordDao.update(parseRecord);
	}
	public int updateByCode(ParseRecordDO parseRecord){
		return parseRecordDao.updateByCode(parseRecord);
	}

	@Override
	public int remove(Long id){
		return parseRecordDao.remove(id);
	}

	@Override
	public int removeByCode(String parseCode){
		return parseRecordDao.removeByCode(parseCode);
	}

	@Override
	public int batchRemove(Long[] ids){
		return parseRecordDao.batchRemove(ids);
	}

	@Override
	public R batch62DataBusi(List<String> wx62Data, String url,String account) {
//		for (String wx : wx62Data) {
//			if (StringUtils.isNotBlank(wx)) {
//				String[] wxdata=wx.split("----");
//				String code = wxdata[0];
//				String pwd = wxdata[1];
//				String data62 = wxdata[2];
//
//			}
//		}

		String parseCode = UUID.randomUUID().toString();
		ParseRecordDO parseRecordDO=new ParseRecordDO();
		parseRecordDO.setParsecode(parseCode);
		parseRecordDO.setUserid(ShiroUtils.getUserId());
		parseRecordDO.setFileurl(url);
		parseRecordDO.setParsedate(new Date());
		parseRecordDO.setParsestate(EnumParseRecordType.NUM_TYPE_ONE.getCode());
		save(parseRecordDO);
		TreadUtils.login62DataXl(wx62Data,parseCode,account);
		return R.ok();
	}
	
}
