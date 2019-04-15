package com.bootdo.bizservice;

import com.bootdo.baseinfo.domain.AccountadjustDO;
import com.bootdo.common.utils.R;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 账户资金冲减
 * 
 * @author zcg
 * @email 804188877@qq.com
 * @date 2019-01-15 15:22:42
 */
public interface AccountadjustBizService {
	
	AccountadjustDO get(Integer id);
	
	List<AccountadjustDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	R save(AccountadjustDO accountadjust);
	
	int update(AccountadjustDO accountadjust);
	
	int remove(Integer id);
	
	int batchRemove(Integer[] ids);
}
