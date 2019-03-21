package com.bootdo.common.task;

import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.service.SettleAccountsJobService;
import com.bootdo.system.dao.ConfigDao;
import com.bootdo.wx.dao.TaskinfoDao;
import com.bootdo.wx.domain.TaskinfoDO;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

public class SettleAccountsJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    TaskinfoDao taskinfoDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private SettleAccountsJobService settleAccountsJobService;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        // 获取待结算任务列表
        List<TaskinfoDO> taskinfoDOS = taskinfoDao.waitSettleTaskList();
        if(taskinfoDOS.size()>0){
            for(int i=0;i<taskinfoDOS.size();i++){
                TaskinfoDO taskinfoDO = taskinfoDOS.get(i);
                int num = settleAccountsJobService.run(taskinfoDO);

                if(num<1){// 结算失败，修改任务结算次数
                    taskinfoDO.setSettlenum(taskinfoDO.getSettlenum()+1);
                    taskinfoDao.update(taskinfoDO);
                }
            }
        }else{
            return;
        }

    }
}
