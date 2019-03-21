package com.bootdo.common.task;

import com.bootdo.baseinfo.dao.WechatDao;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.service.TaskJobService;
import com.bootdo.oa.domain.Response;
import com.bootdo.system.dao.ConfigDao;
import com.bootdo.system.domain.ConfigDO;
import com.bootdo.wx.dao.TaskinfoDao;
import com.bootdo.wx.domain.TaskinfoDO;
import com.google.common.collect.Maps;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务检查
 */
public class TaskJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    TaskJobService taskJobService;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        taskJobService.run();
}
}
