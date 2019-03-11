package com.bootdo.common.TreadPool;
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
	
	public static boolean login62DataXl(List<String> wxdatas){
        boolean bo = false;
		if (wxdatas == null || wxdatas.size() == 0) {
			return false;
		}
		try{
			//回调新浪
	        Runnable r = new CallBackTread(wxdatas);
	        ThreadPoolExecutorManage.getInstance().putTread(r);
			bo = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return bo;
	}
}
