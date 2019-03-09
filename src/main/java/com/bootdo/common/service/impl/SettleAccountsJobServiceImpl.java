package com.bootdo.common.service.impl;

import com.bootdo.baseinfo.dao.AccountDao;
import com.bootdo.baseinfo.dao.AccountdetailDao;
import com.bootdo.baseinfo.domain.AccountDO;
import com.bootdo.baseinfo.domain.AccountdetailDO;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.dao.TaskDao;
import com.bootdo.common.service.SettleAccountsJobService;
import com.bootdo.system.dao.ConfigDao;
import com.bootdo.system.domain.ConfigDO;
import com.bootdo.wx.dao.TaskdetailDao;
import com.bootdo.wx.dao.TaskinfoDao;
import com.bootdo.wx.domain.SettleAccountModel;
import com.bootdo.wx.domain.TaskinfoDO;
import com.google.common.collect.Maps;
import org.activiti.engine.task.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SettleAccountsJobServiceImpl implements SettleAccountsJobService {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    TaskdetailDao taskdetailDao;
    @Autowired
    SettleAccountsJobService settleAccountsJobService;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountdetailDao accountdetailDao;
    @Autowired
    private TaskinfoDao taskinfoDao;

    @Transactional
    @Override
    public int run(TaskinfoDO taskinfoDO) {
        Date now = new Date();
        logger.info("------------>>> 执行结算任务 <<<------------cc" + now);
        int num = 0;
        try {
            // 获取结算规则
            Map<String, Object> configMap = Maps.newHashMap();
            configMap.put("key", "topratio");
            BigDecimal topratio = new BigDecimal(configDao.list(configMap).get(0).getValue());
            configMap.put("key", "secondratio");
            BigDecimal secondratio = new BigDecimal(configDao.list(configMap).get(0).getValue());
            // 查询结算明细
            List<SettleAccountModel> settleAccountModels = taskdetailDao.querySettleAccountDetail(taskinfoDO.getId());
            if (settleAccountModels.size() > 0) {
                for (int i = 0; i < settleAccountModels.size(); i++) {
                    BigDecimal frontAccount = new BigDecimal("0.00");
                    AccountDO accountDO = null;
                    int j = 0;
                    BigDecimal price = new BigDecimal(settleAccountModels.get(i).getPrice());

                    BigDecimal secondmoney = new BigDecimal(0);
                    try {
                        secondmoney = price.multiply(secondratio).setScale(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 二级用户结算
                    while (j < 3) {
                        accountDO = accountDao.getByUid(settleAccountModels.get(i).getUid());
                        frontAccount = accountDO.getUsemoney();
                        accountDO.setUsemoney(accountDO.getUsemoney().add(secondmoney));
                        accountDO.setTotalgainmoney(accountDO.getTotalgainmoney().add(secondmoney));
                        num = accountDao.update(accountDO);
                        if (num > 0) { break; }
                        j++;
                    }
                    if (num > 0) {
                        // 账户资金变动记录
                        AccountdetailDO accountdetailDO = new AccountdetailDO();
                        accountdetailDO.setAid(settleAccountModels.get(i).getUaccountid());
                        accountdetailDO.setOperatetype(1);
                        accountdetailDO.setIsincome(1);
                        accountdetailDO.setFrontaccount(frontAccount);
                        accountdetailDO.setBackaccount(accountDO.getUsemoney());
                        accountdetailDO.setDealmoney(secondmoney);
                        num = accountdetailDao.save(accountdetailDO);
                    } else {  return 0; }
                    // 一级用户结算
                    if (settleAccountModels.get(i).getParentid() != null && !"".equals(settleAccountModels.get(i).getParentid())) {
                        AccountDO accountDO1 = null;
                        BigDecimal frontAccount1 = new BigDecimal("0.00");
                        int m = 0;
                        BigDecimal topmoney = new BigDecimal(0);
                        try {
                            topmoney = price.multiply(topratio).setScale(2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        while (m < 3) {
                            accountDO1 = accountDao.getByUid(settleAccountModels.get(i).getParentid());
                            frontAccount1 = accountDO1.getUsemoney();
                            accountDO1.setUsemoney(accountDO1.getUsemoney().add(topmoney));
                            accountDO1.setTotalgainmoney(accountDO1.getTotalgainmoney().add(topmoney));
                            num = accountDao.update(accountDO1);
                            if (num > 0) {  break; }
                            m++;
                        }
                        if (num > 0) {
                            // 账户资金变动记录
                            AccountdetailDO accountdetailDO = new AccountdetailDO();
                            accountdetailDO.setAid(settleAccountModels.get(i).getUaccountid());
                            accountdetailDO.setOperatetype(1);
                            accountdetailDO.setIsincome(1);
                            accountdetailDO.setFrontaccount(frontAccount);
                            accountdetailDO.setBackaccount(accountDO.getUsemoney());
                            accountdetailDO.setDealmoney(secondmoney);
                            num = accountdetailDao.save(accountdetailDO);
                        } else { throw new Exception("一级用户结算失败"); }
                    }
                }
                if(num>0){
                    taskinfoDO.setStauts(7);
                    taskinfoDao.update(taskinfoDO);
                }
            }
        } catch (Exception e) {
            num = 0;
            e.printStackTrace();
            logger.error("com.bootdo.common.service.impl.SettleAccountsJobServiceImpl->run!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return num;
    }
}
