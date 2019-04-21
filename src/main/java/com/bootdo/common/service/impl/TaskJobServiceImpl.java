package com.bootdo.common.service.impl;

import com.bootdo.baseinfo.dao.WechatDao;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.common.TreadPool.TreadUtils;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.redis.shiro.RedisManager;
import com.bootdo.common.service.TaskJobService;
import com.bootdo.common.utils.R;
import com.bootdo.system.dao.ConfigDao;
import com.bootdo.system.domain.ConfigDO;
import com.bootdo.wx.dao.TaskdetailDao;
import com.bootdo.wx.dao.TaskinfoDao;
import com.bootdo.wx.domain.TaskdetailDO;
import com.bootdo.wx.domain.TaskinfoDO;
import com.google.common.collect.Maps;
import com.wx.demo.common.RetEnum;
import com.wx.demo.frameWork.protocol.CommonApi;
import com.wx.demo.tools.Constant;
import com.wx.demo.wechatapi.model.ModelReturn;
import com.wx.demo.wechatapi.model.WechatApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TaskJobServiceImpl implements TaskJobService {
    private static final Logger logger = LoggerFactory.getLogger(TaskJobServiceImpl.class);

    @Autowired
    TaskinfoDao taskinfoDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private WechatDao wechatDao;
    @Autowired
    private TaskdetailDao taskdetailDao;

    private CommonApi commonApi = CommonApi.getInstance();



  //  @Transactional
    @Override
    public void run() {
        Date now = new Date();
        logger.info("------------>>> 执行微信任务 <<<------------cc" + now);

        // 获取任务信息 状态是1.未开始 3.未完成 的任务    优先级别： 未完成>优先级>创建时间
        List<TaskinfoDO> taskinfoDOS = taskinfoDao.waitTaskList();
        if (taskinfoDOS.size() > 0) {
            for (int i = 0; i < taskinfoDOS.size(); i++) {
                TaskinfoDO taskinfo = taskinfoDOS.get(i);
                //判断是否有锁
                if(RedisManager.exists(Constant.prefix_task+taskinfo.getId())){
                    continue;
                }
                // 加分布式锁
                RedisManager.set(Constant.prefix_task+taskinfo.getId(),taskinfo.getId().toString());
                try {
                    // 提取任务微信号，判断是否有足够的微信号, 冷却时间、当日上限数量、状态及绑定任务
                    Map<String, Object> configMap = Maps.newHashMap();
                    configMap.put("key", "cdtime");
                    List<ConfigDO> configDos = configDao.list(configMap);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar nowTime = Calendar.getInstance();
                    nowTime.add(Calendar.MINUTE, Integer.parseInt("-"+configDos.get(0).getValue()));

                    Map<String, Object> wxMap = Maps.newHashMap();
                    wxMap.put("lastdate", sdf.format(nowTime.getTime()));

                    // 取配置
                    configMap.put("key", "singlenum");
                    List<ConfigDO> singlenum = configDao.list(configMap);
                    if((taskinfo.getNum() - taskinfo.getFinishnum()) < Integer.parseInt(singlenum.get(0).getValue())) {
                        wxMap.put("limit", taskinfo.getNum() - taskinfo.getFinishnum());
                    }else {
                        wxMap.put("limit", Integer.parseInt(singlenum.get(0).getValue()));
                    }
                    wxMap.put("stauts",1);
                    //排除已经用过的微信号    分关注、阅读两种
                    if(taskinfo.getTasktype()==1) { //阅读
                        Map<String, Object> paramMap = Maps.newHashMap();
                        paramMap.put("url", taskinfo.getUrl().trim());
                        List<TaskinfoDO> sameTaskinfos = taskinfoDao.list(paramMap);
                        Integer[] taskInfoIds = new Integer[sameTaskinfos.size()];
                        for (int j = 0; j < sameTaskinfos.size(); j++) {
                            taskInfoIds[j] = sameTaskinfos.get(j).getId();
                        }
                        wxMap.put("exclude", taskInfoIds);
                        Integer[] stauts = new Integer[1];
                        stauts[0] = 1;
                        wxMap.put("stauts",stauts);
                    }else if(taskinfo.getTasktype()==3){ //关注
                        Map<String, Object> paramMap = Maps.newHashMap();
                        paramMap.put("wxid", taskinfo.getWxid().trim());
                        List<TaskinfoDO> sameTaskinfos = taskinfoDao.list(paramMap);
                        Integer[] taskInfoIds = new Integer[sameTaskinfos.size()];
                        for (int j = 0; j < sameTaskinfos.size(); j++) {
                            taskInfoIds[j] = sameTaskinfos.get(j).getId();
                        }
                        wxMap.put("exclude", taskInfoIds);
                        Integer[] stauts = new Integer[3];
                        stauts[0] = 1;
                        stauts[1] = 4;
                        stauts[2] = 5;
                        wxMap.put("stauts",stauts);
                    }

                    List<WechatDO> wechatListdb = wechatDao.wechatforJob(wxMap);

                    if (wechatListdb.size() > 0) {// 有微信号，开始将微信号绑定到任务上
                        logger.info("------------>>>开始将微信号绑定到任务上<<<------------cc" + now);
                        Map<String, Object> param = Maps.newHashMap();
                        Integer[] ids = new Integer[wechatListdb.size()];
                        for (int j = 0; j < wechatListdb.size(); j++) {
                            ids[j] = wechatListdb.get(j).getId();
                        }
                        param.put("id", ids);
                        param.put("taskid", taskinfo.getId());
                        wechatDao.batchUpdate(param);

                        logger.info("------------->>>完成微信号与任务的绑定<<<---------------cc" + now);
                        // 调用执行任务线程
                        TreadUtils.taskRun(wechatListdb, taskinfo);

                        // 修改任务状态为 2.执行中
                        taskinfo.setStauts(2);
                        taskinfoDao.update(taskinfo);
                        } else {
                            RedisManager.del(Constant.prefix_task+taskinfo.getId());
                            logger.info("----第" + i + "个----->>>任务url{}" + taskinfo.getUrl() + "没有足够的资源进行操作,稍后系统进行重试.cc" + now);
                            continue;
                        }
                    }catch (Exception e){
                        //释放微信号
                        relieveAllForTaskId(taskinfo.getId().toString());
                        // 释放任务锁
                        RedisManager.del(Constant.prefix_task+taskinfo.getId());
                        e.printStackTrace();
                        logger.error("com.bootdo.common.task.TaskJob->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
                }
            }
        }
    }




    public void relieveStatus(WechatDO wechatDO, ModelReturn ret){
        Date now = new Date();
        wechatDO.setTaskid(null);  //解除任务绑定
        if(ret.getCode()==RetEnum.RET_COMM_SUCCESS.getCode()) {
            if(!isToday(wechatDO.getLastdate())){// 如果最后一次执行任务不是当天，释放当日执行任务数量
                wechatDO.setTodaytaskquantity(1); //更新当日累计执行任务数量
            }else {
                wechatDO.setTodaytaskquantity(wechatDO.getTodaytaskquantity() + 1); //更新当日累计执行任务数量
            }
            wechatDO.setLastdate(now);  //更新最后一次执行任务时间
            wechatDO.setTotaltaskquantity(wechatDO.getTotaltaskquantity() + 1); //更新累计执行任务数量

        }else{
            if(ret.getCode()==RetEnum.RET_COMM_2001.getCode()){
                wechatDO.setStauts(4);
                wechatDO.setRemark(RetEnum.RET_COMM_2001.getMessage());
            }else if(ret.getCode()==RetEnum.RET_COMM_2002.getCode()){
                wechatDO.setStauts(5);
                wechatDO.setRemark(RetEnum.RET_COMM_2002.getMessage());
            }
           // wechatDO.setStauts(3);
        }
        wechatDao.relieveStatus(wechatDO);
    }

    public void relieveAllForTaskId(String taskid){
        wechatDao.relieveAllForTaskId(taskid);
    }

    private static boolean isToday(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if(param.equals(now)){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Instant t1 = Instant.now();
        System.out.println("t1 ----》 "+t1);
   /*     try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Instant t2 = Instant.now();
        System.out.println("t2 ----》 "+t2);

        System.out.println("以秒计的时间差：" + Duration.between(t1, t2).getSeconds());


    }
}
