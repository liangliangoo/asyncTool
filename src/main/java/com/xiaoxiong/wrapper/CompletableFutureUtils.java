package com.xiaoxiong.wrapper;

import com.jd.platform.async.executor.Async;
import com.jd.platform.async.wrapper.WorkerWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Author 六月
 * @Date 2023/4/15 19:27
 * @Version 1.0
 */
public class CompletableFutureUtils<P, R> {

    private static final String prefix = "completableFutureName-";
    private static final Long TIMEOUT = 1000l;

    private static final ExecutorService executorService =  Executors.newCachedThreadPool();


    public static <P, R> List<R> doParallelTask(Function<R, P>... functions) throws ExecutionException, InterruptedException {
        if (functions.length == 0) {
            return new ArrayList<>();
        }
        Executors.newCachedThreadPool();
        List<WorkerWrapper> workerWrappers = new ArrayList<>();
        for (int i = 0; i < functions.length; i++) {
            workerWrappers.add(buildWorkWrapper(functions[i], String.valueOf(i)));
        }
        Async.beginWork(TIMEOUT, executorService, workerWrappers);
        return aggregationResults(workerWrappers);
    }

    private static <P> List<P> aggregationResults(List<WorkerWrapper> workerWrappers) {
        if (Objects.isNull(workerWrappers) || workerWrappers.size() == 0) {
            return new ArrayList<>();
        }
        List<P> result = new ArrayList<>();
        for (WorkerWrapper workerWrapper : workerWrappers) {
            P res = (P) workerWrapper.getWorkResult().getResult();
            result.add(res);
        }
        return result;
    }

    private static <P, R> WorkerWrapper buildWorkWrapper(Function<P, R> function, String workName) {
        return new WorkerWrapper.Builder<P, R>()
                .id(workName)
                .worker((params, allWrappers) -> {
                    System.out.println("test");
                   return function.apply(params);
                })
                .param(null)
                .build();
    }

    private static <P, R> WorkerWrapper buildWorkWrapper(Function<P, R> function, Consumer<R> callBack, String workName) {
        return new WorkerWrapper.Builder<P, R>()
                .id(workName)
                .worker((params, allWrappers) -> function.apply(params))
                .callback((success, param, workResult) -> {
                    if (success) {
                        callBack.accept(workResult.getResult());
                    }
                })
                .build();
    }

    private static String getWorkName(String suffix) {
        if (Objects.isNull(suffix) || suffix.length() == 0) {
            throw new RuntimeException("必须指定任后缀名称");
        }
        return prefix + suffix;
    }

}
