package com.bootdo.common.service.impl;

import com.bootdo.baseinfo.dao.WechatDao;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.service.TaskJobService;
import com.bootdo.system.dao.ConfigDao;
import com.bootdo.system.domain.ConfigDO;
import com.bootdo.wx.dao.TaskdetailDao;
import com.bootdo.wx.dao.TaskinfoDao;
import com.bootdo.wx.domain.TaskdetailDO;
import com.bootdo.wx.domain.TaskinfoDO;
import com.google.common.collect.Maps;
import com.wx.service.BaseService;
import com.wx.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TaskJobServiceImpl implements TaskJobService {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    TaskinfoDao taskinfoDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private WechatDao wechatDao;
    @Autowired
    private TaskdetailDao taskdetailDao;

    @Transactional
    @Override
    public void run() {
        try {
            // 获取任务信息
            List<TaskinfoDO> taskinfoDOS = taskinfoDao.waitTaskList();
            if (taskinfoDOS.size() > 0) {
                for (int i = 0; i < taskinfoDOS.size(); i++) {
                    TaskinfoDO taskinfo = taskinfoDOS.get(i);
                    // 提取任务微信号，判断是否有足够的微信号, 冷却时间、当日上限数量、状态及绑定任务
                    Map<String, Object> configMap = Maps.newHashMap();
                    configMap.put("key", "cdtime");
                    List<ConfigDO> configDos = configDao.list(configMap);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now = new Date();
                    Calendar nowTime = Calendar.getInstance();
                    nowTime.add(Calendar.MINUTE, Integer.parseInt(configDos.get(0).getValue()));

                    configMap.put("key", "todaytaskquantity");
                    configDos = configDao.list(configMap);
                    Map<String, Object> wxMap = Maps.newHashMap();
                    wxMap.put("lastdate", sdf.format(nowTime.getTime()));
                    wxMap.put("todaytaskquantity", Integer.parseInt(configDos.get(0).getValue()));
                    wxMap.put("limit", taskinfo.getNum()-taskinfo.getFinishnum());
                    List<WechatDO> wechatListdb = wechatDao.list(wxMap);

                    if(wechatListdb.size()==(taskinfo.getNum()-taskinfo.getFinishnum())) {// 有足够的微信号，开始将微信号绑定到任务上
                        logger.info("------------>>>开始将微信号绑定到任务上<<<------------");
                        for(int j=0;j<wechatListdb.size();j++){
                            WechatDO wechatDO = wechatListdb.get(j);
                            wechatDO.setTaskid(taskinfo.getId());
                            wechatDao.update(wechatDO);
                        }
                        logger.info("------------->>>完成微信号与任务的绑定<<<---------------");
                        // 绑定完任务，开始做任务 ----------------------------功能待开发-------------------------------
                        if(taskinfo.getTasktype().equals(1)){//阅读
                            for(WechatDO wxid: wechatListdb) {
                                int count = 0; //成功次数
                                BaseService service = ServiceManager.getInstance().getServiceByRandomId(wxid.getRandomid());
                                String paymentStr = service.getA8KeyService(taskinfo.getUrl(),7,taskinfo.getWxname());

                                TaskdetailDO taskdetailDO = new TaskdetailDO();
                                taskdetailDO.setTaskid(taskinfo.getId());
                                taskdetailDO.setUid(wxid.getUid());
                                taskdetailDO.setWxid(wxid.getId());
                                taskdetailDO.setPrice(taskinfo.getPrice());
                                taskdetailDO.setTasktype(taskinfo.getTasktype());
                                taskdetailDO.setStauts(1); //根据任务执行情况设定
                                taskdetailDO.setParentid(1L); //想想怎么获取到
                                taskdetailDao.save(taskdetailDO);

                                if(true){//记录成功次数
                                    count=count+1;
                                }
                            }
                        }else if(taskinfo.getTasktype().equals(2)){//点赞
                            for(WechatDO wxid: wechatListdb) {
                                int count = 0; //成功次数
                                BaseService service = ServiceManager.getInstance().getServiceByRandomId(wxid.getRandomid());
                                String paymentStr = service.getA8KeyService(taskinfo.getUrl(),7,taskinfo.getWxname());

                                TaskdetailDO taskdetailDO = new TaskdetailDO();
                                taskdetailDO.setTaskid(taskinfo.getId());
                                taskdetailDO.setUid(wxid.getUid());
                                taskdetailDO.setWxid(wxid.getId());
                                taskdetailDO.setPrice(taskinfo.getPrice());
                                taskdetailDO.setTasktype(taskinfo.getTasktype());
                                taskdetailDO.setStauts(1); //根据任务执行情况设定
                                taskdetailDO.setParentid(1L); //想想怎么获取到
                                taskdetailDao.save(taskdetailDO);

                                if(true){//记录成功次数
                                    count=count+1;
                                }
                            }
                        }else if(taskinfo.getTasktype().equals(3)){//关注
                            for(WechatDO wxid: wechatListdb) {
                                int count = 0; //成功次数
                                BaseService service = ServiceManager.getInstance().getServiceByRandomId(wxid.getRandomid());
                                String paymentStr = service.getA8KeyService(taskinfo.getUrl(),7,taskinfo.getWxname());

                                TaskdetailDO taskdetailDO = new TaskdetailDO();
                                taskdetailDO.setTaskid(taskinfo.getId());
                                taskdetailDO.setUid(wxid.getUid());
                                taskdetailDO.setWxid(wxid.getId());
                                taskdetailDO.setPrice(taskinfo.getPrice());
                                taskdetailDO.setTasktype(taskinfo.getTasktype());
                                taskdetailDO.setStauts(1); //根据任务执行情况设定
                                taskdetailDO.setParentid(1L); //想想怎么获取到
                                taskdetailDao.save(taskdetailDO);

                                if(true){//记录成功次数
                                    count=count+1;
                                }
                            }
                        }
                        //----------------------------------------------------任务结束---------------------------------


                        //------------ 任务结束，微信号开始计时冷却、执行任务数量累计、解除绑定 ，任务状态 ------------------
                        taskinfo.setStauts(5); //已完成
                       /* Date now = new Date();
                        for(int i=0;i<=wechatListdb.size();i++){
                            WechatDO wechatDO = wechatListdb.get(i);
                            wechatDO.setTaskid(null);  //解除任务绑定
                            wechatDO.setLastdate(now);  //更新最后一次执行任务时间
                            wechatDO.setTotaltaskquantity(wechatDO.getTotaltaskquantity()+1); //更新累计执行任务数量
                            wechatDO.setTodaytaskquantity(wechatDO.getTodaytaskquantity()+1); //更新当日累计执行任务数量
                            wechatDao.update(wechatDO);
                        }*/

                    } else {
                        break;
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("com.bootdo.common.task.TaskJob->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
