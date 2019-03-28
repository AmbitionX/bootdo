package com.bootdo.common.service.impl;

import com.bootdo.baseinfo.dao.WechatDao;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.redis.shiro.RedisManager;
import com.bootdo.common.service.TaskJobService;
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
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    TaskinfoDao taskinfoDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private WechatDao wechatDao;
    @Autowired
    private TaskdetailDao taskdetailDao;

    private CommonApi commonApi = CommonApi.getInstance();


    private final String prefix_task = "wx_task";

  //  @Transactional
    @Override
    public void run() {
        Date now = new Date();
        logger.info("------------>>> 执行微信任务 <<<------------cc" + now);

        // 获取任务信息 状态是1.未开始 3.未完成 的任务   优先级别： 未完成>优先级>创建时间
        List<TaskinfoDO> taskinfoDOS = taskinfoDao.waitTaskList();
        if (taskinfoDOS.size() > 0) {
            for (int i = 0; i < taskinfoDOS.size(); i++) {
                TaskinfoDO taskinfo = taskinfoDOS.get(i);
                //判断是否有锁
                if(RedisManager.exists(prefix_task+taskinfo.getId())){
                    continue;
                }
                // 加分布式锁
                RedisManager.set(prefix_task+taskinfo.getId(),taskinfo.getId().toString());

                int count = taskinfo.getFinishnum(); //成功次数
                try {
                    // 提取任务微信号，判断是否有足够的微信号, 冷却时间、当日上限数量、状态及绑定任务
                    Map<String, Object> configMap = Maps.newHashMap();
                    configMap.put("key", "cdtime");
                    List<ConfigDO> configDos = configDao.list(configMap);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    Calendar nowTime = Calendar.getInstance();
                    nowTime.add(Calendar.MINUTE, Integer.parseInt("-"+configDos.get(0).getValue()));

                    System.out.println("nowTime----------->>: "+nowTime);

                    configMap.put("key", "todaytaskquantity");
                    configDos = configDao.list(configMap);
                    Map<String, Object> wxMap = Maps.newHashMap();
                    wxMap.put("lastdate", sdf.format(nowTime.getTime()));
                    wxMap.put("todaytaskquantity", Integer.parseInt(configDos.get(0).getValue()));
                    wxMap.put("limit", taskinfo.getNum() - taskinfo.getFinishnum());
                    wxMap.put("stauts",1);
                    //排除已经用过的微信号
                    if (taskinfo.getFinishnum() > 0) {
                        wxMap.put("exclude", taskinfo.getId());
                    }

                    List<WechatDO> wechatListdb = wechatDao.wechatforJob(wxMap);

                    if (wechatListdb.size() == (taskinfo.getNum() - taskinfo.getFinishnum())) {// 有足够的微信号，开始将微信号绑定到任务上
                        logger.info("------------>>>开始将微信号绑定到任务上<<<------------cc" + now);
                        Map<String, Object> param = Maps.newHashMap();
                        Integer[] ids = new Integer[wechatListdb.size()];
                        for (int j = 0; j < wechatListdb.size(); j++) {
                            ids[j] = wechatListdb.get(j).getId();
                            /*WechatDO wechatDO = wechatListdb.get(j);
                            wechatDO.setTaskid(taskinfo.getId());
                            wechatDao.update(wechatDO);*/
                        }
                        param.put("id", ids);
                        param.put("taskid", taskinfo.getId());
                        wechatDao.batchUpdate(param);

                        logger.info("------------->>>完成微信号与任务的绑定<<<---------------cc" + now);
                        // 绑定完任务，开始做任务 ----------------------------功能待开发-------------------------------
                        WechatApi wechatApi = new WechatApi();
                        if (taskinfo.getTasktype().equals(1)) {//阅读
                            for (WechatDO wxid : wechatListdb) {
                                wechatApi.setRandomId(wxid.getRandomid());
                                wechatApi.setAccount(wxid.getUid().toString());
                                wechatApi.setSoftwareId(Constant.softwareId);
                                wechatApi.setAutoLogin(Constant.autoLogin);
                                wechatApi.setProtocolVer(Constant.protocolVer);
                                wechatApi.setReqUrl(taskinfo.getUrl());
                                wechatApi.setScene(Constant.scene);
                                wechatApi.setUserName(taskinfo.getWxname());
                                wechatApi.setCmd(777);

                                ModelReturn modelReturn = commonApi.execute(wechatApi);
                                int flag = 1;
                                if(modelReturn.getCode()!= RetEnum.RET_COMM_SUCCESS.getCode()) {
                                    flag = 2;
                                }
                                TaskdetailDO taskdetailDO = new TaskdetailDO();
                                taskdetailDO.setTaskid(taskinfo.getId());
                                taskdetailDO.setUid(wxid.getUid());
                                taskdetailDO.setWxid(wxid.getId());
                                taskdetailDO.setPrice(taskinfo.getPrice());
                                taskdetailDO.setTasktype(taskinfo.getTasktype());
                                taskdetailDO.setStauts(flag); //根据任务执行情况设定
                                taskdetailDO.setParentid(wxid.getParentid());
                                taskdetailDao.save(taskdetailDO);

                                //释放微信号，根据执行成功失败传参
                                relieveStatus(wxid, flag==1);
                                if (modelReturn.getCode()== RetEnum.RET_COMM_SUCCESS.getCode()) {//记录成功次数
                                    count = count + 1;
                                }
                                Thread.sleep(taskinfo.getTaskperiod());
                            }
                        } else if (taskinfo.getTasktype().equals(2)) {//点赞
                            for (WechatDO wxid : wechatListdb) {
                              /*  BaseService service = ServiceManager.getInstance().getServiceByRandomId(wxid.getRandomid());
                                String paymentStr = service.getA8KeyService(taskinfo.getUrl(), 7, taskinfo.getWxname());
*/

                                TaskdetailDO taskdetailDO = new TaskdetailDO();
                                taskdetailDO.setTaskid(taskinfo.getId());
                                taskdetailDO.setUid(wxid.getUid());
                                taskdetailDO.setWxid(wxid.getId());
                                taskdetailDO.setPrice(taskinfo.getPrice());
                                taskdetailDO.setTasktype(taskinfo.getTasktype());
                                taskdetailDO.setStauts(1); //根据任务执行情况设定
                                taskdetailDO.setParentid(wxid.getParentid());
                                taskdetailDao.save(taskdetailDO);
                                //释放微信号，根据执行成功失败传参
                                relieveStatus(wxid, true);
                                if (true) {//记录成功次数
                                    count = count + 1;
                                }
                                Thread.sleep(taskinfo.getTaskperiod());
                            }
                        } else if (taskinfo.getTasktype().equals(3)) {//关注
                            for (WechatDO wxid : wechatListdb) {
                             /*   BaseService service = ServiceManager.getInstance().getServiceByRandomId(wxid.getRandomid());
                                String paymentStr = service.getA8KeyService(taskinfo.getUrl(), 7, taskinfo.getWxname());
*/
                                wechatApi.setRandomId(wxid.getRandomid());
                                wechatApi.setAccount(wxid.getUid().toString());
                                wechatApi.setSoftwareId(Constant.softwareId);
                                wechatApi.setAutoLogin(Constant.autoLogin);
                                wechatApi.setProtocolVer(Constant.protocolVer);
                                wechatApi.setReqUrl(taskinfo.getUrl());
                                wechatApi.setScene(Constant.scene);
                                wechatApi.setUserName(taskinfo.getWxname());
                                wechatApi.setCmd(999);

                                ModelReturn modelReturn = commonApi.execute(wechatApi);

                                TaskdetailDO taskdetailDO = new TaskdetailDO();
                                taskdetailDO.setTaskid(taskinfo.getId());
                                taskdetailDO.setUid(wxid.getUid());
                                taskdetailDO.setWxid(wxid.getId());
                                taskdetailDO.setPrice(taskinfo.getPrice());
                                taskdetailDO.setTasktype(taskinfo.getTasktype());
                                taskdetailDO.setStauts(1); //根据任务执行情况设定
                                taskdetailDO.setParentid(wxid.getParentid());
                                taskdetailDao.save(taskdetailDO);
                                //释放微信号，根据执行成功失败传参
                                relieveStatus(wxid, true);

                                if (modelReturn!=null && modelReturn.getCode()==0) {//记录成功次数
                                    count = count + 1;
                                }
                                Thread.sleep(taskinfo.getTaskperiod());
                            }
                        }
                        //----------------------------------------------------任务结束----------------------------------
                        //------------------------------ 任务结束，执行任务数量累计、任务状态 -----------------------------
                        taskinfo.setFinishnum(count);
                        if (taskinfo.getNum() <= count) {
                            taskinfo.setStauts(5); //已完成
                        } else {
                            taskinfo.setStauts(3); // 未完成
                        }
                        taskinfoDao.update(taskinfo);
                    } else {
                        logger.info("--------->>>任务url{}" + taskinfo.getUrl() + "没有足够的资源进行操作,稍后系统进行重试.cc" + now);
                        break;
                    }
                } catch (Exception e) {
                    //更新任务
                    if(count>taskinfo.getFinishnum()){ // 有执行任务
                        taskinfo.setFinishnum(count);
                        if(taskinfo.getNum()<=count){ // 已经完成任务
                            taskinfo.setStauts(5);
                        }
                        taskinfoDao.update(taskinfo);
                    }
                    //释放微信号
                    relieveAllForTaskId(taskinfo.getId().toString());
                    e.printStackTrace();
                    logger.error("com.bootdo.common.task.TaskJob->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
                    //   TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }finally {
                    // 释放任务锁
                    RedisManager.del(prefix_task+taskinfo.getId());
                }
            }
        }
    }




    public void relieveStatus(WechatDO wechatDO,boolean flag){
        Date now = new Date();
        wechatDO.setTaskid(null);  //解除任务绑定
        if(flag) {
            if(isToday(wechatDO.getLastdate())){// 如果最后一次执行任务不是当天，释放当日执行任务数量
                wechatDO.setTodaytaskquantity(1); //更新当日累计执行任务数量
            }else {
                wechatDO.setTodaytaskquantity(wechatDO.getTodaytaskquantity() + 1); //更新当日累计执行任务数量
            }
            wechatDO.setLastdate(now);  //更新最后一次执行任务时间
            wechatDO.setTotaltaskquantity(wechatDO.getTotaltaskquantity() + 1); //更新累计执行任务数量

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
