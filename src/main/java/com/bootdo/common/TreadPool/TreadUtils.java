package com.bootdo.common.TreadPool;
import com.alibaba.fastjson.JSONObject;
import com.bootdo.baseinfo.domain.WechatDO;
import com.bootdo.common.aspect.LogAspect;
import com.bootdo.wx.domain.TaskinfoDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/** 
 * 
 * @date 2019年3月10日16:16:17
 * @version 1.0.0 
 * @parameter  
 * @throws 
 * @return  
 */
public class TreadUtils {
    private static final Logger logger = LoggerFactory.getLogger(TreadUtils.class);
	public static boolean login62DataXl(List<String> wxdatas,String parseCode,String account){
        boolean bo = false;
		if (wxdatas == null || wxdatas.size() == 0) {
			return false;
		}
		try{
			//回调新浪
	        Runnable r = new CallBackTread(wxdatas,parseCode,account);
	        ThreadPoolExecutorManage.getInstance().putTread(r);
			bo = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return bo;
	}

	public static boolean taskRun(List<WechatDO> wechatListdb, TaskinfoDO taskinfo){
        logger.info("进入任务执行taskRun：------>>"+ JSONObject.toJSONString(taskinfo));
        boolean bo = false;
        if(wechatListdb==null || wechatListdb.size() == 0){
            return bo;
        }
        if(taskinfo == null){
            return bo;
        }
        try {
            Runnable r = new CallBackTask(wechatListdb,taskinfo);
            ThreadPoolExecutorManage.getInstance().putTread(r);
            bo = true;
        }catch (Exception e){
            logger.error("任务执行出错:------>>"+ JSONObject.toJSONString(e));
            e.printStackTrace();
        }

	    return bo;
    }
}
