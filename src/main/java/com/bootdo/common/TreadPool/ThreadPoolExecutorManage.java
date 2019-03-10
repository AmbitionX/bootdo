package com.bootdo.common.TreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @date 创建时间：2019年3月10日16:21:56
 * @version 1.0.0
 * @parameter
 * @throws
 * @return
 */
public class ThreadPoolExecutorManage {
    private static Logger logger= LoggerFactory.getLogger(ThreadPoolExecutorManage.class);
    //池中所保存的线程数，包括空闲线程。
    final static int corePoolSize = 5;
    //池中允许的最大线程数。
    final static int maximumPoolSize = 50;
    //当线程数大于核心线程时，此为终止前多余的空闲线程等待新任务的最长时间
    final static long keepAliveTime = 200;
    //执行前用于保持任务的队列5，即任务缓存队列
    final static ArrayBlockingQueue<Runnable> workQueue =new ArrayBlockingQueue<Runnable>(5);

    private static ThreadPoolExecutorManage pool;

    private static ThreadPoolExecutor threadPoolExecutor;

    private ThreadPoolExecutorManage(){
        threadPoolExecutor = getPool();
    }

    /**
     *	单例方法.
     *  @return 返回
     */
    public static synchronized ThreadPoolExecutorManage getInstance(){
        if(pool == null){
            pool = new ThreadPoolExecutorManage();
        }
        return pool;
    }

    private ThreadPoolExecutor getPool() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES,	workQueue);
        return threadPoolExecutor;
    }

    public boolean putTread(Runnable command){
        boolean bo = false;
        try{
            threadPoolExecutor.execute(command);
            bo = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        logger.info("线程池中现在的线程数目是："+threadPoolExecutor.getPoolSize()+",  队列中正在等待执行的任务数量为："+
                threadPoolExecutor.getQueue().size());
        return bo;
    }
}
