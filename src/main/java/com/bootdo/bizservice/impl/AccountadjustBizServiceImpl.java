package com.bootdo.bizservice.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bootdo.baseinfo.domain.AccountDO;
import com.bootdo.baseinfo.domain.AccountadjustDO;
import com.bootdo.baseinfo.domain.AccountdetailDO;
import com.bootdo.baseinfo.service.AccountService;
import com.bootdo.baseinfo.service.AccountadjustService;
import com.bootdo.baseinfo.service.AccountdetailService;
import com.bootdo.bizservice.AccountadjustBizService;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.utils.R;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.service.UserService;
import com.google.common.collect.Maps;
import com.wx.demo.common.RetEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Service
public class AccountadjustBizServiceImpl implements AccountadjustBizService {
    private static final Logger logger = LoggerFactory.getLogger(AccountadjustBizServiceImpl.class);


    @Autowired
    private AccountadjustService accountadjustService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountdetailService accountdetailService;

    @Transactional
    @Override
    public R save(AccountadjustDO accountadjust) {
        try {
            if(accountadjust.getIsincome()!=1){
                accountadjust.setDealmoney(accountadjust.getDealmoney().negate());
            }
            logger.info("------资金冲减-----参数{}", JSONObject.toJSONString(accountadjust));
            // 根据账号找到用户账户
            Map<String,Object> param = Maps.newHashMap();
            param.put("searchName",accountadjust.getUsername());
            List<AccountDO> accounts = accountService.list(param);
            // 查用户信息
            param.put("username",accountadjust.getUsername());
            param.put("deptId","");
            List<UserDO> users = userService.list(param);

            if(accounts.size()<1 || users.size()<1){// 如果没有找到，提示账号输入错误
                return R.error(5001, RetEnum.EET_COMM_5001.getMessage());
            }else if(accounts.size()>1 || users.size()>1){// 找到多个
                return R.error(1001,RetEnum.RET_COMM_1001.getMessage());
            }else{// 如果找到账户，添加调账记录， 写资金记录表, 修改账户金额
                // 资金冲减记录
                AccountDO accountDO = accounts.get(0);

                accountadjust.setAid(accountDO.getId());
                accountadjustService.save(accountadjust);
                // 修改账户金额
                BigDecimal frontAccount = new BigDecimal("0.00");
                frontAccount = accountDO.getUsemoney();
                accountDO.setUsemoney(accountDO.getUsemoney().add(accountadjust.getDealmoney()));
                accountDO.setTotalgainmoney(accountDO.getTotalgainmoney().add(accountadjust.getDealmoney()));
                int num = accountService.update(accountDO);
                // 插入资金变动记录
                if (num > 0) {
                    // 账户资金变动记录
                    AccountdetailDO accountdetailDO = new AccountdetailDO();
                    accountdetailDO.setAid(accountDO.getId());
                    accountdetailDO.setOperatetype(4);
                    accountdetailDO.setIsincome(accountadjust.getIsincome());
                    accountdetailDO.setFrontaccount(frontAccount);
                    accountdetailDO.setBackaccount(accountDO.getUsemoney());
                    accountdetailDO.setDealmoney(accountadjust.getDealmoney());
                    accountdetailService.save(accountdetailDO);
                }
            }
        }catch (Exception e){
            logger.error("-------save->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error();
        }
        return R.ok();
    }

    @Override
    public AccountadjustDO get(Integer id) {
        return null;
    }

    @Override
    public List<AccountadjustDO> list(Map<String, Object> map) {
        return null;
    }

    @Override
    public int count(Map<String, Object> map) {
        return 0;
    }

    @Override
    public int update(AccountadjustDO accountadjust) {
        return 0;
    }

    @Override
    public int remove(Integer id) {
        return 0;
    }

    @Override
    public int batchRemove(Integer[] ids) {
        return 0;
    }
}
