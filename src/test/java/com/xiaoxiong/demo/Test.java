package com.xiaoxiong.demo;

import com.jd.platform.async.executor.Async;
import com.jd.platform.async.wrapper.WorkerWrapper;

import java.util.concurrent.ExecutionException;

/**
 * @Author 六月
 * @Date 2023/4/14 18:04
 * @Version 1.0
 */
public class Test {

    @org.junit.jupiter.api.Test
    public void serial_test() throws ExecutionException, InterruptedException {
        MyWork myWork1 = new MyWork();
        MyWork myWork2 = new MyWork();
        WorkerWrapper<String, ResultVO> workerWrapper1 =  new WorkerWrapper.Builder<String, ResultVO>()
                .worker(myWork1)
                .param("mySerialWork1")
                .build();
        WorkerWrapper<String, ResultVO> workerWrapper2 =  new WorkerWrapper.Builder<String, ResultVO>()
                .worker(myWork2)
                .param("mySerialWork2")
                .build();

        Async.beginWork(1000000L, workerWrapper1, workerWrapper2);
        ResultVO result = workerWrapper1.getWorkResult().getResult();
        System.out.println("myWork1 result " + workerWrapper1.getWorkResult().getResult());
        System.out.println("myWork2 result " + workerWrapper2.getWorkResult().getResult());
        System.out.println("end");
    }

    @org.junit.jupiter.api.Test
    public void parallel_test() throws ExecutionException, InterruptedException {
        MyWork myWork1 = new MyWork();
        MyWork myWork2 = new MyWork();
        WorkerWrapper<String, ResultVO> workerWrapper1 =  new WorkerWrapper.Builder<String, ResultVO>()
                .worker(myWork1)
                .param("myParallelWork1")
                .build();
        WorkerWrapper<String, ResultVO> workerWrapper2 =  new WorkerWrapper.Builder<String, ResultVO>()
                .worker(myWork2)
                .param("myParallelWork2")
                .build();

        Async.beginWork(1000L, workerWrapper1, workerWrapper2);
    }

}
