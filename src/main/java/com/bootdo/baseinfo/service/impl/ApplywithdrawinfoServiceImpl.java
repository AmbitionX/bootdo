package com.bootdo.baseinfo.service.impl;

import com.bootdo.baseinfo.dao.AccountDao;
import com.bootdo.baseinfo.dao.AccountdetailDao;
import com.bootdo.baseinfo.domain.AccountDO;
import com.bootdo.baseinfo.domain.AccountdetailDO;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.exception.BDException;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.system.dao.UserDao;
import com.bootdo.util.MessagesCode;
import com.bootdo.util.MsgUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.bootdo.baseinfo.dao.ApplywithdrawinfoDao;
import com.bootdo.baseinfo.domain.ApplywithdrawinfoDO;
import com.bootdo.baseinfo.service.ApplywithdrawinfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


@Service
public class ApplywithdrawinfoServiceImpl implements ApplywithdrawinfoService {
	private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
	@Autowired
	private ApplywithdrawinfoDao applywithdrawinfoDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountdetailDao accountdetailDao;
	
	@Override
	public ApplywithdrawinfoDO get(Integer id){
		return applywithdrawinfoDao.get(id);
	}
	
	@Override
	public List<ApplywithdrawinfoDO> list(Map<String, Object> map){
		return applywithdrawinfoDao.list(map);
	}
	
	@Override
	public int count(Map<String, Object> map){
		return applywithdrawinfoDao.count(map);
	}
	
	@Override
	@Transactional
	public R save(ApplywithdrawinfoDO applywithdrawinfo) {
		//用户申请提现，  申请提现 = 申请提现 + 操作的申请提现金额
		try {
			// 用户账户查询
			AccountDO accountDO = accountDao.getByUid(ShiroUtils.getUserId());
			if(applywithdrawinfo.getApplymoney().compareTo(BigDecimal.ZERO) !=1){
				return R.error(1,MsgUtil.getMsg(MessagesCode.ERROR_CODE_1002));
			}
            if(applywithdrawinfo.getApplymoney().compareTo(accountDO.getUsemoney().subtract(accountDO.getApplywithdrawmoney())) == 1){
				return R.error(1, MsgUtil.getMsg(MessagesCode.ERROR_CODE_1001));
            }
			accountDO.setApplywithdrawmoney(accountDO.getApplywithdrawmoney().add(applywithdrawinfo.getApplymoney()));
		//	accountDO.setUsemoney(accountDO.getUsemoney().subtract(applywithdrawinfo.getApplymoney()));
			accountDao.update(accountDO);
			applywithdrawinfo.setStauts(1);
			applywithdrawinfo.setUid(ShiroUtils.getUserId());
			applywithdrawinfoDao.save(applywithdrawinfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("com.bootdo.baseinfo.service.impl.ApplywithdrawinfoServiceImpl.save->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error();
		}
		return R.ok();
	}
	
	@Override
	@Transactional
	public R update(ApplywithdrawinfoDO applywithdrawinfo){
		try {
			// 查询申请
			ApplywithdrawinfoDO applywithdrawinfodb = applywithdrawinfoDao.get(applywithdrawinfo.getId());
			// 1.未支付的才可以审批
			if(applywithdrawinfodb.getStauts() != 1){
				return R.error(1,MsgUtil.getMsg(MessagesCode.ERROR_CODE_2001));
			}
			ApplywithdrawinfoDO paramDO = new ApplywithdrawinfoDO();
			paramDO.setId(applywithdrawinfo.getId());
            paramDO.setStauts(applywithdrawinfo.getStauts());
            paramDO.setRemark(applywithdrawinfo.getRemark());
			// 用户账户查询
			AccountDO accountDO = accountDao.getByUid(applywithdrawinfodb.getUid());
			BigDecimal changeBefore = accountDO.getUsemoney();
			//查询申请
			//applywithdrawinfodb = applywithdrawinfoDao.get(applywithdrawinfo.getId());
			// 同意并支付
			if(applywithdrawinfo.getStauts().toString().equals("2")){
				accountDO.setApplywithdrawmoney(accountDO.getApplywithdrawmoney().subtract(applywithdrawinfodb.getApplymoney()));
				accountDO.setTotalwithdrawmoney(accountDO.getTotalwithdrawmoney().add(applywithdrawinfodb.getApplymoney()));
				accountDO.setUsemoney(accountDO.getUsemoney().subtract(applywithdrawinfodb.getApplymoney()));
				//账户资金变动记录
				AccountdetailDO accountdetailDO = new AccountdetailDO();
				accountdetailDO.setFrontaccount(changeBefore);
				accountdetailDO.setDealmoney(applywithdrawinfodb.getApplymoney());
				accountdetailDO.setBackaccount(changeBefore.subtract(applywithdrawinfodb.getApplymoney()));
				accountdetailDO.setIsincome(2);//支出
				accountdetailDO.setOperatetype(3);//提现
				accountdetailDO.setAid(accountDO.getId());
				accountdetailDao.save(accountdetailDO);
			}else if(applywithdrawinfo.getStauts().toString().equals("3")){ // 审核拒绝
				accountDO.setApplywithdrawmoney(accountDO.getApplywithdrawmoney().subtract(applywithdrawinfodb.getApplymoney()));
				//accountDO.setUsemoney(accountDO.getUsemoney().add(applywithdrawinfodb.getApplymoney()));
			}else {
				return R.error();
			}
			accountDao.update(accountDO);
			applywithdrawinfoDao.update(paramDO);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("com.bootdo.baseinfo.service.impl.ApplywithdrawinfoServiceImpl.update->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error();
		}
		return R.ok();
	}
	
	@Override
	@Transactional
	public R remove(Integer id){
		try {
			ApplywithdrawinfoDO applywithdrawinfo = applywithdrawinfoDao.get(id);
			// 1.未支付的才可以删除
			if(applywithdrawinfo.getStauts() != 1){
				return R.error(1,MsgUtil.getMsg(MessagesCode.ERROR_CODE_2001));
			}
			// 用户账户查询
			AccountDO accountDO = accountDao.getByUid(applywithdrawinfo.getUid());
			accountDO.setApplywithdrawmoney(accountDO.getApplywithdrawmoney().subtract(applywithdrawinfo.getApplymoney()));
		//	accountDO.setUsemoney(accountDO.getUsemoney().add(applywithdrawinfo.getApplymoney()));
			accountDao.update(accountDO);
			applywithdrawinfoDao.remove(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("com.bootdo.baseinfo.service.impl.ApplywithdrawinfoServiceImpl.remove->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error();
		}
		return R.ok();
	}
	
	@Override
	public int batchRemove(Integer[] ids){
		//return applywithdrawinfoDao.batchRemove(ids);
		return -1;
	}
	
}
