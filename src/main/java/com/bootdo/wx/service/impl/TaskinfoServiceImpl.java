package com.bootdo.wx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.baseinfo.dao.WechatDao;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.common.utils.R;
import com.bootdo.system.dao.ConfigDao;
import com.bootdo.system.domain.ConfigDO;
import com.bootdo.util.MessagesCode;
import com.bootdo.util.MsgUtil;
import com.bootdo.wx.dao.TaskdetailDao;
import com.bootdo.wx.domain.TaskdetailDO;
import com.google.common.collect.Maps;
import com.wx.service.BaseService;
import com.wx.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bootdo.wx.dao.TaskinfoDao;
import com.bootdo.wx.domain.TaskinfoDO;
import com.bootdo.wx.service.TaskinfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


@Service
public class TaskinfoServiceImpl implements TaskinfoService {
	private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
	@Autowired
	private TaskinfoDao taskinfoDao;
	@Autowired
	private WechatDao wechatDao;
	@Autowired
	private ConfigDao configDao;
	@Autowired
    private TaskdetailDao taskdetailDao;
	
	@Override
	public TaskinfoDO get(Integer id){
		return taskinfoDao.get(id);
	}
	
	@Override
	public List<TaskinfoDO> list(Map<String, Object> map){
		return taskinfoDao.list(map);
	}
	
	@Override
	public int count(Map<String, Object> map){
		return taskinfoDao.count(map);
	}
	
	@Override
	@Transactional
	public R save(TaskinfoDO taskinfo){
		try {
		    logger.info("---- TaskinfoDO taskinfo----->>> "  +  JSONObject.toJSON(taskinfo)  + "<<<---------");
			Map<String,Object> configMap = Maps.newHashMap();
			List<ConfigDO> configDos = null;
			BigDecimal totalmoney = taskinfo.getPrice().multiply(new BigDecimal(taskinfo.getNum()));
			taskinfo.setTotalmoney(totalmoney);
			taskinfo.setStauts(1);
			// 获取结算方式
			configMap.put("key","settletype");
			configDos = configDao.list(configMap);
			// 设置任务结算方式
			taskinfo.setSettletype(Integer.parseInt(configDos.get(0).getValue()));

		/*	Map<String,Object> taskMap = Maps.newHashMap();
			taskMap.put("stauts","1");
			List<TaskinfoDO> taskinfoListdb = taskinfoDao.list(taskMap);*/
			// 1 是否有未开始的任务在排队
			taskinfoDao.save(taskinfo);
		//	if(taskinfoListdb.size() != 0){ // 进入排队状态
				taskinfo.setStauts(1);
		//	}
			 /*else {// 2 开始任务前，判断是否有足够的微信号, 冷却时间、当日上限数量、状态及绑定任务

				configMap.put("key","cdtime");
				configDos = configDao.list(configMap);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				Calendar nowTime = Calendar.getInstance();
				nowTime.add(Calendar.MINUTE, Integer.parseInt(configDos.get(0).getValue()));

				configMap.put("key","todaytaskquantity");
				configDos = configDao.list(configMap);
				Map<String,Object> wxMap = Maps.newHashMap();
				wxMap.put("lastdate",sdf.format(nowTime.getTime()));
				wxMap.put("todaytaskquantity",Integer.parseInt(configDos.get(0).getValue()));
				wxMap.put("limit",taskinfo.getNum()-taskinfo.getFinishnum());
				List<WechatDO> wechatListdb = wechatDao.list(wxMap);

				if(wechatListdb.size()==(taskinfo.getNum()-taskinfo.getFinishnum())){// 有足够的微信号，开始将微信号绑定到任务上
				    logger.info("------------>>>开始将微信号绑定到任务上<<<------------");
					for(int i=0;i<wechatListdb.size();i++){
						WechatDO wechatDO = wechatListdb.get(i);
						wechatDO.setTaskid(taskinfo.getId());
						wechatDao.update(wechatDO);
					}
					logger.info("------------->>>完成微信号与任务的绑定<<<---------------");
					// 绑定完任务，开始做任务 ----------------------------功能待开发-------------------------------
					int count = 0; //成功次数
                    if(taskinfo.getTasktype().equals(1)){//阅读
                        for(WechatDO wxid: wechatListdb) {

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
                    } else if(taskinfo.getTasktype().equals(2)) {//点赞
						for(WechatDO wxid: wechatListdb) {
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
                    } else if(taskinfo.getTasktype().equals(3)) {//关注
						for(WechatDO wxid: wechatListdb) {
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

					logger.info("------------->>>完成任务 解除微信号与任务的绑定,微信号开始计时冷却<<<---------------");
                    //------------ 任务结束，微信号开始计时冷却、执行任务数量累计、解除绑定 ，任务状态 ------------------
                    taskinfo.setStauts(5); //已完成
					taskinfo.setFinishnum(count);//完成数量
                    Date now = new Date();
                    for(int i=0;i<=wechatListdb.size();i++){
                        WechatDO wechatDO = wechatListdb.get(i);
                        wechatDO.setTaskid(null);  //解除任务绑定
                        wechatDO.setLastdate(now);  //更新最后一次执行任务时间
                        wechatDO.setTotaltaskquantity(wechatDO.getTotaltaskquantity()+1); //更新累计执行任务数量
                        wechatDO.setTodaytaskquantity(wechatDO.getTodaytaskquantity()+1); //更新当日累计执行任务数量
                        wechatDao.update(wechatDO);
                    }

				}


			}*/
			taskinfoDao.update(taskinfo);
			return R.ok();
		}catch (Exception e){
			e.printStackTrace();
			logger.error("com.bootdo.wx.service.impl.TaskinfoServiceImpl.save->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error();
		}
	}


	
	@Override
	public R update(TaskinfoDO taskinfo){
		try {
			// 查看任务状态
			TaskinfoDO taskinfodb = taskinfoDao.get(taskinfo.getId());
			if(taskinfodb.getStauts()!=1){
				return R.error(1, MsgUtil.getMsg(MessagesCode.ERROR_CODE_2001));
			}
			taskinfoDao.update(taskinfo);
		}catch (Exception e){
			e.printStackTrace();
			logger.error("com.bootdo.wx.service.impl.TaskinfoServiceImpl.update->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error();
		}
		return R.ok();
	}
	
	@Override
	public R remove(Integer id){
		try {
			// 查看任务状态
			TaskinfoDO taskinfodb = taskinfoDao.get(id);
			if(taskinfodb.getStauts()!=1){
				return R.error(1, MsgUtil.getMsg(MessagesCode.ERROR_CODE_2001));
			}
			taskinfoDao.remove(id);
		}catch (Exception e){
			e.printStackTrace();
			logger.error("com.bootdo.wx.service.impl.TaskinfoServiceImpl.remove->exception!message:{},cause:{},detail{}", e.getMessage(), e.getCause(), e.toString());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return R.error();
		}
		return R.ok();
	}
	
	@Override
	public int batchRemove(Integer[] ids){
		//return taskinfoDao.batchRemove(ids);
		return -1;
	}
	
}
