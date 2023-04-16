package com.xiaoxiong.demo;

import com.jd.platform.async.callback.IWorker;
import com.jd.platform.async.wrapper.WorkerWrapper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author 六月
 * @Date 2023/4/14 18:00
 * @Version 1.0
 */
public class MyWork implements IWorker<String, ResultVO> {

    /**
     * 在这里做耗时操作，如rpc请求、IO等
     *
     * @param object      object
     * @param allWrappers 任务包装
     */
    @Override
    public ResultVO action(String object, Map<String, WorkerWrapper> allWrappers) {
        System.out.println("params " + object);
        ResultVO resultVO = new ResultVO();
        resultVO.setName(object);
        try {
            TimeUnit.SECONDS.sleep(5l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("action end " + object);
        return resultVO;
    }

    /**
     * 超时、异常时，返回的默认值
     *
     * @return 默认值
     */
    @Override
    public ResultVO defaultValue() {
        return IWorker.super.defaultValue();
    }

}
