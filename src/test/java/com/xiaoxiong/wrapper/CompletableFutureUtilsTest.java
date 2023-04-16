package com.xiaoxiong.wrapper;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

class CompletableFutureUtilsTest {

    @Test
    void doParallelTask_test() throws ExecutionException, InterruptedException {
        List<String> strs = CompletableFutureUtils.doParallelTask(s -> "test");
        System.out.println(strs.size());
        System.out.println(strs.get(0));
    }

    @Test
    void function_test() {
        buildFunction().apply(null);
    }

    private static String getStr(String string) {
        return string;
    }

    private static <T, R> Function<T, R> buildFunction() {
        return new Function<T, R>() {
            @Override
            public R apply(T t) {
                System.out.println("apply");
                return null;
            }
        };
    }
}