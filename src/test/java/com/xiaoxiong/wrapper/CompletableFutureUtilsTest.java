package com.xiaoxiong.wrapper;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

class CompletableFutureUtilsTest {

    @Test
    void doParallelTask_test() throws ExecutionException, InterruptedException {
        List<String> strs = CompletableFutureUtils.doParallelTask(Lists.newArrayList(s -> "test", s1 -> "test1", s2 -> "test2"), Lists.newArrayList("test", "test1", "test3"));
        System.out.println(strs.size());
        System.out.println(strs.get(0));
    }


    @Test
    void doParallelTask1_test() throws ExecutionException, InterruptedException {
        List<String> strs = CompletableFutureUtils.doParallelTask(Lists.newArrayList(() -> "hello supplier1", () -> "hello supplier2"));
        System.out.println(strs.size());
        System.out.println(JSON.toJSONString(strs));
    }


    @Test
    void function_test() {
        buildFunction().apply(null);
    }

    private static String getStr(String string) {
        return string;
    }

    private static <T, R> Function<T, R> buildFunction() {
        return t -> {
            System.out.println("apply " + Thread.currentThread().getName());
            return (R) (t + "\t" + Thread.currentThread().getName());
        };
    }
}