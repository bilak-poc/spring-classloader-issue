package com.github.bilak.springclassloaderissue;

import com.github.bilak.springclassloaderissue.bootstrapping.CustomForkJoinWorkerThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

public final class ThreadPoolUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolUtil.class);

    private ThreadPoolUtil() {

    }

    public static void init() {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "30");
        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory",
                CustomForkJoinWorkerThreadFactory.class.getCanonicalName()
        );
        LOG.debug("ForkJoinPool parallelism: {}", ForkJoinPool.getCommonPoolParallelism());
        LOG.info(
                "ForkJoinPool has been instantiated with factory {}",
                ForkJoinPool.commonPool().getFactory()
        );

        LOG.info("TCCL {}", Thread.currentThread().getContextClassLoader());
        LOG.info("FKCL {}", ForkJoinPool.commonPool().getFactory().getClass().getClassLoader());
    }
}
