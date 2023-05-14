package com.xiaoxiong.wrapper;

import com.google.common.collect.Lists;
import com.jd.platform.async.executor.Async;
import com.jd.platform.async.wrapper.WorkerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Author 六月
 * @Date 2023/4/15 19:27
 * @Version 1.0
 */
public class CompletableFutureUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(CompletableFutureUtils.class);

    private static final String prefix = "completableFutureName-";
    private static final Long TIMEOUT = 2000l;

    private static final ExecutorService executorService =  Executors.newCachedThreadPool();

    public static <R> List<R> doParallelTask(List<Supplier<R>> functions) throws ExecutionException, InterruptedException {
        if (functions == null || functions.isEmpty()) {
            return Lists.newArrayList();
        }
        List<WorkerWrapper> workerWrappers = new ArrayList<>();
        for (int i = 0; i < functions.size(); i++) {
            workerWrappers.add(buildWorkWrapper(functions.get(i), String.valueOf(i)));
        }
        return getRunResult(workerWrappers);
    }

    public static <P, R> List<R> doParallelTask(List<Function<P, R>> functions, List<P> params) throws ExecutionException, InterruptedException {
        if (functions == null || functions.isEmpty()) {
            return new ArrayList<>();
        }
        List<WorkerWrapper> workerWrappers = new ArrayList<>();
        for (int i = 0; i < functions.size(); i++) {
            workerWrappers.add(buildWorkWrapper(functions.get(i), String.valueOf(i), params.get(i)));
        }
        return getRunResult(workerWrappers);
    }

    private static <R> List<R> getRunResult(List<WorkerWrapper> workerWrappers) throws ExecutionException, InterruptedException {
        Async.beginWork(TIMEOUT, executorService, workerWrappers);
        return aggregationResults(workerWrappers);
    }

    private static <P> List<P> aggregationResults(List<WorkerWrapper> workerWrappers) {
        if (Objects.isNull(workerWrappers) || workerWrappers.isEmpty()) {
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
                    System.out.println(params);
                    return function.apply(params);
                })
                .param(null)
                .build();
    }

    private static <P, R> WorkerWrapper buildWorkWrapper(Supplier<R> function, String workName) {
        return new WorkerWrapper.Builder<P, R>()
                .id(workName)
                .worker((params, allWrappers) -> function.get())
                .param(null)
                .build();
    }

    private static <P, R> WorkerWrapper buildWorkWrapper(Function<P, R> function, String workName, P param) {
        return new WorkerWrapper.Builder<P, R>()
                .id(workName)
                .worker((params, allWrappers) -> {
                    LOGGER.debug("buildWorkWrapper params {}", params);
                    return function.apply(params);
                })
                .param(param)
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
